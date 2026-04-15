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
        get() = if (hasName()) _name!! else toString()

    /** The standard name of this tuning (if named) including its string representation. */
    val fullName: String
        get() = if (hasName()) "$name ($this)" else toString()

    /**
     * The standard name of this tuning (if named) or an empty string.
     */
    val nameOrBlank: String = if (hasName()) _name!! else ""

    /**
     * The standard name of this tuning if named, null otherwise.
     */
    val rawName: String? = _name

    /** The tuning's key for use in lists. */
    abstract val key: String

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Tuning) return false

        return name == other.name
            && category == other.category
    }

    override fun hashCode(): Int {
        return Objects.hash(name, category)
    }

    /**
     * Returns whether this tuning is equivalent to the specified tuning.
     * @param other The tuning to check equivalence with.
     * @return True if the other tuning has the same strings and instrument as this tuning, false otherwise.
     */
    abstract infix fun equivalentTo(other: Tuning?): Boolean

    /**
     * Returns whether the specified collection contains an equivalent tuning to this tuning.
     * @param tunings The collection of tunings to search.
     * @return True if the collection contains an equivalent tuning, false otherwise.
     *
     * @see equivalentTo
     */
    fun hasEquivalentIn(tunings: Collection<Tuning>): Boolean {
        return tunings.any(::equivalentTo)
    }

    /**
     * Searches for an equivalent tuning in the specified collection.
     * @param tunings The collection of tunings to search.
     * @return The equivalent tuning in the collection, or null if one is not found.
     */
    fun findEquivalentIn(tunings: Collection<Tuning>): Tuning? {
        return tunings.firstOrNull(::equivalentTo)
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

        /** Extended range tuning. */
        EXTENDED,

        /** Miscellaneous tuning.  */
        MISC
    }
}
