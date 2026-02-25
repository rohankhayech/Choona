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

package com.rohankhayech.choona.lib.view.viewmodel

import kotlin.math.max
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.rohankhayech.choona.lib.controller.tuner.Tuner
import com.rohankhayech.choona.lib.controller.tunings.TuningList
import com.rohankhayech.choona.lib.model.tuning.Tuning
import com.rohankhayech.choona.lib.model.tuning.TuningEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

/** View model used to hold the current tuner and UI state. */
class TunerViewModel : ViewModel() {
    /** Tuner used for audio processing and note comparison. */
    val tuner = Tuner()

    /** State holder containing the lists of favourite and custom tunings. */
    val tuningList = TuningList(tuner.tuning.value, viewModelScope)

    /** Mutable backing property for [backStack]. */
    private val _backStack: NavBackStack<Screen> = NavBackStack(Screen.Tuner)

    /* Navigation back-stack for the Tuner activity. */
    val backStack: List<Screen> = _backStack

    /** Mutable backing property for [editModeEnabled]. */
    private val _editModeEnabled = MutableStateFlow(false)

    /** Whether the edit mode is currently enabled. */
    val editModeEnabled = _editModeEnabled.asStateFlow()

    /** Whether the app is displayed in an expanded width window. */
    private val _expanded = MutableStateFlow(false)

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
        if (!_backStack.contains(Screen.TuningSelection)) {
            val index = max(_backStack.indexOf(Screen.ConfigureTuning), _backStack.indexOf(Screen.Tuner)) + 1
            _backStack.add(index, Screen.TuningSelection)
        }
    }

    /** Opens the configure tuning panel. */
    fun openConfigurePanel() {
        if (!_backStack.contains(Screen.ConfigureTuning)) {
            _backStack.add(Screen.ConfigureTuning)
        }
    }

    /**
     * Navigates back to the previous screen in the back-stack,
     * if one exists.
     */
    fun navBack() {
        if (_backStack.size > 1) {
            _backStack.removeLastOrNull()
        }
    }

    /** Dismisses the configure tuning panel. */
    fun dismissConfigurePanel() {
        _backStack.remove(Screen.ConfigureTuning)
    }

    /** Sets the current tuning to that selected in the tuning selection screen and dismisses it. */
    fun selectTuningFromList(tuning: Tuning) {
        navBack()
        tuner.setTuning(tuning)
    }

    /** Sets the current tuning to chromatic as selected in the tuning selection screen and dismisses it. */
    fun selectChromaticFromList() {
        navBack()
        tuner.setChromatic(true)
    }

    /** Sets whether the app is displayed in an [expanded] width window. */
    fun setExpanded(expanded: Boolean) {
        _expanded.update { expanded }
    }

    /**
     * @return Whether the tuner screen is open.
     */
    fun isTunerScreenOpen(): Boolean =
        backStack.last() == Screen.Tuner || _expanded.value && backStack.last() == Screen.TuningSelection

    fun showPermissionScreen() {
        _backStack.clear()
        _backStack.add(Screen.Permission)
    }

    fun dismissPermissionScreen() {
        if (backStack.first() == Screen.Permission) {
            _backStack.add(Screen.Tuner)
            _backStack.remove(Screen.Permission)
        }
    }

    /**
     * Navigation entries for the screens in the Tuner activity.
     */
    @Serializable
    sealed class Screen: NavKey {
        @Serializable object Tuner: Screen()
        @Serializable object ConfigureTuning: Screen()
        @Serializable object TuningSelection: Screen()
        @Serializable object Permission: Screen()
    }
}