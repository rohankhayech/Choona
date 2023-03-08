/*
 * Copyright (c) 2023 Rohan Khayech
 */

package com.rohankhayech.choona.view.activity

import com.rohankhayech.music.Tuning
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

/**
 * Test harness for the TunerActivity view model.
 *
 * @author Rohan Khayech
 */
class TunerActivityViewModelTest {

    private lateinit var vm: TunerActivityViewModel

    @Before
    fun setUp() {
        vm = TunerActivityViewModel()
    }

    @Test
    fun testInitial() {
        assertFalse(vm.tuningSelectorOpen.value)
    }

    @Test
    fun testOpenTuningSelector() {
        vm.openTuningSelector()
        assertTrue(vm.tuningSelectorOpen.value)
        assertEquals(vm.tuner.tuning.value, vm.tuningList.current.value)
    }

    @Test
    fun testDismissTuningSelector() {
        vm.openTuningSelector()
        vm.dismissTuningSelector()
        assertFalse(vm.tuningSelectorOpen.value)
    }

    @Test
    fun testSelectTuning() {
        vm.openTuningSelector()
        vm.selectTuning(Tuning.DROP_D)
        assertFalse(vm.tuningSelectorOpen.value)
        assertEquals(Tuning.DROP_D, vm.tuner.tuning.value)
    }
}