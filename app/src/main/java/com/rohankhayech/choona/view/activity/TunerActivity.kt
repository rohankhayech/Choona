/*
 * Copyright (c) 2023 Rohan Khayech
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
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.rohankhayech.choona.controller.Tuner
import com.rohankhayech.choona.controller.midi.MidiController
import com.rohankhayech.choona.model.preferences.TunerPreferences
import com.rohankhayech.choona.model.preferences.tunerPreferenceDataStore
import com.rohankhayech.choona.model.tuning.TuningList
import com.rohankhayech.choona.view.PermissionHandler
import com.rohankhayech.choona.view.screens.TunerPermissionScreen
import com.rohankhayech.choona.view.screens.TunerScreen
import com.rohankhayech.choona.view.screens.TuningSelectionScreen
import com.rohankhayech.choona.view.theme.AppTheme
import com.rohankhayech.music.Tuning
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.billthefarmer.mididriver.GeneralMidiConstants
import org.billthefarmer.mididriver.MidiDriver

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
                    val favTunings = vm.tuningList.favourites.collectAsStateWithLifecycle()
                    val customTunings = vm.tuningList.custom.collectAsStateWithLifecycle()

                    // Display UI content.
                    AnimatedVisibility(!tuningSelectorOpen,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        TunerScreen(
                            windowSizeClass = calculateWindowSizeClass(this@TunerActivity),
                            tuning = tuning,
                            noteOffset = noteOffset,
                            selectedString = selectedString,
                            tuned = tuned,
                            autoDetect = autoDetect,
                            favTunings = favTunings,
                            customTunings = customTunings,
                            prefs = prefs,
                            onSelectString = remember(prefs.enableStringSelectSound) {
                                {
                                    vm.tuner.selectString(it)
                                    // Play sound on string selection.
                                    if (prefs.enableStringSelectSound) playStringSelectSound(it)
                                }
                            },
                            onSelectTuning = vm.tuner::setTuning,
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
                            onBackPressed = ::finish,
                            onSettingsPressed = ::openSettings
                        )

                    }
                    AnimatedVisibility(tuningSelectorOpen,
                        enter = slideInVertically { it/2 },
                        exit = slideOutVertically { it }
                    ) {
                        TuningSelectionScreen(
                            tuningList = vm.tuningList,
                            onSelect = ::selectTuning,
                            onDismiss = ::dismissTuningSelector,
                        )
                    }
                } else {
                    // Audio permission not granted, show permission rationale.
                    TunerPermissionScreen(
                        requestAgain = !shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO),
                        onBackPressed = {},
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

        checkPermission()
        // Start the tuner.
        if (!vm.tuningSelectorOpen.value) {
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

        // Save tunings.
        vm.tuningList.saveTunings(this)

        // Call superclass.
        super.onPause()
    }

    /** Called before the activity is destroyed. */
    override fun onDestroy() {
        // Close midi driver.
        MidiDriver.getInstance().stop()

        // Call superclass
        super.onDestroy()
    }

    /** Plays the string selection sound for the specified [string]. */
    private fun playStringSelectSound(string: Int) {
        midi.playNote(
            string,
            MidiController.noteIndexToMidi(vm.tuner.tuning.value.getString(string).rootNoteIndex),
            150
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
     * Opens the tuning selection screen, and stops the tuner.
     */
    private fun openTuningSelector() {
        dismissTuningSelectorOnBack.isEnabled = true
        vm.openTuningSelector()
        vm.tuner.stop()
    }

    /**
     * Dismisses the tuning selection screen and restarts the tuner.
     */
    private fun dismissTuningSelector() {
        dismissTuningSelectorOnBack.isEnabled = false
        vm.dismissTuningSelector()
        try {
            vm.tuner.start(ph)
        } catch(_: IllegalStateException) {}
    }

    /**
     * Sets the current tuning to the tuning selected on the tuning selection screen and restarts the tuner.
     */
    private fun selectTuning(tuning: Tuning) {
        dismissTuningSelectorOnBack.isEnabled = false
        vm.selectTuning(tuning)
        try {
            vm.tuner.start(ph)
        } catch (_: IllegalStateException) {}
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
    val tuningList = TuningList()

    /** Mutable backing property for [tuningSelectorOpen]. */
    private val _tuningSelectorOpen = MutableStateFlow(false)

    /** Whether the tuning selection screen is currently open. */
    val tuningSelectorOpen = _tuningSelectorOpen.asStateFlow()

    /** Opens the tuning selection screen. */
    fun openTuningSelector() {
        tuningList.setCurrent(tuner.tuning.value)
        _tuningSelectorOpen.update { true }
    }

    /** Dismisses the tuning selection screen. */
    fun dismissTuningSelector() {
        _tuningSelectorOpen.update { false }
    }

    /** Sets the current tuning to that selected in the tuning selection screen and dismisses it. */
    fun selectTuning(tuning: Tuning) {
        _tuningSelectorOpen.update { false }
        tuner.setTuning(tuning)
    }
}