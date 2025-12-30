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

package com.rohankhayech.choona.lib.controller.tunings

import com.rohankhayech.choona.lib.controller.tuner.Tuner
import com.rohankhayech.choona.lib.model.tuning.Tuning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Holds a tuning and allows retuning of the whole tuning or individual strings.
 *
 * @param tuning The guitar tuning used for comparison.
 *
 * @author Rohan Khayech
 */
open class TuningEditor(
    tuning: Tuning = Tuning.STANDARD
) {
    /** Mutable backing property for [tuning]. */
    protected val _tuning = MutableStateFlow(tuning)

    /** Guitar tuning used for comparison. */
    val tuning = _tuning.asStateFlow()

    /** Tunes all strings in the tuning up by one semitone */
    open fun tuneUp(): Boolean {
        return if (tuning.value.max().rootNoteIndex < Tuner.Companion.HIGHEST_NOTE) {
            _tuning.update { it.higherTuning() }
            true
        } else false
    }

    /** Tunes all strings in the tuning down by one semitone */
    open fun tuneDown(): Boolean {
        return if (tuning.value.min().rootNoteIndex > Tuner.Companion.LOWEST_NOTE) {
            _tuning.update { it.lowerTuning() }
            true
        } else false
    }

    /** Tunes the [nth][n] string in the tuning up by one semitone.
     * @return False if the string could not be tuned any lower, true otherwise.
     */
    open fun tuneStringUp(n: Int): Boolean {
        require(n in 0 until tuning.value.numStrings()) { "Invalid string index." }

        return if (tuning.value.getString(n).rootNoteIndex < Tuner.Companion.HIGHEST_NOTE) {
            _tuning.update { tuning ->
                tuning.withString(n, tuning.getString(n).higherString())
            }
            true
        } else false
    }

    /**
     * Tunes the [nth][n] string in the tuning down by one semitone.
     * @return False if the string could not be tuned any lower, true otherwise.
     */
    open fun tuneStringDown(n: Int): Boolean {
        require(n in 0 until tuning.value.numStrings()) { "Invalid string index." }

        return if (tuning.value.getString(n).rootNoteIndex > Tuner.Companion.LOWEST_NOTE) {
            _tuning.update { tuning ->
                tuning.withString(n, tuning.getString(n).lowerString())
            }
            true
        } else false
    }
}