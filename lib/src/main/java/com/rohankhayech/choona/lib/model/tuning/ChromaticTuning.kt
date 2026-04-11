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

import androidx.compose.runtime.Immutable

/** The chromatic tuning mode. */
@Immutable
object ChromaticTuning: Tuning("Chromatic", Category.MISC) {
    override val key: String = "chromatic"

    override fun toString(): String {
        return name
    }

    /**
     * Returns whether this tuning is equivalent to the specified tuning.
     * @param other The tuning to check equivalence with.
     * @return True if the other tuning has the same strings and instrument as this tuning, false otherwise.
     */
    override fun equivalentTo(other: Tuning?): Boolean {
        return this === other
    }
}