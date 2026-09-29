/*
 * Choona - Guitar Tuner
 * Copyright (C) 2026 Utkarsh Verma
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

package com.rohankhayech.choona.lib.controller.tuner

import com.rohankhayech.choona.lib.model.tuning.Notes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CaptureFormatTest {

    private val lowestHz = Notes.getPitch(Tuner.LOWEST_NOTE)

    @Test
    fun analysisWindowCoversTwoPeriodsOfTheLowestNote() {
        assertEquals(2048, MicrophoneCapture.analysisSamples(22050, lowestHz))
        val native = MicrophoneCapture.analysisSamples(48000, lowestHz)
        assertEquals(4096, native)
        assertTrue(native / 2 >= (48000 / lowestHz).toInt())
    }
}
