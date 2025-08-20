/*
 * Choona - Guitar Tuner
 * Copyright (C) 2025 Rohan Khayech
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.rohankhayech.choona.controller.tuner

import kotlin.math.abs
import kotlin.math.roundToInt
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.AMDF
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchDetectionResult
import com.rohankhayech.choona.controller.tuner.Tuner.Companion.HIGHEST_NOTE
import com.rohankhayech.choona.controller.tuner.Tuner.Companion.LOWEST_NOTE
import com.rohankhayech.choona.model.error.TunerException
import com.rohankhayech.choona.view.PermissionHandler
import com.rohankhayech.music.Notes
import com.rohankhayech.music.Tuning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * The Guitar Tuner class is responsible for listening to incoming microphone audio
 * and comparing the currently playing note with the strings in the tuning.
 *
 * @param tuning The guitar tuning used for comparison.
 *
 * @author Rohan Khayech
 */
class Tuner(
    tuning: Tuning = Tuning.STANDARD
) {

    companion object {

        /** Threshold in semitones that note offset must be below to be considered in tune. */
        const val TUNED_OFFSET_THRESHOLD = 0.15

        /** Time in ms that a note must be held below the threshold for before being considered in tune. */
        const val TUNED_SUSTAIN_TIME = 900

        // Audio Dispatcher Constants
        /** Microphone sample rate. */
        private const val SAMPLE_RATE = 22050

        /** Audio buffer size. */
        private const val AUDIO_BUFFER_SIZE = 1024

        /** Index of the lowest detectable note. */
        val LOWEST_NOTE = Notes.getIndex("D1")

        /** Index of the highest detectable note. */
        val HIGHEST_NOTE = Notes.getIndex("B4")
    }

    /** Mutable backing property for [tuning]. */
    private val _tuning = MutableStateFlow(tuning)

    /** Guitar tuning used for comparison. */
    val tuning = _tuning.asStateFlow()

    /** Mutable backing property for [selectedString]. */
    private val _selectedString = MutableStateFlow(0)

    /** Index of the currently selected string within the tuning. */
    val selectedString = _selectedString.asStateFlow()

    /** Mutable backing property for [selectedNote]. */
    private val _selectedNote = MutableStateFlow(Notes.getIndex("E2"))

    /** The index of the currently selected note. */
    val selectedNote = _selectedNote.asStateFlow()

    /** Mutable backing property for [noteOffset] */
    private val _noteOffset = MutableStateFlow<Double?>(null)

    /** The offset between the currently playing note and the selected string. */
    val noteOffset = _noteOffset.asStateFlow()

    /** Mutable backing property for [autoDetect]. */
    private val _autoDetect = MutableStateFlow(true)

    /** Whether the tuner will automatically detect the currently playing string. */
    val autoDetect = _autoDetect.asStateFlow()

    /** Mutable backing property for [tuned]. */
    private val _tuned = MutableStateFlow(BooleanArray(tuning.numStrings()) { false })

    /** Whether each string has been tuned. */
    val tuned = _tuned.asStateFlow()

    /** Mutable backing property for [noteTuned]. */
    private val _noteTuned = MutableStateFlow(false)

    /** Whether the currently playing chromatic note is tuned. */
    val noteTuned = _noteTuned.asStateFlow()

    /** Mutable backing property for [chromatic]. */
    private val _chromatic = MutableStateFlow(true)

    /** Whether the tuner is currently in chromatic mode, or instrument mode. */
    val chromatic = _chromatic.asStateFlow()

    /** Whether the tuner is currently running. */
    private var running = false

    /** Audio dispatcher used to receive incoming audio data.  */
    private var dispatcher: AudioDispatcher? = null

    /** Mutable backing property for [error]. */
    private val _error = MutableStateFlow<Exception?>(null)

    /** Error preventing the tuner from running. `null` if no error has occurred. */
    val error = _error.asStateFlow()

    /** Selects the [nth][n] string in the tuning for comparison. */
    fun selectString(n: Int) {
        require(n in 0 until tuning.value.numStrings()) { "Invalid string index." }
        _selectedString.update { n }
        _autoDetect.update { false }
    }

    /** Sets the guitar tuning for comparison. */
    fun setTuning(tuning: Tuning) {
        _tuning.update {
            updateTunedStatus(it, tuning)
            tuning
        }
        if (selectedString.value >= tuning.numStrings()) {
            _selectedString.update { tuning.numStrings() - 1 }
        }
    }

    /** Tunes all strings in the tuning up by one semitone */
    fun tuneUp(): Boolean {
        return if (tuning.value.max().rootNoteIndex < HIGHEST_NOTE) {
            _tuning.update { it.higherTuning() }
            _tuned.update { BooleanArray(tuning.value.numStrings()) { false } }
            true
        } else false
    }

    /** Tunes all strings in the tuning down by one semitone */
    fun tuneDown(): Boolean {
        return if (tuning.value.min().rootNoteIndex > LOWEST_NOTE) {
            _tuning.update { it.lowerTuning() }
            _tuned.update { BooleanArray(tuning.value.numStrings()) { false } }
            true
        } else false
    }

    /** Tunes the [nth][n] string in the tuning up by one semitone.
     * @return False if the string could not be tuned any lower, true otherwise.
     */
    fun tuneStringUp(n: Int): Boolean {
        require(n in 0 until tuning.value.numStrings()) { "Invalid string index." }

        return if (tuning.value.getString(n).rootNoteIndex < HIGHEST_NOTE) {
            _tuning.update { tuning ->
                tuning.withString(n, tuning.getString(n).higherString())
            }
            setTuned(n, false)
            true
        } else false
    }

    /**
     * Tunes the [nth][n] string in the tuning down by one semitone.
     * @return False if the string could not be tuned any lower, true otherwise.
     */
    fun tuneStringDown(n: Int): Boolean {
        require(n in 0 until tuning.value.numStrings()) { "Invalid string index." }

        return if (tuning.value.getString(n).rootNoteIndex > LOWEST_NOTE) {
            _tuning.update { tuning ->
                tuning.withString(n, tuning.getString(n).lowerString())
            }
            setTuned(n, false)
            true
        } else false
    }

    /**
     * Selects the note to tune to in chromatic mode.
     * @param noteIndex The index of the note to select, must be between [LOWEST_NOTE] and [HIGHEST_NOTE].
     */
    fun selectNote(noteIndex: Int) {
        require(noteIndex in LOWEST_NOTE..HIGHEST_NOTE) { "Invalid note index." }
        if (selectedNote.value != noteIndex) {
            _noteTuned.update { false }
        }
        _selectedNote.update { noteIndex }
        _autoDetect.update { false }
    }

    /**
     * Sets the [tuned] value of the [nth][n] string.
     */
    fun setTuned(n: Int = selectedString.value, tuned: Boolean = true) {
        require(n in 0 until tuning.value.numStrings()) { "Invalid string index." }
        if (chromatic.value) {
            _noteTuned.update { tuned }
        } else {
            _tuned.update { old -> old.clone().also { it[n] = tuned } }
        }
    }

    /** Sets whether the tuner will automatically detect the currently playing string. */
    fun setAutoDetect(on: Boolean) {
        _autoDetect.update { on }
    }

    /** Sets whether the tuner is in chromatic mode or instrument mode. */
    fun setChromatic(on: Boolean) {
        // Reset tuned state.
        _tuned.update { BooleanArray(tuning.value.numStrings()) { false } }
        _noteTuned.update { false }

        // Set chromatic or instrument mode
        _chromatic.update { on }
    }

    /**
     * Checks if any strings in the [new][newTuning] tuning are the same as in the [old][oldTuning] tuning and allows them to keep their tuned status.
     */
    private fun updateTunedStatus(oldTuning: Tuning, newTuning: Tuning) {
        _tuned.update {
            if (oldTuning.numStrings() != newTuning.numStrings()) {
                BooleanArray(newTuning.numStrings()) { false }
            } else {
                it.clone().also { newArr ->
                    for (i in 0 until newTuning.numStrings()) {
                        if (newTuning.getString(i) != oldTuning.getString(i)) {
                            newArr[i] = false
                        }
                    }
                }
            }
        }
    }

    /**
     * Processes incoming pitch detection [results][result] and updates the offset
     * between the currently playing note and the root note of the selected string.
     */
    fun processPitch(result: PitchDetectionResult) {
        if (result.isPitched) {
            // Calc note playing.
            val notePlaying = Notes.getOffsetFromA4(result.pitch.toDouble())

            // Detect closest string/note in auto mode.
            if (autoDetect.value) {
                if (chromatic.value) {
                    val closestNote = notePlaying.roundToInt()
                    if (selectedNote.value != closestNote) {
                        _noteTuned.update { false }
                    }
                    _selectedNote.update { closestNote }
                } else {
                    _selectedString.update {
                        tuning.value.getStringNum(
                            tuning.value.minBy { abs(it.getNoteIndex(0) - notePlaying) }
                        )
                    }
                }

            }

            // Update note offset.
            _noteOffset.update { calcNoteOffset(notePlaying) }
        } else {
            _noteOffset.update { null }
        }
    }

    /** Returns the offset between the specified [note][notePlaying] and the root note of the selected string. */
    private fun calcNoteOffset(notePlaying: Double): Double {
        val noteIndex = if (chromatic.value) {
            selectedNote.value
        } else {
            val str = tuning.value.getString(selectedString.value)
            str.getNoteIndex(0)
        }
        return notePlaying - noteIndex
    }

    /**
     * Starts listening to incoming audio and begins note comparison.
     * This process must be stopped by calling [stop] when finished.
     *
     * @param ph The Android runtime permission handler.
     * @throws IllegalStateException If the RECORD_AUDIO permission is not granted, or the tuner has already started.
     * @throws TunerException If the tuner fails to start due to another error.
     *                        The [error] property will also be set with this exception.
     */
    @Throws(IllegalStateException::class, TunerException::class)
    fun start(ph: PermissionHandler) {
        check(!running) { "Tuner already started." }
        check(ph.check()) { "RECORD_AUDIO permission not granted." }

        running = true
        _error.update { null }

        var bufferSize = AUDIO_BUFFER_SIZE
        var sampleRate = SAMPLE_RATE
        while (dispatcher == null) {
            try {
                // Create audio dispatcher from default microphone.
                dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, 0)

                // Setup and add pitch processor.
                val pdh = PitchDetectionHandler { result, _ -> processPitch(result) }
                val pitchProcessor = PitchProcessor(
                    AMDF(
                        sampleRate.toFloat(),
                        bufferSize,
                        Notes.getPitch(LOWEST_NOTE),
                        Notes.getPitch(HIGHEST_NOTE)
                    ),
                    pdh
                )
                dispatcher?.addAudioProcessor(pitchProcessor)

                // Start the audio dispatcher (producer) thread.
                Thread(dispatcher, "audio-dispatcher").start()
            } catch(e: Exception) {
                    // Extract the required buffer size from the exception message.
                    val requiredBufferSize = e.message?.substringAfter("should be at least ")?.substringBefore("\n")?.trim()?.toIntOrNull()

                    if (requiredBufferSize == null || requiredBufferSize == bufferSize) {
                        // If we have tried the device's required buffer size and still failed, throw an exception.
                        val err = TunerException(e.message, e)
                        _error.update { err }
                        running = false
                        throw err
                    } else {
                        bufferSize = requiredBufferSize
                        sampleRate = (SAMPLE_RATE.toFloat() * (bufferSize.toFloat() / AUDIO_BUFFER_SIZE.toFloat())).roundToInt()
                    }
            }
        }
    }

    /** Stops listening to incoming audio and note comparison. */
    fun stop() {
        if (running) {
            running = false

            // Stop the audio dispatcher.
            dispatcher!!.stop()
            dispatcher = null
        }
    }
}