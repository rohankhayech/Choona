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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rohankhayech.choona.lib.controller.fileio.TuningFileIO
import com.rohankhayech.choona.lib.controller.tunings.CustomTuningEditor
import com.rohankhayech.choona.lib.model.tuning.Tuning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the Edit Tuning screen.
 *
 * Manages the state for editing a tuning, including its name and the
 * [CustomTuningEditor] for structural changes.
 *
 * @param initialTuning The tuning to edit.
 * @param new Whether a new tuning is being created.
 *
 * @author Rohan Khayech
 */
class EditTuningViewModel(
    initialTuning: Tuning,
    val new: Boolean
) : ViewModel() {
    private val _name = MutableStateFlow(
        if (new) "" else initialTuning.nameOrBlank
    )

    /** The current name of the tuning. */
    val name = _name.asStateFlow()

    /** Sets the name of the tuning. */
    fun setName(name: String) {
        _name.update { name }
    }

    /** The editor used to modify the tuning. */
    val editor = CustomTuningEditor(initialTuning)

    /**
     * Returns the edited tuning result.
     *
     * @return A new [Tuning] object with the updated name and structure.
     */
    fun returnResult(): Tuning {
        return Tuning(
            _name.value.ifBlank { null },
            editor.tuning.value.instrument,
            null,
            editor.tuning.value.strings
        )
    }

    companion object {
        /**
         * Provides a [ViewModelProvider.Factory] for creating [EditTuningViewModel] instances.
         *
         * @param initialTuningJSON JSON representation of the tuning to edit.
         * @param new Whether a new tuning is being created.
         * @return A factory for the ViewModel.
         */
        fun provideFactory(
            initialTuningJSON: String,
            new: Boolean
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val initialTuning = TuningFileIO.parseTuningFromString(initialTuningJSON)
                return EditTuningViewModel(initialTuning, new) as T
            }
        }
    }
}