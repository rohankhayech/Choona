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

package com.rohankhayech.choona.lib.model.tuning

import com.rohankhayech.music.Instrument
import com.rohankhayech.music.Tuning

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

    val STANDARD: Tuning = add(Tuning.STANDARD)
    val HALF_STEP_DOWN: Tuning = add(Tuning.fromString("Half Step Down", Instrument.GUITAR, Tuning.Category.COMMON, "D#4 A#3 F#3 C#3 G#2 D#2"))
    val WHOLE_STEP_DOWN: Tuning = add(Tuning.fromString("Whole Step Down", Instrument.GUITAR, Tuning.Category.COMMON, "D4 A3 F3 C3 G2 D2"))
    val DROP_D: Tuning = add(Tuning.DROP_D)
    val DOUBLE_DROP_D: Tuning = add(Tuning.fromString("Double Drop D", Instrument.GUITAR, Tuning.Category.POWER, "D4 B3 G3 D3 A2 D2"))
    val BASS_STANDARD: Tuning = add(Tuning.fromString("Standard", Instrument.BASS, Tuning.Category.COMMON, "G2 D2 A1 E1"))
    val BASS_DROP_D: Tuning = add(Tuning.fromString("Drop D", Instrument.BASS, Tuning.Category.COMMON, "G2 D2 A1 D1"))
    val BASS_E_FLAT: Tuning = add(Tuning.fromString("E Flat", Instrument.BASS, Tuning.Category.COMMON, "F#2 C#2 G#1 D#1"))
    val D_MODAL: Tuning = add(Tuning.fromString("D Modal", Instrument.GUITAR, Tuning.Category.POWER, "D4 A3 G3 D3 A2 D2"))
    val DOUBLE_DADDY: Tuning = add(Tuning.fromString("Double Daddy", Instrument.GUITAR, Tuning.Category.POWER, "D4 A3 D3 D3 A2 D2"))
    val DROP_CS: Tuning = add(Tuning.fromString("Drop C#", Instrument.GUITAR, Tuning.Category.POWER, "D#4 A#3 F#3 C#3 G#2 C#2"))
    val DROP_C: Tuning = add(Tuning.fromString("Drop C", Instrument.GUITAR, Tuning.Category.POWER, "D4 A3 F3 C3 G2 C2"))
    val DROP_B: Tuning = add(Tuning.fromString("Drop B", Instrument.GUITAR, Tuning.Category.POWER, "C#4 G#3 E3 B2 F#2 B1"))
    val DROP_A: Tuning = add(Tuning.fromString("Drop A", Instrument.GUITAR, Tuning.Category.POWER, "B3 F#3 D3 A2 E2 A1"))
    val OPEN_C: Tuning = add(Tuning.fromString("Open C", Instrument.GUITAR, Tuning.Category.OPEN, "E4 C4 G3 C3 G2 C2"))
    val OPEN_E: Tuning = add(Tuning.fromString("Open E", Instrument.GUITAR, Tuning.Category.OPEN, "E4 B3 G#3 E3 B2 E2"))
    val OPEN_F: Tuning = add(Tuning.fromString("Open F", Instrument.GUITAR, Tuning.Category.OPEN, "F4 A3 F3 C3 F2 C2"))
    val OPEN_G: Tuning = add(Tuning.fromString("Open G", Instrument.GUITAR, Tuning.Category.OPEN, "D4 B3 G3 D3 G2 D2"))
    val OPEN_A: Tuning = add(Tuning.fromString("Open A", Instrument.GUITAR, Tuning.Category.OPEN, "E4 C#4 A3 E3 A2 E2"))
    val OPEN_AM: Tuning = add(Tuning.fromString("Open Am", Instrument.GUITAR, Tuning.Category.OPEN, "E4 C4 A3 E3 A2 E2"))
    val OPEN_EM: Tuning = add(Tuning.fromString("Open Em", Instrument.GUITAR, Tuning.Category.OPEN, "E4 B3 G3 E3 B2 E2"))
    val OPEN_D: Tuning = add(Tuning.fromString("Open D", Instrument.GUITAR, Tuning.Category.OPEN, "D4 A3 F#3 D3 A2 D2"))
    val OPEN_DM: Tuning = add(Tuning.fromString("Open Dm", Instrument.GUITAR, Tuning.Category.OPEN, "D4 A3 F3 D3 A2 D2"))
    val G_MODAL: Tuning = add(Tuning.fromString("G Modal", Instrument.GUITAR, Tuning.Category.MISC, "D4 C4 G3 D3 G2 D2"))
    val ALL_4TH: Tuning = add(Tuning.fromString("All 4th", Instrument.GUITAR, Tuning.Category.MISC, "F4 C4 G3 D3 A2 E2"))
    val NST: Tuning = add(Tuning.fromString("New Standard Tuning", Instrument.GUITAR, Tuning.Category.MISC, "G4 E4 A3 D3 G2 C2"))
    val UKULELE_STANDARD: Tuning = add(Tuning.fromString("Standard", Instrument.UKULELE, Tuning.Category.COMMON, "A4 E4 C4 G4"))

    /** Adds the specified tuning to the list and returns it. */
    private fun add(tuning: Tuning): Tuning {
        tunings.add(tuning)
        return tuning
    }
}