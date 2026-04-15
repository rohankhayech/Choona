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

package com.rohankhayech.choona.wear.view.screens

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.wear.compose.navigation3.rememberSwipeDismissableSceneStrategy
import com.rohankhayech.choona.lib.controller.tunings.TuningList
import com.rohankhayech.choona.lib.model.preferences.InitialTuningType
import com.rohankhayech.choona.lib.model.preferences.TunerPreferences
import com.rohankhayech.choona.lib.model.tuning.Instrument
import com.rohankhayech.choona.lib.model.tuning.InstrumentTuning
import com.rohankhayech.choona.lib.model.tuning.Tuning
import com.rohankhayech.choona.lib.view.viewmodel.EditTuningViewModel
import com.rohankhayech.choona.lib.view.viewmodel.TunerViewModel.Screen

/**
 * Layout composable for the main tuning screen.
 *
 * @param backStack List of screens that have been navigated to.
 * @param granted Whether the audio permission has been granted.
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
 * @param canRequest Whether the permission can be requested.
 * @param error The error that has occurred.
 * @param onSelectString Called when a string is selected.
 * @param onSelectTuning Called when a tuning is selected from the selection panel.
 * @param onSelectChromatic Called when the chromatic mode is selected from the selection panel.
 * @param onSelectNote Called when a note is selected in chromatic mode.
 * @param onTuneUpString Called when a string is tuned up.
 * @param onTuneDownString Called when a string is tuned down.
 * @param onTuneUpTuning Called when the tuning is tuned up.
 * @param onTuneDownTuning Called when the tuning is tuned down.
 * @param onAutoChanged Called when the auto detect switch is toggled.
 * @param onTuned Called when the detected note is held in tune.
 * @param onOpenTuningSelector Called when the user opens the tuning selector screen.
 * @param onOpenConfigurePanel Called when the configure tuning panel is opened.
 * @param onOpenTuningEditor Called when the edit tuning screen is opened.
 * @param onSaveTuningFromEditor Called when the user saves a tuning from the editor.
 * @param onDeleteTuningFromEditor Called when the user deletes a tuning from the editor.
 * @param onPressNote Called when a note selection button is pressed.
 * @param onSettingsPressed Called when the settings button is pressed.
 * @param onBack Called when the back button is pressed.
 * @param onRequestPermission Called when the request permission button is pressed.
 * @param onOpenPermissionSettings Called when the open permission settings button is pressed.
 *
 * @author Rohan Khayech
 */
@Composable
fun MainLayout(
    backStack: List<Screen>,
    granted: Boolean,
    tuning: Tuning,
    noteOffset: State<Double?>,
    selectedString: Int,
    selectedNote: Int,
    tuned: BooleanArray,
    noteTuned: Boolean,
    autoDetect: Boolean,
    chromatic: Boolean,
    favTunings: State<Set<Tuning>>,
    getCanonicalName: (InstrumentTuning) -> String,
    prefs: TunerPreferences,
    tuningList: TuningList,
    canRequest: Boolean,
    error: Exception?,
    onSelectString: (Int) -> Unit,
    onSelectTuning: (InstrumentTuning) -> Unit,
    onSelectChromatic: () -> Unit,
    onSelectNote: (Int) -> Unit,
    onTuneUpString: (Int) -> Unit,
    onTuneDownString: (Int) -> Unit,
    onTuneUpTuning: () -> Unit,
    onTuneDownTuning: () -> Unit,
    onAutoChanged: (Boolean) -> Unit,
    onTuned: () -> Unit,
    onOpenTuningSelector: () -> Unit,
    onOpenConfigurePanel: () -> Unit,
    onOpenTuningEditor: (InstrumentTuning, Boolean) -> Unit,
    onSaveTuningFromEditor: (InstrumentTuning, Screen.EditTuning) -> Unit,
    onDeleteTuningFromEditor: (Screen.EditTuning) -> Unit,
    onPressNote: (Int, Instrument) -> Unit,
    onBack: () -> Unit,
    onSettingsPressed: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenPermissionSettings: () -> Unit
) {
    NavDisplay(
        backStack = backStack,
        onBack = onBack,
        sceneStrategy = rememberSwipeDismissableSceneStrategy(),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(), // state preservation
            rememberViewModelStoreNavEntryDecorator(), // enable ViewModel scoping to the NavEntry
        ),
        entryProvider = entryProvider {
            entry<Screen.Tuner> {
                TunerScreen(
                    granted = granted,
                    tuning = tuning,
                    prefs = prefs,
                    noteOffset = noteOffset,
                    selectedString = selectedString,
                    selectedNote = selectedNote,
                    autoDetect = autoDetect,
                    chromatic = chromatic,
                    tuned = tuned,
                    noteTuned = noteTuned,
                    canRequest = canRequest,
                    error = error,
                    onSelectString = onSelectString,
                    onSelectNote = onSelectNote,
                    onAutoChanged = onAutoChanged,
                    getCanonicalName = getCanonicalName,
                    onTuned = onTuned,
                    onOpenConfigurePanel = onOpenConfigurePanel,
                    onRequestPermission = onRequestPermission,
                    onOpenPermissionSettings = onOpenPermissionSettings
                )
            }

            val transitionSpec = NavDisplay.transitionSpec {
                slideIntoContainer(SlideDirection.Up) togetherWith fadeOut()
            } + NavDisplay.popTransitionSpec {
                fadeIn() togetherWith slideOutOfContainer(SlideDirection.Down)
            } + NavDisplay.predictivePopTransitionSpec {
                fadeIn() togetherWith slideOutOfContainer(SlideDirection.End)
            }

            entry<Screen.ConfigureTuning>(
                metadata = transitionSpec
            ) {
                ConfigureTuningScreen(
                    tuning = tuning,
                    chromatic = chromatic,
                    selectedNote = selectedNote,
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

            entry<Screen.TuningSelection>(
                metadata = transitionSpec
            ) {
                TuningListScreen(
                    tuningList = tuningList,
                    pinnedInitial = prefs.initialTuning == InitialTuningType.PINNED,
                    onSelect = onSelectTuning,
                    onSelectChromatic = onSelectChromatic,
                    onOpenTuningEditor = onOpenTuningEditor,
                    onDismiss = onBack,
                )
            }

            entry<Screen.EditTuning>(
                metadata = transitionSpec
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
                    onDelete = { onDeleteTuningFromEditor(key) }
                )
            }
        }
    )
}
