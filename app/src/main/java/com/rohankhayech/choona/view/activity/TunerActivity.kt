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
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.rohankhayech.choona.R
import com.rohankhayech.choona.controller.midi.MidiController
import com.rohankhayech.choona.controller.tuner.Tuner
import com.rohankhayech.choona.model.preferences.InitialTuningType
import com.rohankhayech.choona.model.preferences.TunerPreferences
import com.rohankhayech.choona.model.preferences.TunerPreferences.Companion.REVIEW_PROMPT_ATTEMPTS
import com.rohankhayech.choona.model.preferences.tunerPreferenceDataStore
import com.rohankhayech.choona.model.tuning.TuningEntry
import com.rohankhayech.choona.model.tuning.TuningList
import com.rohankhayech.choona.view.PermissionHandler
import com.rohankhayech.choona.view.screens.MainLayout
import com.rohankhayech.choona.view.screens.TunerErrorScreen
import com.rohankhayech.choona.view.screens.TunerPermissionScreen
import com.rohankhayech.choona.view.theme.AppTheme
import com.rohankhayech.music.Instrument
import com.rohankhayech.music.Tuning
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.billthefarmer.mididriver.GeneralMidiConstants

/**
 * Activity that allows the user to select a tuning and tune their guitar, displaying a comparison of played notes
 * and the correct notes of the strings in the tuning.
 *
 * @author Rohan Khayech
 */
class TunerActivity : ComponentActivity() {

    /** View model used to hold the current tuner state. */
    private val vm: TunerActivityViewModel by viewModels()

    /** Handler used to check and request microphone permission. */
    private lateinit var ph: PermissionHandler

    /** MIDI controller used to play guitar notes. */
    private lateinit var midi: MidiController

    /** User preferences for the tuner. */
    private lateinit var prefs: Flow<TunerPreferences>

    /** Callback used to dismiss tuning selection screen when the back button is pressed. */
    private lateinit var dismissTuningSelectorOnBack: OnBackPressedCallback

    /** Callback used to dismiss configure tuning panel when the back button is pressed. */
    private lateinit var dismissConfigurePanelOnBack: OnBackPressedCallback

    /** Google Play review manager. */
    private lateinit var manager: ReviewManager

    /**
     * Called when activity is created.
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            enableEdgeToEdge()
        }

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
            val firstLoad = vm.tuningList.loadTunings(this@TunerActivity)

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

        manager = ReviewManagerFactory.create(this)

        // Set UI content.
        setContent {
            val prefs by prefs.collectAsStateWithLifecycle(initialValue = TunerPreferences())

            AppTheme(fullBlack = prefs.useBlackTheme, dynamicColor = prefs.useDynamicColor) {
                val granted by ph.granted.collectAsStateWithLifecycle()
                val error by vm.tuner.error.collectAsStateWithLifecycle()
                if (granted && error == null) {
                    // Collect state.
                    val tuning by vm.tuner.tuning.collectAsStateWithLifecycle()
                    val noteOffset = vm.tuner.noteOffset.collectAsStateWithLifecycle()
                    val selectedString by vm.tuner.selectedString.collectAsStateWithLifecycle()
                    val selectedNote by vm.tuner.selectedNote.collectAsStateWithLifecycle()
                    val autoDetect by vm.tuner.autoDetect.collectAsStateWithLifecycle()
                    val chromatic by vm.tuner.chromatic.collectAsStateWithLifecycle()
                    val tuned by vm.tuner.tuned.collectAsStateWithLifecycle()
                    val noteTuned by vm.tuner.noteTuned.collectAsStateWithLifecycle()
                    val tuningSelectorOpen by vm.tuningSelectorOpen.collectAsStateWithLifecycle()
                    val configurePanelOpen by vm.configurePanelOpen.collectAsStateWithLifecycle()
                    val favTunings = vm.tuningList.favourites.collectAsStateWithLifecycle()
                    val editModeEnabled by vm.editModeEnabled.collectAsStateWithLifecycle()

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

                    // Launch review prompt after tuning if conditions met.
                    var askedForReview by rememberSaveable { mutableStateOf(false) }
                    LaunchedEffect(tuned, askedForReview, prefs.showReviewPrompt, prefs.reviewPromptLaunches) {
                        if (
                            !askedForReview  // Only ask once per app session.
                            && prefs.showReviewPrompt  // Do not ask if user has disabled.
                            && prefs.reviewPromptLaunches < REVIEW_PROMPT_ATTEMPTS // Only ask a maximum of 3 times.
                            && tuned.all { it } // Only ask once all strings are in tune, as the user is likely finished using the app and satisfied.
                            && Math.random() < REVIEW_PROMPT_CHANCE // Only ask 30% of the time.
                        ) {
                            delay(1000)
                            launchReviewPrompt()
                            askedForReview = true
                        }
                    }

                    // Display UI content.
                    MainLayout(
                        windowSizeClass = windowSizeClass,
                        compact = compact,
                        expanded = expanded,
                        tuning = if (chromatic) TuningEntry.ChromaticTuning else TuningEntry.InstrumentTuning(tuning),
                        noteOffset = noteOffset,
                        selectedString = selectedString,
                        selectedNote = selectedNote,
                        tuned = tuned,
                        noteTuned = noteTuned,
                        autoDetect = autoDetect,
                        chromatic = chromatic,
                        favTunings = favTunings,
                        getCanonicalName = {
                            vm.tuningList.run {
                                this@MainLayout.getCanonicalName()
                            }
                        },
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
                        onSelectChromatic = { vm.tuner.setChromatic(true) },
                        onSelectNote = remember(prefs.enableStringSelectSound) {
                            {
                                vm.tuner.selectNote(it)
                                // Play sound on string selection.
                                if (prefs.enableStringSelectSound) playNoteSelectSound(it)
                            }
                        },
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
                        onSelectChromaticFromList = ::selectChromatic,
                        onDismissTuningSelector = ::dismissTuningSelector,
                        onDismissConfigurePanel = ::dismissConfigurePanel,
                        onEditModeChanged = vm::setEditMode,
                        editModeEnabled = editModeEnabled
                    )
                } else if (!granted) {
                    // Audio permission not granted, show permission rationale.
                    val firstRequest by ph.firstRequest.collectAsStateWithLifecycle()
                    TunerPermissionScreen(
                        canRequest = firstRequest,
                        onSettingsPressed = ::openSettings,
                        onRequestPermission = ph::request,
                        onOpenPermissionSettings = ::openPermissionSettings,
                    )
                } else {
                    TunerErrorScreen(error, ::openSettings)
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

        // Start the tuner if no panels are open.
        if (!vm.tuningSelectorOpen.value && !vm.configurePanelOpen.value) {
            try {
                vm.tuner.start(ph)
            } catch (_: Exception) {
                // Catch and ignore, error will be displayed in the UI.
            }
        }
    }

    /** Called when the activity is paused but still visible. */
    override fun onPause() {
        // Stop the tuner.
        vm.tuner.stop()

        // Stop midi driver.
        midi.stop()

        // Call superclass.
        super.onPause()
    }

    /** Called when the activity is no longer visible. */
    override fun onStop() {
        // Save tunings.
        vm.tuningList.saveTunings(this)

        // Call superclass
        super.onStop()
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

    /** Plays the note selection sound for the specified [noteIndex]. */
    private fun playNoteSelectSound(noteIndex: Int) {
        midi.playNote(
            0,
            MidiController.noteIndexToMidi(noteIndex),
            150,
            Instrument.GUITAR.midiInstrument
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
            } catch(_: Exception) {}
        }
    }

    /** Dismisses the configure panel and restarts the tuner if no other panel is open. */
    private fun dismissConfigurePanel() {
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
            } catch(_: Exception) {}
        }
    }

    /**
     * Sets chromatic mode on as selected on the tuning selection screen
     * and restarts the tuner if no other panel is open.
     */
    private fun selectChromatic() {
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
        val pinnedName = when (val current = vm.tuningList.current.value) {
            is TuningEntry.InstrumentTuning -> current.tuning.fullName
            is TuningEntry.ChromaticTuning -> getString(R.string.chromatic)
            null -> null
        }

        val intent = Intent(this, SettingsActivity::class.java)
        intent.putExtra(SettingsActivity.EXTRA_PINNED, pinnedName)
        startActivity(intent)
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

    /** Launches a prompt for the user to review the app on Google Play. */
    private fun launchReviewPrompt() {
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                manager.launchReviewFlow(this, reviewInfo)
                    .addOnCompleteListener {
                        // Increment launches counter.
                        lifecycleScope.launch {
                            tunerPreferenceDataStore.edit { prefs ->
                                prefs[TunerPreferences.REVIEW_PROMPT_LAUNCHES_KEY] = (
                                    (prefs[TunerPreferences.REVIEW_PROMPT_LAUNCHES_KEY]?.toIntOrNull() ?: 0)
                                        + 1
                                ).toString()
                            }
                        }

                    }
            }
        }
    }
}

/** View model used to hold the current tuner and UI state. */
@VisibleForTesting
class TunerActivityViewModel : ViewModel() {
    /** Tuner used for audio processing and note comparison. */
    val tuner = Tuner()

    /** State holder containing the lists of favourite and custom tunings. */
    val tuningList = TuningList(tuner.tuning.value, viewModelScope)

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

    /** Mutable backing property for [editModeEnabled]. */
    private val _editModeEnabled = MutableStateFlow(false)

    /** Whether the edit mode is currently enabled. */
    val editModeEnabled = _editModeEnabled.asStateFlow()

    /** Sets the edit mode state. */
    fun setEditMode(enabled: Boolean) {
        _editModeEnabled.update { enabled }
    }

    /** Runs when the view model is instantiated. */
    init {
        // Update the tuning list when the tuner's tuning is updated.
        viewModelScope.launch {
            tuner.tuning.collect {
                tuningList.setCurrent(TuningEntry.InstrumentTuning(it))
            }
        }
        viewModelScope.launch {
            tuner.chromatic.collect { chromatic ->
                if (chromatic) {
                    tuningList.setCurrent(TuningEntry.ChromaticTuning)
                } else {
                    // If switching back to the same instrument tuning, the tuning flow above will not emit, so update here.
                    tuningList.setCurrent(TuningEntry.InstrumentTuning(tuner.tuning.value))
                }
            }
        }

        // Update tuner when the current selection in the tuning list is updated.
        viewModelScope.launch {
            tuningList.current.collect {
                it?.let {
                    when (it) {
                        is TuningEntry.InstrumentTuning -> tuner.setTuning(it.tuning)
                        is TuningEntry.ChromaticTuning -> tuner.setChromatic(true)
                    }
                }
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

    /** Sets the current tuning to chromatic as selected in the tuning selection screen and dismisses it. */
    fun selectChromatic() {
        _tuningSelectorOpen.update { false }
        tuner.setChromatic(true)
    }
}

/** Probability of showing the review prompt once all strings are in tune. */
private const val REVIEW_PROMPT_CHANCE = 0.3