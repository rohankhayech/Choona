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

package com.rohankhayech.choona.model.tuning

import androidx.compose.runtime.Immutable
import com.rohankhayech.music.Tuning

/**
 * Represents an instrument or chromatic tuning option selectable for tuning.
 * This can also be pinned, favourited and stored.
 * @author Rohan Khayech
 */
@Immutable
sealed class TuningEntry(
    /** Name of the tuning. */
    open val name: String?
) {
    /** The instrument tuning, if applicable. */
    abstract val tuning: Tuning?

    /** The tuning's key for use in lists. */
    abstract val key: String

    /** Whether the tuning has a name. */
    fun hasName(): Boolean = !name.isNullOrEmpty()

    /** The chromatic tuning mode. */
    @Immutable
    object ChromaticTuning: TuningEntry("Chromatic") {
        override val tuning: Tuning? = null
        override val key: String = "chromatic"

        override fun toString(): String {
            return "Chromatic Tuning"
        }
    }

    /** An instrument tuning. */
    @Immutable
    class InstrumentTuning(
        override val tuning: Tuning
    ): TuningEntry(if (tuning.hasName()) tuning.name else null) {
        override val key: String
            get() = "${tuning.instrument}-[${tuning.toFullString()}]"

        override fun equals(other: Any?): Boolean {
            if (other === this) return true
            if (other !is InstrumentTuning) return false
            return tuning == other.tuning
        }

        override fun hashCode(): Int {
             return tuning.hashCode()
        }

        override fun toString(): String {
            return "Instrument Tuning: $tuning"
        }
    }
}