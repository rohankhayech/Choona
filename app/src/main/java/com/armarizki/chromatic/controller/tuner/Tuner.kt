/*
 * ChromaticTuner - Arma Rizki
 *
 * Includes modified source code from Choona Guitar Tuner
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

package com.armarizki.chromatic.controller.tuner

import kotlin.math.abs
import kotlin.math.roundToInt
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.AMDF
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchDetectionResult
import com.armarizki.chromatic.controller.tuner.Tuner.Companion.HIGHEST_NOTE
import com.armarizki.chromatic.controller.tuner.Tuner.Companion.LOWEST_NOTE
import com.armarizki.chromatic.model.error.TunerException
import com.armarizki.chromatic.view.PermissionHandler
import com.armarizki.music.Notes
import com.armarizki.music.Tuning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class Tuner(
    tuning: Tuning = Tuning.STANDARD
) {

    companion object {

         
        const val TUNED_OFFSET_THRESHOLD = 0.05

         
        const val TUNED_SUSTAIN_TIME = 900

        // Audio Dispatcher Constants
         
        private const val SAMPLE_RATE = 44100

         
        private const val AUDIO_BUFFER_SIZE = 2048

         
        val LOWEST_NOTE = Notes.getIndex("D1")

         
        val HIGHEST_NOTE = Notes.getIndex("B4")
    }

     
    private val _tuning = MutableStateFlow(tuning)

     
    val tuning = _tuning.asStateFlow()

     
    private val _selectedString = MutableStateFlow(0)

     
    val selectedString = _selectedString.asStateFlow()

     
    private val _selectedNote = MutableStateFlow(Notes.getIndex("E2"))

     
    val selectedNote = _selectedNote.asStateFlow()

     
    private val _noteOffset = MutableStateFlow<Double?>(null)

     
    val noteOffset = _noteOffset.asStateFlow()

     
    private val _autoDetect = MutableStateFlow(true)

     
    val autoDetect = _autoDetect.asStateFlow()

     
    private val _tuned = MutableStateFlow(BooleanArray(tuning.numStrings()) { false })

     
    val tuned = _tuned.asStateFlow()

     
    private val _noteTuned = MutableStateFlow(false)

     
    val noteTuned = _noteTuned.asStateFlow()

     
    private val _chromatic = MutableStateFlow(false)

     
    val chromatic = _chromatic.asStateFlow()

     
    private var running = false

     
    private var dispatcher: AudioDispatcher? = null

     
    private val _error = MutableStateFlow<Exception?>(null)

     
    private val _a4Pitch = MutableStateFlow(440.0)
    val a4Pitch = _a4Pitch.asStateFlow()

    fun setA4Pitch(value: Double) {
        _a4Pitch.update { value }
    }


     
    val error = _error.asStateFlow()

     
    fun selectString(n: Int) {
        require(n in 0 until tuning.value.numStrings()) { "Invalid string index." }
        _selectedString.update { n }
        _autoDetect.update { false }
    }

     
    fun setTuning(tuning: Tuning) {
        if (chromatic.value) {
            setChromatic(false)
        }
        _tuning.update {
            updateTunedStatus(it, tuning)
            tuning
        }
        if (selectedString.value >= tuning.numStrings()) {
            _selectedString.update { tuning.numStrings() - 1 }
        }
    }

     
    fun tuneUp(): Boolean {
        return if (tuning.value.max().rootNoteIndex < HIGHEST_NOTE) {
            _tuning.update { it.higherTuning() }
            _tuned.update { BooleanArray(tuning.value.numStrings()) { false } }
            true
        } else false
    }

     
    fun tuneDown(): Boolean {
        return if (tuning.value.min().rootNoteIndex > LOWEST_NOTE) {
            _tuning.update { it.lowerTuning() }
            _tuned.update { BooleanArray(tuning.value.numStrings()) { false } }
            true
        } else false
    }

     
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

     
    fun selectNote(noteIndex: Int) {
        require(noteIndex in LOWEST_NOTE..HIGHEST_NOTE) { "Invalid note index." }
        if (selectedNote.value != noteIndex) {
            _noteTuned.update { false }
        }
        _selectedNote.update { noteIndex }
        _autoDetect.update { false }
    }

     
    fun setTuned(n: Int = selectedString.value, tuned: Boolean = true) {
        require(n in 0 until tuning.value.numStrings()) { "Invalid string index." }
        if (chromatic.value) {
            _noteTuned.update { tuned }
        } else {
            _tuned.update { old -> old.clone().also { it[n] = tuned } }
        }
    }

     
    fun setAutoDetect(on: Boolean) {
        _autoDetect.update { on }
    }

     
    fun setChromatic(on: Boolean = true) {
        // Reset tuned state.
        _tuned.update { BooleanArray(tuning.value.numStrings()) { false } }
        _noteTuned.update { false }

        // Set chromatic or instrument mode
        _chromatic.update { on }
    }

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

    fun processPitch(result: PitchDetectionResult) {
        if (result.isPitched) {
            val a4 = _a4Pitch.value
            val notePlaying = Notes.getOffsetFromA4(result.pitch.toDouble(), a4)

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

     
    private fun calcNoteOffset(notePlaying: Double): Double {
        val noteIndex = if (chromatic.value) {
            selectedNote.value
        } else {
            val str = tuning.value.getString(selectedString.value)
            str.getNoteIndex(0)
        }
        return notePlaying - noteIndex
    }

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
                val a4 = _a4Pitch.value
                val pitchProcessor = PitchProcessor(
                    AMDF(
                        sampleRate.toFloat(),
                        bufferSize,
                        Notes.getPitch(LOWEST_NOTE, a4),
                        Notes.getPitch(HIGHEST_NOTE, a4)
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

     
    fun stop() {
        if (running) {
            running = false

            // Stop the audio dispatcher.
            dispatcher!!.stop()
            dispatcher = null
        }
    }
}