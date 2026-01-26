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

import com.rohankhayech.choona.lib.model.tuning.ChromaticTuning
import com.rohankhayech.choona.lib.model.tuning.Tunings
import com.rohankhayech.choona.lib.model.tuning.Tuning
import com.rohankhayech.choona.lib.model.tuning.TuningEntry
import com.rohankhayech.choona.lib.view.viewmodel.TunerViewModel.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Test harness for the TunerActivity view model.
 *
 * @author Rohan Khayech
 */
class TunerViewModelTest {

    private lateinit var vm: TunerViewModel

    private var testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        vm = TunerViewModel()
    }

    @Test
    fun testInitial() {
        assertEquals(vm.tuner.tuning.value, vm.tuningList.current.value)
        assertTrue(vm.backStack.size == 1)
        assertTrue(vm.backStack.last() == Screen.Tuner)
        assertFalse(vm.editModeEnabled.value)
    }

    @Test
    fun testOpenTuningSelector() {
        vm.openTuningSelector()
        assertTrue(vm.backStack.size == 2)
        assertTrue(vm.backStack.last() == Screen.TuningSelection)
    }

    @Test
    fun testOpenConfigurePanel() {
        vm.openConfigurePanel()
        assertTrue(vm.backStack.size == 2)
        assertTrue(vm.backStack.last() == Screen.ConfigureTuning)
    }

    @Test
    fun testNavBack() {
        vm.openTuningSelector()
        vm.navBack()
        assertTrue(vm.backStack.size == 1)
        assertTrue(vm.backStack.last() == Screen.Tuner)
    }

    @Test
    fun testDismissConfigurePanel() {
        vm.openConfigurePanel()
        vm.dismissConfigurePanel()
        assertTrue(vm.backStack.size == 1)
        assertTrue(vm.backStack.last() == Screen.Tuner)
    }

    @Test
    fun testSelectTuning() {
        vm.openTuningSelector()
        vm.selectTuningFromList(Tunings.DROP_D)
        testDispatcher.scheduler.runCurrent()
        assertTrue(vm.backStack.size == 1)
        assertTrue(vm.backStack.last() == Screen.Tuner)
        assertEquals(Tunings.DROP_D, vm.tuner.tuning.value)
        assertEquals(Tunings.DROP_D, vm.tuningList.current.value)
    }

    @Test
    fun testTuningSync() {
        vm.tuner.setTuning(Tunings.DROP_D)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(Tunings.DROP_D, vm.tuningList.current.value)
        vm.tuningList.setCurrent(Tunings.STANDARD)
        testDispatcher.scheduler.runCurrent()
        assertEquals(Tunings.STANDARD, vm.tuner.tuning.value)

        vm.tuner.setChromatic()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(ChromaticTuning, vm.tuningList.current.value)

        vm.tuner.setChromatic(false)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(Tunings.STANDARD, vm.tuningList.current.value)

        vm.tuningList.setCurrent(ChromaticTuning)
        testDispatcher.scheduler.runCurrent()
        assertTrue(vm.tuner.chromatic.value)

        vm.tuningList.setCurrent(Tunings.STANDARD)
        testDispatcher.scheduler.runCurrent()
        assertEquals(Tunings.STANDARD, vm.tuner.tuning.value)
        assertFalse(vm.tuner.chromatic.value)
    }

    @Test
    fun testEditModeToggle() {
        vm.setEditMode(true)
        assertTrue(vm.editModeEnabled.value)
        vm.setEditMode(false)
        assertFalse(vm.editModeEnabled.value)
    }

    @Test
    fun testSelectChromatic() {
        vm.openTuningSelector()
        vm.selectChromaticFromList()
        assertTrue(vm.backStack.size == 1)
        assertTrue(vm.backStack.last() == Screen.Tuner)
        assertTrue(vm.tuner.chromatic.value)
    }
}