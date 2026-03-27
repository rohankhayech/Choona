/*
 * Choona - Guitar Tuner
 * Copyright (C) 2026 Rohan Khayech
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

package com.rohankhayech.choona.lib.view.activity

import java.io.IOException
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.lifecycleScope
import com.rohankhayech.choona.lib.controller.midi.MidiController
import com.rohankhayech.choona.lib.model.preferences.InitialTuningType
import com.rohankhayech.choona.lib.model.preferences.TunerPreferences
import com.rohankhayech.choona.lib.model.preferences.tunerPreferenceDataStore
import com.rohankhayech.choona.lib.model.tuning.Instrument
import com.rohankhayech.choona.lib.model.tuning.Tuning
import com.rohankhayech.choona.lib.model.tuning.TuningEntry
import com.rohankhayech.choona.lib.view.PermissionHandler
import com.rohankhayech.choona.lib.view.viewmodel.TunerViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.billthefarmer.mididriver.GeneralMidiConstants

/**
 * Activity that allows the user to select a tuning and tune their guitar, displaying a comparison of played notes
 * and the correct notes of the strings in the tuning.
 *
 * @author Rohan Khayech
 */
abstract class BaseTunerActivity : ComponentActivity() {
    companion object {
        /** Activity intent extra for the launched tuning. */
        const val EXTRA_LAUNCHED_TUNING = "launched_tuning"
    }

    /** View model used to hold the current tuner state. */
    protected val vm: TunerViewModel by viewModels()

    /** Handler used to check and request microphone permission. */
    protected lateinit var ph: PermissionHandler

    /** MIDI controller used to play guitar notes. */
    protected lateinit var midi: MidiController

    /** User preferences for the tuner. */
    protected lateinit var prefs: Flow<TunerPreferences>

    /**
     * Called when activity is created.
     * 
     * Sets up preferences, permission handler, and MIDI controller.
     * Loads tunings, and initializes edit mode and initial tuning state from preferences only on app load.
     * Keeps the screen on while tuning.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup preferences
        prefs = tunerPreferenceDataStore.data
            .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
            .map(TunerPreferences::fromAndroidPreferences)

        // Setup permission handler.
        ph = PermissionHandler(this, Manifest.permission.RECORD_AUDIO)

        // Setup MIDI controller for note playback.
        midi = MidiController()

        // Load tunings
        lifecycleScope.launch {
            val firstLoad = vm.tuningList.loadTunings(this@BaseTunerActivity)

            // Initialize edit mode and initial tuning state from preferences only on app load.
            if (firstLoad) prefs.firstOrNull()?.let { preferences ->
                vm.setEditMode(preferences.editModeDefault)

                // Switch to initial tuning
                if (!intent.hasExtra(EXTRA_LAUNCHED_TUNING)) {
                    when (preferences.initialTuning) {
                        InitialTuningType.PINNED -> when (vm.tuningList.pinned.value) {
                            is TuningEntry.InstrumentTuning -> vm.tuner.setTuning(vm.tuningList.pinned.value.tuning!!)
                            is TuningEntry.ChromaticTuning -> vm.tuner.setChromatic(true)
                        }

                        InitialTuningType.LAST_USED -> vm.tuningList.lastUsed.value?.let {
                            when (it) {
                                is TuningEntry.InstrumentTuning -> vm.tuner.setTuning(it.tuning)
                                is TuningEntry.ChromaticTuning -> vm.tuner.setChromatic(true)
                            }
                        }
                    }
                }
            }
        }

        // Keep the screen on while tuning.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    /**
     * Called when the activity resumes after being paused.
     *
     * Starts the MIDI driver, and the tuner if no panels are open.
     */
    override fun onResume() {
        // Call superclass.
        super.onResume()

        // Start midi driver.
        midi.start()

        // Start the tuner if no panels are open.
        checkAndStartTuner()
    }

    /**
     * Called when the activity is paused but still visible.
     *
     * Stops the tuner and MIDI driver.
     */
    override fun onPause() {
        // Stop the tuner.
        vm.tuner.stop()

        // Stop midi driver.
        midi.stop()

        // Call superclass.
        super.onPause()
    }

    /**
     * Called when the activity is no longer visible.
     *
     * Saves the tunings in the tuning list.
     */
    override fun onStop() {
        // Save tunings.
        vm.tuningList.saveTunings(this)

        // Call superclass
        super.onStop()
    }

    /**
     * Selects the [nth][n] string in the tuning for comparison,
     * and plays the string selection sound if enabled.
     */
    protected fun selectString(n: Int) {
        // Select the string
        vm.tuner.selectString(n)

        // Play sound on string selection.
        lifecycleScope.launch {
            if (prefs.first().enableStringSelectSound) playStringSelectSound(n)
        }
    }

    /** Plays the string selection sound for the specified [string]. */
    private suspend fun playStringSelectSound(string: Int) {
        midi.playNote(
            string,
            MidiController.noteIndexToMidi(vm.tuner.tuning.value.getString(string).rootNoteIndex),
            NOTE_SELECT_SOUND_DURATION,
            vm.tuner.tuning.value.instrument.midiInstrument
        )
    }

    /**
     * Selects the note to tune to in chromatic mode,
     * and plays the note selection sound if enabled.
     */
    protected fun selectNote(noteIndex: Int) {
        // Select the note
        vm.tuner.selectNote(noteIndex)

        // Play sound on string selection.
        playNoteSelectSound(noteIndex)
    }

    /** Plays the note selection sound for the specified [noteIndex] if enabled. */
    private fun playNoteSelectSound(noteIndex: Int) {
        playNote(noteIndex, Instrument.GUITAR)
    }

    /** Plays the specified note index with the specified instrument if enabled. */
    protected fun playNote(noteIndex: Int, instrument: Instrument) {
        lifecycleScope.launch {
            if (prefs.first().enableStringSelectSound) {
                midi.playNote(
                    0,
                    MidiController.noteIndexToMidi(noteIndex),
                    NOTE_SELECT_SOUND_DURATION,
                    instrument.midiInstrument
                )
            }
        }
    }

    /**
     * Sets the current string/note as tuned,
     * and plays the in tune sound if enabled.
     */
    protected fun setTuned() {
        // Set the string/note as tuned.
        vm.tuner.setTuned()

        // Play sound when string tuned.
        lifecycleScope.launch {
            if (prefs.first().enableInTuneSound) playInTuneSound()
        }
    }

    /** Plays the in tune sound for the selected string. */
    private suspend fun playInTuneSound() {
        val string = if (vm.tuner.chromatic.value) 0 else vm.tuner.selectedString.value
        val noteIndex = if (vm.tuner.chromatic.value) vm.tuner.selectedNote.value
        else vm.tuner.tuning.value.getString(string).rootNoteIndex

        midi.playNote(
            string,
            MidiController.noteIndexToMidi(noteIndex) + 12,
            50,
            GeneralMidiConstants.MARIMBA
        )
    }

    /**
     * Opens the configure tuning panel, and stops the tuner.
     */
    protected fun openConfigurePanel() {
        vm.openConfigurePanel()
        checkAndStopTuner()
    }

    /**
     * Opens the tuning selection screen, and stops the tuner.
     */
    protected fun openTuningSelector() {
        vm.openTuningSelector()
        checkAndStopTuner()
    }

    /**
     * Navigates back and restarts the tuner if no other panel is open.
     */
    protected fun navBack() {
        vm.navBack()

        // Start tuner if no other panel is open.
        checkAndStartTuner()
    }

    /**
     * Sets the current tuning to the [tuning] selected on the tuning
     * selection screen and restarts the tuner if no other panel is open.
     */
    protected fun selectTuningFromList(tuning: Tuning) {
        // Select the tuning.
        vm.selectTuningFromList(tuning)

        // Start tuner if no other panel is open.
        checkAndStartTuner()
    }

    /**
     * Sets chromatic mode on as selected on the tuning selection screen
     * and restarts the tuner if no other panel is open.
     */
    protected fun selectChromaticFromList() {
        // Select the tuning.
        vm.selectChromaticFromList()

        // Start tuner if no other panel is open.
        checkAndStartTuner()
    }

    /** Starts tuner if no other panel is open above it. */
    protected fun checkAndStartTuner() {
        if (vm.isTunerScreenOpen()) {
            try {
                vm.tuner.start(ph)
            } catch(_: Exception) {}
        }
    }

    /** Stops tuner if a panel is open above it. */
    protected fun checkAndStopTuner() {
        if (!vm.isTunerScreenOpen()) vm.tuner.stop()
    }

    /** Opens the permission settings screen in the device settings. */
    protected fun openPermissionSettings() {
        startActivity(
            Intent(
                ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            )
        )
    }

    companion object {
        private const val NOTE_SELECT_SOUND_DURATION = 150L
    }
}