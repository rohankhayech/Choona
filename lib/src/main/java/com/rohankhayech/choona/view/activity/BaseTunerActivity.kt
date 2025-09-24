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

package com.rohankhayech.choona.view.activity

import java.io.IOException
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.lifecycleScope
import com.rohankhayech.choona.controller.midi.MidiController
import com.rohankhayech.choona.model.preferences.InitialTuningType
import com.rohankhayech.choona.model.preferences.TunerPreferences
import com.rohankhayech.choona.model.preferences.tunerPreferenceDataStore
import com.rohankhayech.choona.model.tuning.TuningEntry
import com.rohankhayech.choona.view.PermissionHandler
import com.rohankhayech.choona.view.viewmodel.TunerViewModel
import com.rohankhayech.music.Instrument
import com.rohankhayech.music.Tuning
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
    /** View model used to hold the current tuner state. */
    protected val vm: TunerViewModel by viewModels()

    /** Handler used to check and request microphone permission. */
    protected lateinit var ph: PermissionHandler

    /** MIDI controller used to play guitar notes. */
    protected lateinit var midi: MidiController

    /** User preferences for the tuner. */
    protected lateinit var prefs: Flow<TunerPreferences>

    /** Callback used to dismiss tuning selection screen when the back button is pressed. */
    private lateinit var dismissTuningSelectorOnBack: OnBackPressedCallback

    /** Callback used to dismiss configure tuning panel when the back button is pressed. */
    private lateinit var dismissConfigurePanelOnBack: OnBackPressedCallback

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
        midi = MidiController(vm.tuner.tuning.value.numStrings())

        // Load tunings
        lifecycleScope.launch {
            val firstLoad = vm.tuningList.loadTunings(this@BaseTunerActivity)

            // Initialize edit mode and initial tuning state from preferences only on app load.
            if (firstLoad) prefs.firstOrNull()?.let { preferences ->
                vm.setEditMode(preferences.editModeDefault)

                // Switch to initial tuning
                when(preferences.initialTuning) {
                    InitialTuningType.PINNED -> when (vm.tuningList.pinned.value) {
                        is TuningEntry.InstrumentTuning -> setTuning(vm.tuningList.pinned.value.tuning!!)
                        is TuningEntry.ChromaticTuning -> vm.tuner.setChromatic(true)
                    }
                    InitialTuningType.LAST_USED -> vm.tuningList.lastUsed.value?.let {
                        when (it) {
                            is TuningEntry.InstrumentTuning -> setTuning(it.tuning)
                            is TuningEntry.ChromaticTuning -> vm.tuner.setChromatic(true)
                        }
                    }
                }
            }
        }

        // Setup custom back navigation.
        dismissConfigurePanelOnBack = onBackPressedDispatcher.addCallback(this,
            enabled = vm.configurePanelOpen.value,
        ) {
            dismissConfigurePanel()
        }

        dismissTuningSelectorOnBack = onBackPressedDispatcher.addCallback(this,
            enabled = vm.tuningSelectorOpen.value
        ) {
            dismissTuningSelector()
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
        if (!vm.tuningSelectorOpen.value && !vm.configurePanelOpen.value) {
            try {
                vm.tuner.start(ph)
            } catch (_: Exception) {
                // Catch and ignore, error will be displayed in the UI.
            }
        }
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
    private fun playStringSelectSound(string: Int) {
        midi.playNote(
            string,
            MidiController.noteIndexToMidi(vm.tuner.tuning.value.getString(string).rootNoteIndex),
            150,
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
        lifecycleScope.launch {
            if (prefs.first().enableStringSelectSound) playNoteSelectSound(noteIndex)
        }
    }

    /** Plays the note selection sound for the specified [noteIndex]. */
    private fun playNoteSelectSound(noteIndex: Int) {
        midi.playNote(
            0,
            MidiController.noteIndexToMidi(noteIndex),
            150,
            Instrument.GUITAR.midiInstrument
        )
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
    private fun playInTuneSound() {
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
        dismissConfigurePanelOnBack.isEnabled = true
        vm.openConfigurePanel()
        vm.tuner.stop()
    }

    /**
     * Opens the tuning selection screen, and stops the tuner.
     */
    protected fun openTuningSelector() {
        dismissTuningSelectorOnBack.isEnabled = true
        vm.openTuningSelector()
        vm.tuner.stop()
    }

    /**
     * Dismisses the tuning selection screen and restarts the tuner if no other panel is open.
     */
    protected fun dismissTuningSelector() {
        dismissTuningSelectorOnBack.isEnabled = false
        vm.dismissTuningSelector()
        if (!vm.configurePanelOpen.value) {
            try {
                vm.tuner.start(ph)
            } catch(_: Exception) {}
        }
    }

    /** Dismisses the configure panel and restarts the tuner if no other panel is open. */
    protected fun dismissConfigurePanel() {
        dismissConfigurePanelOnBack.isEnabled = false
        vm.dismissConfigurePanel()
        if (!vm.tuningSelectorOpen.value) {
            try {
                vm.tuner.start(ph)
            } catch (_: Exception) {}
        }
    }

    /**
     * Sets the current tuning to the [tuning] selected on the tuning
     * selection screen, restarts the tuner if no other panel is open,
     * and recreates the MIDI driver if necessary.
     */
    protected fun selectTuning(tuning: Tuning) {
        // Consume back stack entry.
        dismissTuningSelectorOnBack.isEnabled = false

        // Recreate MIDI driver if number of strings different.
        checkAndRecreateMidiDriver(tuning)

        // Select the tuning.
        vm.selectTuning(tuning)

        // Start tuner if no other panel is open.
        if (!vm.configurePanelOpen.value) {
            try {
                vm.tuner.start(ph)
            } catch(_: Exception) {}
        }
    }

    /**
     * Sets chromatic mode on as selected on the tuning selection screen
     * and restarts the tuner if no other panel is open.
     */
    protected open fun selectChromatic() {
        // Consume back stack entry.
        dismissTuningSelectorOnBack.isEnabled = false

        // Select the tuning.
        vm.selectChromatic()

        // Start tuner if no other panel is open.
        if (!vm.configurePanelOpen.value) {
            try {
                vm.tuner.start(ph)
            } catch(_: Exception) {}
        }
    }

    /**
     * Sets the current tuning to the [tuning] specified,
     * and recreates the MIDI driver if necessary.
     */
    protected fun setTuning(tuning: Tuning) {
        // Recreate MIDI driver if number of strings different.
        checkAndRecreateMidiDriver(tuning)

        // Select the tuning.
        vm.tuner.setTuning(tuning)
    }

    /**
     * Recreates the MIDI driver when the number of strings
     * in the new tuning is different from the current tuning.
     *
     * @param newTuning The new selected tuning.
     */
    private fun checkAndRecreateMidiDriver(newTuning: Tuning) {
        if (newTuning.numStrings() != vm.tuner.tuning.value.numStrings()) {
            midi.stop()
            midi = MidiController(newTuning.numStrings())
            midi.start()
        }
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
}