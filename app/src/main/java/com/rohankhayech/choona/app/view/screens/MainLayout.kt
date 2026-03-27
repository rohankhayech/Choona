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

package com.rohankhayech.choona.app.view.screens

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberSupportingPaneSceneStrategy
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.rohankhayech.android.util.ui.theme.m3.isLight
import com.rohankhayech.android.util.ui.theme.m3.isTrueDark
import com.rohankhayech.choona.lib.controller.tunings.TuningList
import com.rohankhayech.choona.lib.model.preferences.InitialTuningType
import com.rohankhayech.choona.lib.model.preferences.TunerPreferences
import com.rohankhayech.choona.lib.model.tuning.Instrument
import com.rohankhayech.choona.lib.model.tuning.Tuning
import com.rohankhayech.choona.lib.model.tuning.TuningEntry
import com.rohankhayech.choona.lib.view.viewmodel.EditTuningViewModel
import com.rohankhayech.choona.lib.view.viewmodel.TunerViewModel.Screen

/**
 * Layout composable for the main tuning screen.
 * Handles layout of screens on different form factors.
 *
 * @param backStack List of screens that have been navigated to.
 * @param windowSizeClass Size class of the activity window.
 * @param granted Whether the audio permission has been granted.
 * @param compact Whether to use compact layout.
 * @param expanded Whether to use expanded layout.
 * @param tuning Guitar tuning used for comparison.
 * @param noteOffset The offset between the currently playing note and the selected string.
 * @param selectedString Index of the currently selected string within the tuning.
 * @param selectedNote The index of the currently selected note in chromatic mode.
 * @param tuned Whether each string has been tuned.
 * @param noteTuned Whether the currently detected note in chromatic mode has been tuned.
 * @param autoDetect Whether the tuner will automatically detect the currently playing string/note.
 * @param chromatic Whether the tuner is in chromatic mode.
 * @param favTunings Set of tunings marked as favourite by the user.
 * @param getCanonicalName Gets the name of the tuning if it is saved as a custom tuning.
 * @param prefs User preferences for the tuner.
 * @param tuningList State holder for the tuning list.
 * @param editModeEnabled Whether the edit mode is enabled.
 * @param canRequest Whether the permission can be requested.
 * @param error The error that has occurred.
 * @param onEditModeChanged Called when the edit mode is toggled.
 * @param onSelectString Called when a string is selected.
 * @param onSelectTuning Called when a tuning is selected.
 * @param onSelectChromatic Called when the chromatic mode is selected.
 * @param onSelectNote Called when a note is selected in chromatic mode.
 * @param onTuneUpString Called when a string is tuned up.
 * @param onTuneDownString Called when a string is tuned down.
 * @param onTuneUpTuning Called when the tuning is tuned up.
 * @param onTuneDownTuning Called when the tuning is tuned down.
 * @param onAutoChanged Called when the auto detect switch is toggled.
 * @param onTuned Called when the detected note is held in tune.
 * @param onOpenTuningSelector Called when the user opens the tuning selector screen.
 * @param onSettingsPressed Called when the settings button is pressed.
 * @param onConfigurePressed Called when the configure tuning button is pressed.
 * @param onSelectTuningFromList Called when a tuning is selected from the selection panel.
 * @param onSelectChromaticFromList Called when the chromatic mode is selected from the selection panel.
 * @param onOpenTuningEditor Called when the edit tuning screen is opened.
 * @param onSaveTuningFromEditor Called when the user saves a tuning from the editor.
 * @param onDeleteTuningFromEditor Called when the user deletes a tuning from the editor.
 * @param onPressNote Called when a note selection button is pressed.
 * @param onBack Called when the back button is pressed.
 * @param onRequestPermission Called when the request permission button is pressed.
 * @param onOpenPermissionSettings Called when the open permission settings button is pressed.
 *
 * @author Rohan Khayech
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainLayout(
    backStack: List<Screen>,
    windowSizeClass: WindowSizeClass,
    granted: Boolean,
    compact: Boolean,
    expanded: Boolean,
    tuning: TuningEntry,
    noteOffset: State<Double?>,
    selectedString: Int,
    selectedNote: Int,
    tuned: BooleanArray,
    noteTuned: Boolean,
    autoDetect: Boolean,
    chromatic: Boolean,
    favTunings: State<Set<TuningEntry>>,
    getCanonicalName: (TuningEntry.InstrumentTuning) -> String,
    prefs: TunerPreferences,
    tuningList: TuningList,
    editModeEnabled: Boolean,
    canRequest: Boolean,
    error: Exception?,
    onEditModeChanged: (Boolean) -> Unit,
    onSelectString: (Int) -> Unit,
    onSelectTuning: (Tuning) -> Unit,
    onSelectChromatic: () -> Unit,
    onSelectNote: (Int) -> Unit,
    onTuneUpString: (Int) -> Unit,
    onTuneDownString: (Int) -> Unit,
    onTuneUpTuning: () -> Unit,
    onTuneDownTuning: () -> Unit,
    onAutoChanged: (Boolean) -> Unit,
    onTuned: () -> Unit,
    onOpenTuningSelector: () -> Unit,
    onSettingsPressed: () -> Unit,
    onConfigurePressed: () -> Unit,
    onSelectTuningFromList: (Tuning) -> Unit,
    onSelectChromaticFromList: () -> Unit,
    onOpenTuningEditor: (Tuning, Boolean) -> Unit,
    onSaveTuningFromEditor: (Tuning, Screen.EditTuning) -> Unit,
    onDeleteTuningFromEditor: (Screen.EditTuning) -> Unit,
    onPressNote: (Int, Instrument) -> Unit,
    onBack: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenPermissionSettings: () -> Unit
) {
    val sceneStrategy = rememberSupportingPaneSceneStrategy<Screen>(
        directive = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo()).copy(
            maxHorizontalPartitions = if (expanded) 2 else 1,
            maxVerticalPartitions = 1,
            horizontalPartitionSpacerSize =
                if (expanded && MaterialTheme.isTrueDark && !MaterialTheme.isLight) DividerDefaults.Thickness else 0.dp
        )
    )

    val dialogStrategy = remember { DialogSceneStrategy<Screen>() }

    NavDisplay(
        backStack,
        onBack = onBack,
        sceneStrategy = if (expanded) dialogStrategy then sceneStrategy else sceneStrategy,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(), // state preservation
            rememberViewModelStoreNavEntryDecorator(), // enable ViewModel scoping to the NavEntry
        ),
        entryProvider = entryProvider {
            entry<Screen.Tuner>(metadata = SupportingPaneSceneStrategy.mainPane()) {
                TunerScreen(
                    granted = granted,
                    compact,
                    expanded,
                    windowSizeClass,
                    tuning,
                    noteOffset,
                    selectedString,
                    selectedNote,
                    tuned,
                    noteTuned,
                    autoDetect,
                    chromatic,
                    favTunings,
                    getCanonicalName,
                    prefs,
                    canRequest = canRequest,
                    error = error,
                    onSelectString,
                    onSelectTuning,
                    onSelectChromatic,
                    onSelectNote,
                    onTuneUpString,
                    onTuneDownString,
                    onTuneUpTuning,
                    onTuneDownTuning,
                    onAutoChanged,
                    onTuned,
                    onOpenTuningSelector,
                    onSettingsPressed,
                    onConfigurePressed,
                    editModeEnabled,
                    onEditModeChanged,
                    onRequestPermission,
                    onOpenPermissionSettings
                )
            }

            entry<Screen.ConfigureTuning>(
                metadata = NavDisplay.transitionSpec {
                    slideIntoContainer(SlideDirection.Down) togetherWith fadeOut()
                } + NavDisplay.popTransitionSpec {
                    fadeIn() togetherWith slideOutOfContainer(SlideDirection.Up)
                } + NavDisplay.predictivePopTransitionSpec {
                    fadeIn() togetherWith slideOutOfContainer(SlideDirection.Up)
                }
            ) {
                ConfigureTuningScreen(
                    tuning = tuning,
                    chromatic,
                    selectedNote = selectedNote,
                    favTunings = favTunings,
                    getCanonicalName = getCanonicalName,
                    onTuneUpString = onTuneUpString,
                    onTuneDownString = onTuneDownString,
                    onTuneUpTuning = onTuneUpTuning,
                    onTuneDownTuning = onTuneDownTuning,
                    onSelectNote = onSelectNote,
                    onOpenTuningSelector = onOpenTuningSelector,
                    onDismiss = onBack,
                    onSettingsPressed = onSettingsPressed
                )
            }

            entry<Screen.TuningSelection> (
                metadata = SupportingPaneSceneStrategy.supportingPane()
                    + NavDisplay.transitionSpec {
                        slideIntoContainer(SlideDirection.Up) togetherWith fadeOut()
                    } + NavDisplay.popTransitionSpec {
                        fadeIn() togetherWith slideOutOfContainer(SlideDirection.Down)
                    } + NavDisplay.predictivePopTransitionSpec {
                        fadeIn() togetherWith slideOutOfContainer(SlideDirection.Down)
                    }
            ) {
                Surface(tonalElevation = if (expanded && (!MaterialTheme.isTrueDark || MaterialTheme.isLight)) 1.dp else 0.dp) {
                    TuningSelectionScreen(
                        tuningList = tuningList,
                        backIcon = if (backStack.contains(Screen.ConfigureTuning)) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Close,
                        pinnedInitial = prefs.initialTuning == InitialTuningType.PINNED,
                        onSelect = onSelectTuningFromList,
                        onSelectChromatic = onSelectChromaticFromList,
                        onOpenTuningEditor = onOpenTuningEditor,
                        onDismiss = onBack
                    )
                }
            }

            entry<Screen.EditTuning> (
                metadata = DialogSceneStrategy.dialog() + NavDisplay.transitionSpec {
                    slideIntoContainer(SlideDirection.Up) togetherWith fadeOut()
                } + NavDisplay.popTransitionSpec {
                    fadeIn() togetherWith slideOutOfContainer(SlideDirection.Down)
                } + NavDisplay.predictivePopTransitionSpec {
                    fadeIn() togetherWith slideOutOfContainer(SlideDirection.Down)
                }
            ) { key ->
                val editVM: EditTuningViewModel = viewModel(
                    factory = EditTuningViewModel.provideFactory(key.tuningJSON, key.new)
                )
                val editTuning by editVM.editor.tuning.collectAsStateWithLifecycle()
                val editName by editVM.name.collectAsStateWithLifecycle()
                val hasChanges by editVM.hasChanges.collectAsStateWithLifecycle()

                EditTuningScreen(
                    name = editName,
                    new = key.new,
                    tuning = editTuning,
                    hasChanges = hasChanges,
                    onNameChange = editVM::setName,
                    onInstrumentChange = editVM.editor::setInstrument,
                    onSetString = editVM.editor::setString,
                    onAddLowString = editVM.editor::addLowString,
                    onAddHighString = editVM.editor::addHighString,
                    onRemoveLowString = editVM.editor::removeLowString,
                    onRemoveHighString = editVM.editor::removeHighString,
                    onTuneStringUp = editVM.editor::tuneStringUp,
                    onTuneStringDown = editVM.editor::tuneStringDown,
                    onTuneUp = editVM.editor::tuneUp,
                    onTuneDown = editVM.editor::tuneDown,
                    onPressNote = onPressNote,
                    onCancel = onBack,
                    onSave = {
                        onSaveTuningFromEditor(editVM.returnResult(), key)
                    },
                    onDelete = { onDeleteTuningFromEditor(key) },
                    useRoundedCorners = expanded
                )
            }
        }
    )
}
