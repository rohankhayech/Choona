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

package com.rohankhayech.choona.lib.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohankhayech.choona.lib.controller.tuner.Tuner
import com.rohankhayech.choona.lib.model.tuning.TuningEntry
import com.rohankhayech.choona.lib.model.tuning.TuningList
import com.rohankhayech.music.Tuning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** View model used to hold the current tuner and UI state. */
class TunerViewModel : ViewModel() {
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