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

import java.util.Objects
import androidx.compose.runtime.Immutable

@Immutable
abstract class Tuning protected constructor(
    /** The standard name of this tuning.  */
    name: String?,
    /** The category of tuning.  */
    @JvmField val category: Category?
) {
    private val _name = name

    /** @return True if this tuning is named, false otherwise. */
    fun hasName(): Boolean {
        return !_name.isNullOrBlank()
    }

    /** @return True if this tuning has a category, false otherwise.
     */
    fun hasCategory(): Boolean {
        return category != null
    }

    /**
     * @return The standard name of this tuning, or the string representation if it is not named.
     */
    val name: String
        get() = _name ?: toString()

    /** The standard name of this tuning (if named) including it's string representation. */
    val fullName: String
        get() = if (hasName()) "$name ($this)" else toString()

    /** The tuning's key for use in lists. */
    abstract val key: String

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Tuning) return false

        val o = other as InstrumentTuning
        return name == o.name
            && category == o.category
    }

    override fun hashCode(): Int {
        return Objects.hash(name, category)
    }

    /**
     * Enum describing tuning categories.
     */
    enum class Category {

        /** Common tuning.  */
        COMMON,

        /** Power chord tuning.  */
        POWER,

        /** Open chord tuning.  */
        OPEN,

        /** Miscellaneous tuning.  */
        MISC
    }
}
