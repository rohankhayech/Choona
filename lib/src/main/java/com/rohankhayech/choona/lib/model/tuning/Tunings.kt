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

package com.rohankhayech.choona.lib.model.tuning

import com.rohankhayech.choona.lib.model.tuning.Instrument.BASS
import com.rohankhayech.choona.lib.model.tuning.Instrument.GUITAR
import com.rohankhayech.choona.lib.model.tuning.Instrument.UKULELE
import com.rohankhayech.choona.lib.model.tuning.Tuning.Category.COMMON
import com.rohankhayech.choona.lib.model.tuning.Tuning.Category.EXTENDED
import com.rohankhayech.choona.lib.model.tuning.Tuning.Category.MISC
import com.rohankhayech.choona.lib.model.tuning.Tuning.Category.OPEN
import com.rohankhayech.choona.lib.model.tuning.Tuning.Category.POWER

/**
 * Object defining common guitar tunings.
 * @author Rohan Khayech
 */
@Suppress("unused")
object Tunings {
    /** Internal list of common guitar tunings.  */
    private val tunings: MutableSet<Tuning> = LinkedHashSet()

    /** A list of common guitar tunings.  */
    val TUNINGS: Set<Tuning> = tunings

    val STANDARD = add(Tuning.STANDARD)
    val HALF_STEP_DOWN = add(Tuning.fromString("Half Step Down", GUITAR, COMMON, "D#4 A#3 F#3 C#3 G#2 D#2"))
    val WHOLE_STEP_DOWN = add(Tuning.fromString("Whole Step Down", GUITAR, COMMON, "D4 A3 F3 C3 G2 D2"))
    val DROP_D = add(Tuning.DROP_D)
    val DOUBLE_DROP_D = add(Tuning.fromString("Double Drop D", GUITAR, POWER, "D4 B3 G3 D3 A2 D2"))
    val BASS_STANDARD = add(Tuning.fromString("Standard", BASS, COMMON, "G2 D2 A1 E1"))
    val BASS_DROP_D = add(Tuning.fromString("Drop D", BASS, COMMON, "G2 D2 A1 D1"))
    val BASS_E_FLAT = add(Tuning.fromString("E Flat", BASS, COMMON, "F#2 C#2 G#1 D#1"))
    val BASS_HIGH_C = add(Tuning.fromString("High C", BASS, EXTENDED, "C3 G2 D2 A1 E1"))
    val D_MODAL = add(Tuning.fromString("D Modal", GUITAR, POWER, "D4 A3 G3 D3 A2 D2"))
    val DOUBLE_DADDY = add(Tuning.fromString("Double Daddy", GUITAR, POWER, "D4 A3 D3 D3 A2 D2"))
    val DROP_CS = add(Tuning.fromString("Drop C#", GUITAR, POWER, "D#4 A#3 F#3 C#3 G#2 C#2"))
    val DROP_C = add(Tuning.fromString("Drop C", GUITAR, POWER, "D4 A3 F3 C3 G2 C2"))
    val DROP_B = add(Tuning.fromString("Drop B", GUITAR, POWER, "C#4 G#3 E3 B2 F#2 B1"))
    val DROP_A = add(Tuning.fromString("Drop A", GUITAR, POWER, "B3 F#3 D3 A2 E2 A1"))
    val OPEN_C = add(Tuning.fromString("Open C", GUITAR, OPEN, "E4 C4 G3 C3 G2 C2"))
    val OPEN_E = add(Tuning.fromString("Open E", GUITAR, OPEN, "E4 B3 G#3 E3 B2 E2"))
    val OPEN_F = add(Tuning.fromString("Open F", GUITAR, OPEN, "F4 A3 F3 C3 F2 C2"))
    val OPEN_G = add(Tuning.fromString("Open G", GUITAR, OPEN, "D4 B3 G3 D3 G2 D2"))
    val OPEN_A = add(Tuning.fromString("Open A", GUITAR, OPEN, "E4 C#4 A3 E3 A2 E2"))
    val OPEN_AM = add(Tuning.fromString("Open Am", GUITAR, OPEN, "E4 C4 A3 E3 A2 E2"))
    val OPEN_EM = add(Tuning.fromString("Open Em", GUITAR, OPEN, "E4 B3 G3 E3 B2 E2"))
    val OPEN_D = add(Tuning.fromString("Open D", GUITAR, OPEN, "D4 A3 F#3 D3 A2 D2"))
    val OPEN_DM = add(Tuning.fromString("Open Dm", GUITAR, OPEN, "D4 A3 F3 D3 A2 D2"))
    val G_MODAL = add(Tuning.fromString("G Modal", GUITAR, MISC, "D4 C4 G3 D3 G2 D2"))
    val ALL_4TH = add(Tuning.fromString("All 4th", GUITAR, MISC, "F4 C4 G3 D3 A2 E2"))
    val NST = add(Tuning.fromString("New Standard Tuning", GUITAR, MISC, "G4 E4 A3 D3 G2 C2"))
    val UKULELE_STANDARD = add(Tuning.fromString("Standard", UKULELE, COMMON, "A4 E4 C4 G4"))
    val SEVEN_STRING = add(Tuning.fromString("7-String", GUITAR, EXTENDED, "E4 B3 G3 D3 A2 E2 B1"))
    val EIGHT_STRING = add(Tuning.fromString("8-String", GUITAR, EXTENDED, "E4 B3 G3 D3 A2 E2 B1 F#1"))
    val TWELVE_STRING = add(Tuning.fromString("12-String", GUITAR, EXTENDED, "E4 E4 B3 B3 G3 G4 D3 D4 A2 A3 E2 E3"))

    /** Adds the specified tuning to the list and returns it. */
    private fun add(tuning: Tuning): Tuning {
        tunings.add(tuning)
        return tuning
    }
}