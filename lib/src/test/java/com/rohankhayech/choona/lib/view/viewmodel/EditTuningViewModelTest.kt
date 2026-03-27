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

import com.rohankhayech.choona.lib.model.tuning.Instrument
import com.rohankhayech.choona.lib.model.tuning.Tunings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [EditTuningViewModel].
 *
 * @author Rohan Khayech
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EditTuningViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Verifies that [EditTuningViewModel.setName] correctly updates the name state.
     */
    @Test
    fun setName() {
        val viewModel = EditTuningViewModel(Tunings.STANDARD, false)
        val newName = "New Tuning Name"
        viewModel.setName(newName)
        assertEquals(newName, viewModel.name.value)
    }

    /**
     * Verifies that [EditTuningViewModel.returnResult] correctly constructs a [com.rohankhayech.choona.lib.model.tuning.Tuning] object
     * from the current state of the ViewModel and its editor.
     */
    @Test
    fun returnResult() {
        val initialTuning = Tunings.STANDARD
        val viewModel = EditTuningViewModel(initialTuning, true)
        val newName = "Custom"
        viewModel.setName(newName)
        viewModel.editor.setInstrument(Instrument.BASS)

        val result = viewModel.returnResult()

        assertEquals(newName, result.name)
        assertEquals(Instrument.BASS, result.instrument)
        assertEquals(initialTuning.strings, result.strings)
    }

    /**
     * Verifies that the ViewModel is initialized with correct default values when creating a new tuning.
     */
    @Test
    fun initialValues_newTuning() {
        val initialTuning = Tunings.STANDARD
        val viewModel = EditTuningViewModel(initialTuning, true)
        assertEquals("", viewModel.name.value)
        assertEquals(true, viewModel.new)
        assertEquals(initialTuning, viewModel.editor.tuning.value)
        assertEquals(false, viewModel.hasChanges.value)
    }

    /**
     * Verifies that the ViewModel is initialized with values from the existing tuning when in edit mode.
     */
    @Test
    fun initialValues_editTuning() {
        val initialTuning = Tunings.STANDARD
        val viewModel = EditTuningViewModel(initialTuning, false)
        assertEquals(initialTuning.name, viewModel.name.value)
        assertEquals(false, viewModel.new)
        assertEquals(initialTuning, viewModel.editor.tuning.value)
        assertEquals(false, viewModel.hasChanges.value)
    }

    /**
     * Verifies that [EditTuningViewModel.hasChanges] correctly reflects whether the tuning has been modified.
     */
    @Test
    fun hasChanges() {
        val initialTuning = Tunings.STANDARD
        val viewModel = EditTuningViewModel(initialTuning, false)
        assertEquals(false, viewModel.hasChanges.value)

        // Change name
        viewModel.setName("Changed")
        testDispatcher.scheduler.runCurrent()
        assertEquals(true, viewModel.hasChanges.value)

        // Reset name
        viewModel.setName(initialTuning.name)
        testDispatcher.scheduler.runCurrent()
        assertEquals(false, viewModel.hasChanges.value)

        // Change tuning structure
        viewModel.editor.tuneUp()
        testDispatcher.scheduler.runCurrent()
        assertEquals(true, viewModel.hasChanges.value)

        // Reset tuning structure
        viewModel.editor.tuneDown()
        testDispatcher.scheduler.runCurrent()
        assertEquals(false, viewModel.hasChanges.value)
    }
}