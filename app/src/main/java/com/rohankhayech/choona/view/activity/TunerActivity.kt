/*
 * Choona - Guitar Tuner
 * Copyright (C) 2023 Rohan Khayech
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
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.rohankhayech.choona.controller.midi.MidiController
import com.rohankhayech.choona.controller.tuner.Tuner
import com.rohankhayech.choona.model.preferences.TunerPreferences
import com.rohankhayech.choona.model.preferences.tunerPreferenceDataStore
import com.rohankhayech.choona.model.tuning.TuningList
import com.rohankhayech.choona.view.PermissionHandler
import com.rohankhayech.choona.view.screens.MainLayout
import com.rohankhayech.choona.view.screens.TunerPermissionScreen
import com.rohankhayech.choona.view.theme.AppTheme
import com.rohankhayech.music.Tuning
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.billthefarmer.mididriver.GeneralMidiConstants

/**
 * Activity that allows the user to select a tuning and tune their guitar, displaying a comparison of played notes
 * and the correct notes of the strings in the tuning.
 *
 * @author Rohan Khayech
 */
class TunerActivity : AppCompatActivity() {

    /** View model used to hold the current tuner state. */
    private val vm: TunerActivityViewModel by viewModels()

    /** Handler used to check and request microphone permission. */
    private lateinit var ph: PermissionHandler

    /** MIDI controller used to play guitar notes. */
    private lateinit var midi: MidiController

    /** Whether the RECORD_AUDIO permission has been granted. */
    private val permGranted = MutableStateFlow(false)

    /** User preferences for the tuner. */
    private lateinit var prefs: Flow<TunerPreferences>

    /** Callback used to dismiss tuning selection screen when the back button is pressed. */
    private lateinit var dismissTuningSelectorOnBack: OnBackPressedCallback

    /** Callback used to dismiss configure tuning panel when the back button is pressed. */
    private lateinit var dismissConfigurePanelOnBack: OnBackPressedCallback

    /**
     * Called when activity is created.
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup preferences
        prefs = tunerPreferenceDataStore.data
            .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
            .map(TunerPreferences::fromAndroidPreferences)

        // Setup permission handler.
        ph = PermissionHandler(this, Manifest.permission.RECORD_AUDIO, object : PermissionHandler.PermissionCallback {
            override fun showInContextUI() {
                permGranted.update { false }
            }

            override fun onPermissionDenied() {
                permGranted.update { false }
            }
        })
        requestPermission()

        // Setup MIDI controller for note playback.
        midi = MidiController(vm.tuner.tuning.value.numStrings())

        // Load tunings
        lifecycleScope.launch {
            vm.tuningList.loadTunings(this@TunerActivity)
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

        // Set UI content.
        setContent {
            val prefs by prefs.collectAsStateWithLifecycle(initialValue = TunerPreferences())

            AppTheme(fullBlack = prefs.useBlackTheme) {
                val granted by permGranted.collectAsStateWithLifecycle()
                if (granted) {
                    // Collect state.
                    val tuning by vm.tuner.tuning.collectAsStateWithLifecycle()
                    val noteOffset = vm.tuner.noteOffset.collectAsStateWithLifecycle()
                    val selectedString by vm.tuner.selectedString.collectAsStateWithLifecycle()
                    val autoDetect by vm.tuner.autoDetect.collectAsStateWithLifecycle()
                    val tuned by vm.tuner.tuned.collectAsStateWithLifecycle()
                    val tuningSelectorOpen by vm.tuningSelectorOpen.collectAsStateWithLifecycle()
                    val configurePanelOpen by vm.configurePanelOpen.collectAsStateWithLifecycle()
                    val favTunings = vm.tuningList.favourites.collectAsStateWithLifecycle()
                    val customTunings = vm.tuningList.custom.collectAsStateWithLifecycle()

                    // Calculate window size/orientation
                    val windowSizeClass = calculateWindowSizeClass(this)

                    val compact = remember(windowSizeClass) {
                        windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact &&
                            windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
                    }

                    val expanded = remember(windowSizeClass) {
                        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded &&
                            windowSizeClass.heightSizeClass > WindowHeightSizeClass.Compact
                    }

                    // Dismiss configure panel if no longer compact.
                    LaunchedEffect(compact, configurePanelOpen) {
                        if (configurePanelOpen && !compact) dismissConfigurePanel()
                    }

                    // Dismiss tuning selector when switching to expanded view.
                    LaunchedEffect(expanded, tuningSelectorOpen) {
                        if (tuningSelectorOpen && expanded) dismissTuningSelector()
                    }

                    // Display UI content.
                    MainLayout(
                        windowSizeClass = windowSizeClass,
                        compact = compact,
                        expanded = expanded,
                        tuning = tuning,
                        noteOffset = noteOffset,
                        selectedString = selectedString,
                        tuned = tuned,
                        autoDetect = autoDetect,
                        favTunings = favTunings,
                        customTunings = customTunings,
                        prefs = prefs,
                        tuningList = vm.tuningList,
                        tuningSelectorOpen = tuningSelectorOpen,
                        configurePanelOpen = configurePanelOpen,
                        onSelectString = remember(prefs.enableStringSelectSound) {
                            {
                                vm.tuner.selectString(it)
                                // Play sound on string selection.
                                if (prefs.enableStringSelectSound) playStringSelectSound(it)
                            }
                        },
                        onSelectTuning = ::setTuning,
                        onTuneUpString = vm.tuner::tuneStringUp,
                        onTuneDownString = vm.tuner::tuneStringDown,
                        onTuneUpTuning = vm.tuner::tuneUp,
                        onTuneDownTuning = vm.tuner::tuneDown,
                        onAutoChanged = vm.tuner::setAutoDetect,
                        onTuned = remember(prefs.enableInTuneSound) {
                            {
                                vm.tuner.setTuned()
                                // Play sound when string tuned.
                                if (prefs.enableInTuneSound) playInTuneSound()
                            }
                        },
                        onOpenTuningSelector = ::openTuningSelector,
                        onSettingsPressed = ::openSettings,
                        onConfigurePressed = ::openConfigurePanel,
                        onSelectTuningFromList = ::selectTuning,
                        onDismissTuningSelector = ::dismissTuningSelector,
                        onDismissConfigurePanel = ::dismissConfigurePanel
                    )
                } else {
                    // Audio permission not granted, show permission rationale.
                    TunerPermissionScreen(
                        fullBlack = prefs.useBlackTheme,
                        requestAgain = shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO),
                        onSettingsPressed = ::openSettings,
                        onRequestPermission = ::requestPermission,
                        onOpenPermissionSettings = ::openPermissionSettings,
                    )
                }
            }
        }

        // Keep the screen on while tuning.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    /** Called when the activity resumes after being paused. */
    override fun onResume() {
        // Call superclass.
        super.onResume()

        // Start midi driver.
        midi.start()

        checkPermission()
        // Start the tuner if no panels are open.
        if (!vm.tuningSelectorOpen.value && !vm.configurePanelOpen.value) {
            try {
                vm.tuner.start(ph)
            } catch (_: IllegalStateException) {
            }
        }
    }

    /** Called when the activity is paused but still visible. */
    override fun onPause() {
        // Stop the tuner.
        vm.tuner.stop()

        // Stop midi driver.
        midi.stop()

        // Save tunings.
        vm.tuningList.saveTunings(this)

        // Call superclass.
        super.onPause()
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

    /** Plays the in tune sound for the selected string. */
    private fun playInTuneSound() {
        val string = vm.tuner.selectedString.value
        midi.playNote(
            string,
            MidiController.noteIndexToMidi(vm.tuner.tuning.value.getString(string).rootNoteIndex) + 12,
            50,
            GeneralMidiConstants.MARIMBA
        )
    }

    /**
     * Opens the configure tuning panel, and stops the tuner.
     */
    private fun openConfigurePanel() {
        dismissConfigurePanelOnBack.isEnabled = true
        vm.openConfigurePanel()
        vm.tuner.stop()
    }

    /**
     * Opens the tuning selection screen, and stops the tuner.
     */
    private fun openTuningSelector() {
        dismissTuningSelectorOnBack.isEnabled = true
        vm.openTuningSelector()
        vm.tuner.stop()
    }

    /**
     * Dismisses the tuning selection screen and restarts the tuner if no other panel is open.
     */
    private fun dismissTuningSelector() {
        dismissTuningSelectorOnBack.isEnabled = false
        vm.dismissTuningSelector()
        if (!vm.configurePanelOpen.value) {
            try {
                vm.tuner.start(ph)
            } catch(_: IllegalStateException) {}
        }
    }

    /** Dismisses the configure panel and restarts the tuner if no other panel is open. */
    private fun dismissConfigurePanel() {
        dismissConfigurePanelOnBack.isEnabled = false
        vm.dismissConfigurePanel()
        if (!vm.tuningSelectorOpen.value) {
            try {
                vm.tuner.start(ph)
            } catch (_: IllegalStateException) {}
        }
    }

    /**
     * Sets the current tuning to the [tuning] selected on the tuning
     * selection screen, restarts the tuner if no other panel is open,
     * and recreates the MIDI driver if necessary.
     */
    private fun selectTuning(tuning: Tuning) {
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
            } catch(_: IllegalStateException) {}
        }
    }

    /**
     * Sets the current tuning to the [tuning] specified,
     * and recreates the MIDI driver if necessary.
     */
    private fun setTuning(tuning: Tuning) {
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

    /** Opens the tuner settings activity. */
    private fun openSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    /**
     * Requests the audio permission.
     */
    private fun requestPermission() {
        ph.requestPermAndPerform {
            permGranted.update { true }
        }
    }

    /** Checks for audio permission. */
    private fun checkPermission() {
        ph.checkPermAndPerform {
            permGranted.update { true }
        }
    }

    /** Opens the permission settings screen in the device settings. */
    private fun openPermissionSettings() {
        startActivity(
            Intent(
                ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            )
        )
    }
}

/** View model used to hold the current tuner and UI state. */
class TunerActivityViewModel : ViewModel() {
    /** Tuner used for audio processing and note comparison. */
    val tuner = Tuner()

    /** State holder containing the lists of favourite and custom tunings. */
    val tuningList = TuningList(tuner.tuning.value)

    /** Mutable backing property for [tuningSelectorOpen]. */
    private val _tuningSelectorOpen = MutableStateFlow(false)

    /** Whether the tuning selection screen is currently open. */
    val tuningSelectorOpen = _tuningSelectorOpen.asStateFlow()

    /** Mutable backing property for [configurePanelOpen]. */
    private val _configurePanelOpen = MutableStateFlow(false)

    /**
     * Whether the configure tuning panel is currently open.
     */
    val configurePanelOpen = _configurePanelOpen.asStateFlow()

    /** Runs when the view model is instantiated. */
    init {
        // Update tuner when the current selection in the tuning list is updated.
        viewModelScope.launch {
            tuner.tuning.collect {
                tuningList.setCurrent(it)
            }
        }
        // Update the tuning list when the tuner's tuning is updated.
        viewModelScope.launch {
            tuningList.current.collect {
                it?.let { tuner.setTuning(it) }
            }
        }
    }

    /** Opens the tuning selection screen. */
    fun openTuningSelector() {
        _tuningSelectorOpen.update { true }
    }

    /**
     * Opens the configure tuning panel.
     */
    fun openConfigurePanel() {
        _configurePanelOpen.update { true }
    }

    /** Dismisses the tuning selection screen. */
    fun dismissTuningSelector() {
        _tuningSelectorOpen.update { false }
    }

    /**
     * Dismisses the configure tuning panel.
     */
    fun dismissConfigurePanel() {
        _configurePanelOpen.update { false }
    }

    /** Sets the current tuning to that selected in the tuning selection screen and dismisses it. */
    fun selectTuning(tuning: Tuning) {
        _tuningSelectorOpen.update { false }
        tuner.setTuning(tuning)
    }
}