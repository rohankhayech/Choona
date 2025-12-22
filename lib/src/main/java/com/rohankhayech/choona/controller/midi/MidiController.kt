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
package com.rohankhayech.choona.controller.midi

import kotlin.experimental.or
import com.rohankhayech.music.Notes
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.billthefarmer.mididriver.GeneralMidiConstants
import org.billthefarmer.mididriver.MidiConstants
import org.billthefarmer.mididriver.MidiDriver

/**
 * Controller class responsible for generating and sending MIDI events representing guitar notes.
 * @property numStrings The number of strings this controller can simultaneously play a note on.
 *
 * @author Rohan Khayech
 */
class MidiController(val numStrings: Int) {

    /** The system MIDI driver. */
    private var midiDriver: MidiDriver = MidiDriver.getInstance()

    /** Mutex for exclusive writing to the MIDI driver. */
    private var mutex = Mutex()

    /** Starts the midi controller and system driver. */
    fun start() {
        midiDriver.start()
    }

    /** Stops all playing notes and then stops the midi controller and system driver. */
    fun stop() {
        midiDriver.stop()
    }

    /**
     * Plays the specified note on the specified string for the specified duration.
     * @param string The position of the string on the tuning.
     * @param midiNote The MIDI note number to play.
     * @param duration The duration of the note, in ms.
     * @param instrument The MIDI instrument code to play.
     */
    suspend fun playNote(string: Int, midiNote: Int, duration: Long, instrument: Byte = GeneralMidiConstants.ELECTRIC_GUITAR_CLEAN) {
        // Set channel
        val channel = getChannel(string)

        // Play note.
        try {
            mutex.withLock {
                setInstrument(channel, instrument)

                // Send note on event
                val event = ByteArray(3)
                event[0] = MidiConstants.NOTE_ON or channel // Status byte and channel
                event[1] = midiNote.toByte() // Pitch (midi note number)
                event[2] = 0x7F.toByte() // Velocity
                midiDriver.write(event)
            }

            // Wait for duration of note.
            delay(duration)
        } finally {
            stopNote(string, midiNote)
        }
    }

    /**
     * Stops the currently playing note on the specified string.
     * @param string The position of the string on the tuning.
     */
    suspend fun stopNote(string: Int, midiNote: Int) {
        // Set channel
        val channel = getChannel(string)

        // Stop the note playing after duration or interrupted.
        // Send note off event.
        val event = ByteArray(3)
        event[0] = MidiConstants.NOTE_OFF or channel // Status byte and channel
        event[1] = midiNote.toByte() // Pitch (midi note number)
        event[2] = 0x00.toByte() // Velocity

        mutex.withLock {
            midiDriver.write(event)
        }
    }

    /**
     * Sets the instrument on the specified channel to the specified instrument.
     * @param channel The MIDI channel number.
     * @param instrument The MIDI instrument code to set.
     */
    private fun setInstrument(channel: Byte, instrument: Byte) {
        val event = ByteArray(2)
        event[0] = MidiConstants.PROGRAM_CHANGE or channel
        event[1] = instrument
        midiDriver.write(event)
    }

    /**
     * Gets the MIDI channel number for the string.
     */
    private fun getChannel(string: Int): Byte {
        return (if (string < 9) string else string + 1).toByte() // Channel 9 is reserved for percussion, skip it.
    }

    companion object {
        /**
         * Converts the specified note index to a MIDI note number.
         * @param noteIndex The internal note index.
         * @return The corresponding MIDI note number.
         */
        fun noteIndexToMidi(noteIndex: Int): Int {
            return noteIndex + Notes.A4_MIDI_NOTE_NUMBER
        }
    }
}