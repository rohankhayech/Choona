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

package com.armarizki.chromatic.view.activity

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
import androidx.compose.runtime.remember
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.armarizki.chromatic.R
import com.armarizki.chromatic.controller.play.ReviewController
import com.armarizki.chromatic.controller.play.ReviewControllerImpl
import com.armarizki.chromatic.controller.tone.ToneGenerator
import com.armarizki.chromatic.controller.tuner.Tuner
import com.armarizki.chromatic.model.preferences.InitialTuningType
import com.armarizki.chromatic.model.preferences.TunerPreferences
import com.armarizki.chromatic.model.preferences.tunerPreferenceDataStore
import com.armarizki.chromatic.model.tuning.TuningEntry
import com.armarizki.chromatic.model.tuning.TuningList
import com.armarizki.chromatic.view.PermissionHandler
import com.armarizki.chromatic.view.screens.MainLayout
import com.armarizki.chromatic.view.screens.TunerErrorScreen
import com.armarizki.chromatic.view.screens.TunerPermissionScreen
import com.armarizki.chromatic.view.theme.AppTheme
import com.armarizki.music.Notes
import com.armarizki.music.Tuning
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException

class TunerActivity : ComponentActivity() {

    private val vm: TunerActivityViewModel by viewModels()

    private lateinit var permissionHandler: PermissionHandler
    private lateinit var toneGenerator: ToneGenerator
    private lateinit var prefsFlow: Flow<TunerPreferences>
    private lateinit var reviewController: ReviewController

    private lateinit var dismissTuningSelectorOnBack: OnBackPressedCallback
    private lateinit var dismissConfigurePanelOnBack: OnBackPressedCallback

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) enableEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Preferences
        prefsFlow = tunerPreferenceDataStore.data
            .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
            .map(TunerPreferences::fromAndroidPreferences)

        permissionHandler = PermissionHandler(this, Manifest.permission.RECORD_AUDIO)
        toneGenerator = ToneGenerator()
        reviewController = ReviewControllerImpl(this)

        // Load tunings once
        lifecycleScope.launch {
            val firstLoad = vm.tuningList.loadTunings(this@TunerActivity)
            if (firstLoad) {
                prefsFlow.firstOrNull()?.let { prefs ->
                    vm.setEditMode(prefs.editModeDefault)
                    when (prefs.initialTuning) {
                        InitialTuningType.PINNED ->
                            when (val pinned = vm.tuningList.pinned.value) {
                                is TuningEntry.InstrumentTuning -> vm.tuner.setTuning(pinned.tuning!!)
                                is TuningEntry.ChromaticTuning -> vm.tuner.setChromatic(true)
                            }

                        InitialTuningType.LAST_USED ->
                            vm.tuningList.lastUsed.value?.let {
                                when (it) {
                                    is TuningEntry.InstrumentTuning -> vm.tuner.setTuning(it.tuning)
                                    is TuningEntry.ChromaticTuning -> vm.tuner.setChromatic(true)
                                }
                            }
                    }
                }
            }
        }

        dismissConfigurePanelOnBack = onBackPressedDispatcher.addCallback(this, false) {
            vm.dismissConfigurePanel()
        }

        dismissTuningSelectorOnBack = onBackPressedDispatcher.addCallback(this, false) {
            vm.dismissTuningSelector()
        }

        setContent {
            val prefs by prefsFlow.collectAsStateWithLifecycle(initialValue = TunerPreferences())

            LaunchedEffect(prefs.a4Pitch) {
                vm.tuner.setA4Pitch(prefs.a4Pitch)
            }

            AppTheme(
                fullBlack = prefs.useBlackTheme,
                dynamicColor = prefs.useDynamicColor
            ) {
                val granted by permissionHandler.granted.collectAsStateWithLifecycle()
                val error by vm.tuner.error.collectAsStateWithLifecycle()

                if (granted && error == null) {

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

                    LaunchedEffect(tuningSelectorOpen) {
                        dismissTuningSelectorOnBack.isEnabled = tuningSelectorOpen
                    }
                    LaunchedEffect(configurePanelOpen) {
                        dismissConfigurePanelOnBack.isEnabled = configurePanelOpen
                    }

                    val windowSizeClass = calculateWindowSizeClass(this)
                    val compact = remember(windowSizeClass) {
                        windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact &&
                                windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
                    }
                    val expanded = remember(windowSizeClass) {
                        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded &&
                                windowSizeClass.heightSizeClass > WindowHeightSizeClass.Compact
                    }

                    MainLayout(
                        windowSizeClass = windowSizeClass,
                        compact = compact,
                        expanded = expanded,

                        tuning = if (chromatic)
                            TuningEntry.ChromaticTuning
                        else
                            TuningEntry.InstrumentTuning(tuning),

                        noteOffset = noteOffset,
                        selectedString = selectedString,
                        selectedNote = selectedNote,
                        tuned = tuned,
                        noteTuned = noteTuned,
                        autoDetect = autoDetect,
                        chromatic = chromatic,
                        favTunings = favTunings,

                        getCanonicalName = {
                            vm.tuningList.run { this@MainLayout.getCanonicalName() }
                        },

                        prefs = prefs,
                        tuningList = vm.tuningList,
                        tuningSelectorOpen = tuningSelectorOpen,
                        configurePanelOpen = configurePanelOpen,

                        onSelectTuning = vm::selectTuning,
                        onSelectChromatic = vm::selectChromatic,

                        onSelectString = { string ->
                            vm.tuner.selectString(string)
                            if (prefs.enableStringSelectSound) {
                                val noteIndex =
                                    vm.tuner.tuning.value.getString(string).rootNoteIndex
                                playTone(noteIndex, prefs.a4Pitch)
                            }
                        },

                        onSelectNote = { noteIndex ->
                            vm.tuner.selectNote(noteIndex)
                            if (prefs.enableStringSelectSound) {
                                playTone(noteIndex, prefs.a4Pitch)
                            }
                        },

                        onTuneUpString = vm.tuner::tuneStringUp,
                        onTuneDownString = vm.tuner::tuneStringDown,
                        onTuneUpTuning = vm.tuner::tuneUp,
                        onTuneDownTuning = vm.tuner::tuneDown,
                        onAutoChanged = vm.tuner::setAutoDetect,

                        onTuned = {
                            vm.tuner.setTuned()
                            if (prefs.enableInTuneSound) {
                                playTone(vm.tuner.selectedNote.value, prefs.a4Pitch, 120)
                            }
                        },

                        onOpenTuningSelector = vm::openTuningSelector,
                        onSettingsPressed = ::openSettings,
                        onConfigurePressed = vm::openConfigurePanel,
                        onSelectTuningFromList = vm::selectTuning,
                        onSelectChromaticFromList = vm::selectChromatic,
                        onDismissTuningSelector = vm::dismissTuningSelector,
                        onDismissConfigurePanel = vm::dismissConfigurePanel,
                        onEditModeChanged = vm::setEditMode,
                        editModeEnabled = editModeEnabled
                    )

                } else if (!granted) {
                    val firstRequest by permissionHandler.firstRequest.collectAsStateWithLifecycle()
                    TunerPermissionScreen(
                        canRequest = firstRequest,
                        onSettingsPressed = ::openSettings,
                        onRequestPermission = permissionHandler::request,
                        onOpenPermissionSettings = ::openPermissionSettings
                    )
                } else {
                    TunerErrorScreen(error, ::openSettings)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!vm.tuningSelectorOpen.value && !vm.configurePanelOpen.value) {
            try {
                vm.tuner.start(permissionHandler)
            } catch (_: Exception) {}
        }
    }

    override fun onPause() {
        vm.tuner.stop()
        toneGenerator.stop()
        super.onPause()
    }

    private fun playTone(
        noteIndex: Int,
        a4Pitch: Double,
        durationMs: Int = 200
    ) {
        val freq = Notes.getPitch(noteIndex, a4Pitch)
        toneGenerator.play(freq, durationMs)
    }

    private fun openSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun openPermissionSettings() {
        startActivity(
            Intent(
                ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            )
        )
    }
}

@VisibleForTesting
class TunerActivityViewModel : ViewModel() {

    val tuner = Tuner()
    val tuningList = TuningList(tuner.tuning.value, viewModelScope)

    private val _tuningSelectorOpen = MutableStateFlow(false)
    val tuningSelectorOpen = _tuningSelectorOpen.asStateFlow()

    private val _configurePanelOpen = MutableStateFlow(false)
    val configurePanelOpen = _configurePanelOpen.asStateFlow()

    private val _editModeEnabled = MutableStateFlow(false)
    val editModeEnabled = _editModeEnabled.asStateFlow()

    fun setEditMode(enabled: Boolean) {
        _editModeEnabled.value = enabled
    }

    fun openTuningSelector() {
        _tuningSelectorOpen.value = true
    }

    fun dismissTuningSelector() {
        _tuningSelectorOpen.value = false
    }

    fun openConfigurePanel() {
        _configurePanelOpen.value = true
    }

    fun dismissConfigurePanel() {
        _configurePanelOpen.value = false
    }

    fun selectTuning(tuning: Tuning) {
        tuner.setTuning(tuning)
        dismissTuningSelector()
    }

    fun selectChromatic() {
        tuner.setChromatic(true)
        dismissTuningSelector()
    }
}
