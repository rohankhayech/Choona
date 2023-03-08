/*
 * Copyright (c) 2023 Rohan Khayech
 */

package com.rohankhayech.choona.controller.midi;

import com.rohankhayech.music.Notes;

import org.billthefarmer.mididriver.GeneralMidiConstants;
import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;

/**
 * Controller class responsible for generating and sending MIDI events representing guitar notes.
 *
 * @author Rohan Khayech
 */
public final class MidiController {

    private MidiDriver midiDriver;

    /** Array of threads for each string. Used to wait for the duration of a note. */
    private Thread[] stringThread;

    /** Array of mutexes for each string thread. */
    private Object[] stringMutex;

    /**
     * Constructs a new MIDI controller.
     * @param numStrings The number of strings this controller can simultaneously play a note on.
     */
    public MidiController(int numStrings) {
        init(numStrings);
    }

    private void init(int numStrings) {
        midiDriver = MidiDriver.getInstance();
        midiDriver.start();

        // Setup string thread array.
        stringThread = new Thread[numStrings];
        stringMutex = new Object[numStrings];

        for (int i=0; i<numStrings; i++) {
            // Setup channels
            // Set channel instrument.
            setInstrument(i, GeneralMidiConstants.ELECTRIC_GUITAR_CLEAN);

            // Setup string thread.
            stringThread[i] = null;
            stringMutex[i] = new Object();
        }
    }

    /**
     * Plays the specified note on the specified string for the specified duration.
     * @param string The position of the string on the tuning.
     * @param midiNote The MIDI note number to play.
     * @param duration The duration of the note, in ms.
     */
    public void playNote(int string, int midiNote, long duration) {
        playNote(string, midiNote, duration, GeneralMidiConstants.ELECTRIC_GUITAR_CLEAN);
    }

    /**
     * Plays the specified note on the specified string for the specified duration.
     * @param string The position of the string on the tuning.
     * @param midiNote The MIDI note number to play.
     * @param duration The duration of the note, in ms.
     * @param instrument The MIDI instrument code to play.
     */
    public void playNote(int string, int midiNote, long duration, byte instrument) {
        stopNote(string);

        // Play note.
        synchronized (stringMutex[string]) {
            stringThread[string] = new Thread(() -> {
                try {
                    setInstrument(string, instrument);

                    // Send note on event
                    byte[] event = new byte[3];
                    event[0] = (byte) (MidiConstants.NOTE_ON | (byte) string); // Status byte and channel
                    event[1] = (byte) midiNote; // Pitch (midi note number)
                    event[2] = (byte) 0x7F; // Velocity
                    midiDriver.write(event);

                    // Wait for duration of note.
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    // Cancel thread sleep.
                } finally {
                    synchronized (stringMutex[string]) {
                        // Stop the note playing after duration or interrupted.
                        // Send note off event.
                        byte[] event = new byte[3];
                        event[0] = (byte) (MidiConstants.NOTE_OFF | (byte) string); // Status byte and channel
                        event[1] = (byte) midiNote; // Pitch (midi note number)
                        event[2] = (byte) 0x00; // Velocity
                        midiDriver.write(event);

                        // Clean up thread.
                        stringThread[string] = null;
                    }
                }
            }, "string_thread_" + string);

            stringThread[string].start();
        }
    }

    /**
     * Stops the currently playing note on the specified string.
     * @param string The position of the string on the tuning.
     */
    public void stopNote(int string) {
        if (isNotePlaying(string)) {
            stringThread[string].interrupt();
        }
    }

    /**
     * Converts the specified note index to a MIDI note number.
     * @param noteIndex The internal note index.
     * @return The corresponding MIDI note number.
     */
    public static int noteIndexToMidi(int noteIndex) {
        return noteIndex+Notes.A4_MIDI_NOTE_NUMBER;
    }

    /**
     * Sets the instrument on the specified channel to the specified instrument.
     * @param channel The MIDI channel number.
     * @param instrument The MIDI instrument code to set.
     */
    private void setInstrument(int channel, byte instrument) {
        byte[] event = new byte[2];
        event[0] = (byte) (MidiConstants.PROGRAM_CHANGE | (byte)channel);
        event[1] = instrument;
        midiDriver.write(event);
    }

    private boolean isNotePlaying(int string) {
        return stringThread[string] != null;
    }

}