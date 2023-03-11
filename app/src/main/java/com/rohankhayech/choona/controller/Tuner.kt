/*
 * Copyright (c) 2023 Rohan Khayech
 */

package com.rohankhayech.choona.controller

import kotlin.math.abs
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.AMDF
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchDetectionResult
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

    /** Whether the tuner is currently running. */
    private var running = false

    /** Audio dispatcher used to receive incoming audio data.  */
    private var dispatcher: AudioDispatcher? = null

    /** Audio processor used to determine pitch of incoming audio data.  */
    private val pitchProcessor: AudioProcessor

    init {
        // Setup pitch processor.
        val pdh = PitchDetectionHandler { result, _ -> processPitch(result) }
        pitchProcessor = PitchProcessor(
            AMDF(
                SAMPLE_RATE.toFloat(),
                AUDIO_BUFFER_SIZE,
                Notes.getPitch(LOWEST_NOTE),
                Notes.getPitch(HIGHEST_NOTE)
            ),
            pdh
        )
    }

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
     * Sets the [tuned] value of the [nth][n] string.
     */
    fun setTuned(n: Int = selectedString.value, tuned: Boolean = true) {
        require(n in 0 until tuning.value.numStrings()) { "Invalid string index." }
        _tuned.update { old -> old.clone().also { it[n] = tuned } }
    }

    /** Sets whether the tuner will automatically detect the currently playing string. */
    fun setAutoDetect(on: Boolean) {
        _autoDetect.update { on }
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

            // Detect closest string.
            if (autoDetect.value) {
                _selectedString.update {
                    tuning.value.getStringNum(
                        tuning.value.minBy { abs(it.getNoteIndex(0) - notePlaying) }
                    )
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
        val str = tuning.value.getString(_selectedString.value)
        return notePlaying - str.getNoteIndex(0)
    }

    /**
     * Starts listening to incoming audio and begins note comparison.
     * This process must be stopped by calling [stop] when finished.
     *
     * @param ph The Android runtime permission handler.
     * @throws IllegalStateException If the RECORD_AUDIO permission is not granted, or the tuner has already started.
     */
    @Throws(IllegalStateException::class)
    fun start(ph: PermissionHandler) {
        if (!running) {
            val granted = ph.checkPermAndPerform {
                running = true

                // Create audio dispatcher from default microphone.
                dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, AUDIO_BUFFER_SIZE, 0)
                dispatcher?.addAudioProcessor(pitchProcessor)

                // Start the audio dispatcher (producer) thread.
                Thread(dispatcher, "audio-dispatcher").start()
            }
            check(granted) {"RECORD_AUDIO permission not granted."}
        } else {
            throw IllegalStateException("Tuner already started")
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