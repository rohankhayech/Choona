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

import com.rohankhayech.music.Tuning
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
class TunerActivityViewModelTest {

    private lateinit var vm: TunerActivityViewModel

    private var testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        vm = TunerActivityViewModel()
    }

    @Test
    fun testInitial() {
        assertEquals(vm.tuner.tuning.value, vm.tuningList.current.value)
        assertFalse(vm.tuningSelectorOpen.value)
        assertFalse(vm.configurePanelOpen.value)
        assertFalse(vm.editModeEnabled.value)
    }

    @Test
    fun testOpenTuningSelector() {
        vm.openTuningSelector()
        assertTrue(vm.tuningSelectorOpen.value)
    }

    @Test
    fun testOpenConfigurePanel() {
        vm.openConfigurePanel()
        assertTrue(vm.configurePanelOpen.value)
    }

    @Test
    fun testDismissTuningSelector() {
        vm.openTuningSelector()
        vm.dismissTuningSelector()
        assertFalse(vm.tuningSelectorOpen.value)
    }

    @Test
    fun testDismissConfigurePanel() {
        vm.openConfigurePanel()
        vm.dismissConfigurePanel()
        assertFalse(vm.configurePanelOpen.value)
    }

    @Test
    fun testSelectTuning() {
        vm.openTuningSelector()
        vm.selectTuning(Tuning.DROP_D)
        testDispatcher.scheduler.runCurrent()
        assertFalse(vm.tuningSelectorOpen.value)
        assertEquals(Tuning.DROP_D, vm.tuner.tuning.value)
        assertEquals(Tuning.DROP_D, vm.tuningList.current.value)
    }

    @Test
    fun testTuningSync() {
        vm.tuner.setTuning(Tuning.DROP_D)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(Tuning.DROP_D, vm.tuningList.current.value)
        vm.tuningList.setCurrent(Tuning.STANDARD)
        testDispatcher.scheduler.runCurrent()
        assertEquals(Tuning.STANDARD, vm.tuner.tuning.value)
    }

    @Test
    fun testEditModeToggle() {
        vm.toggleEditMode(true)
        assertTrue(vm.editModeEnabled.value)
        vm.toggleEditMode(false)
        assertFalse(vm.editModeEnabled.value)
    }
}