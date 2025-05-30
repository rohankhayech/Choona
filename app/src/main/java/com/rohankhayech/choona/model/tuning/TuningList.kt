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

import java.util.Objects
import java.util.SortedMap
import android.content.Context
import com.rohankhayech.choona.controller.fileio.TuningFileIO
import com.rohankhayech.music.Instrument
import com.rohankhayech.music.Tuning
import com.rohankhayech.music.Tuning.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * State holder class which contains lists of favourite and custom tunings.
 *
 * @param initialCurrentTuning Initial current tuning.
 * @param coroutineScope Coroutine scope used to perform filtering actions.
 *
 * @author Rohan Khayech
 */
class TuningList(
    initialCurrentTuning: Tuning? = null,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

    /** Mutable backing property for [current]. */
    private val _current = MutableStateFlow(initialCurrentTuning)

    /** The current tuning, or null if N/A. */
    val current = _current.asStateFlow()

    /** Mutable backing property for [favourites]. */
    private val _favourites = MutableStateFlow<Set<Tuning>>(setOf(
        Tuning.STANDARD))

    /** Set of tunings marked as favourite by the user. */
    val favourites = _favourites.asStateFlow()

    /** Mutable backing property for [custom]. */
    private val _custom = MutableStateFlow<Set<Tuning>>(emptySet())

    /** Set of custom tunings added by the user. */
    val custom = _custom.asStateFlow()

    /** Mutable backing property for [pinned]. */
    private val _pinned = MutableStateFlow<Tuning>(Tuning.STANDARD)

    /** Pinned tuning to open when the app is launched. */
    val pinned = _pinned.asStateFlow()

    /** Mutable backing property for [lastUsed]. */
    private val _lastUsed = MutableStateFlow<Tuning?>(null)

    /** The tuning used last time the app was opened. */
    val lastUsed = _lastUsed.asStateFlow()

    /** Mutable backing property for [instrumentFilter]. */
    private val _instrumentFilter = MutableStateFlow<Instrument?>(null)

    /** Current filter for tuning instrument. */
    val instrumentFilter = _instrumentFilter.asStateFlow()

    /** Mutable backing property for [categoryFilter]. */
    private val _categoryFilter = MutableStateFlow<Category?>(null)

    /** Current filter for tuning category. */
    val categoryFilter = _categoryFilter.asStateFlow()

    /**
     * Current collection of included tunings, filtered by the current instrument and category filters,
     * and grouped by instrument and category.
     */
    val filteredTunings = combine(instrumentFilter, categoryFilter) { instrument, category ->
        Tunings.TUNINGS.filter {
            (instrument == null || it.instrument == instrument)
                && (category == null || it.category == category)
        }.groupAndSort()
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), Tunings.TUNINGS.groupAndSort())

    /** Available category filters and their enabled states. */
    val categoryFilters = instrumentFilter.map { instrument ->
        Category.entries.associateWith {
            it.isValidFilterWith(instrument)
        }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), Category.entries.associateWith { true })

    /** Available instrument filters and their enabled states. */
    val instrumentFilters = categoryFilter.map { category ->
        Instrument.entries
            .dropLast(1)
            .associateWith {
                it.isValidFilterWith(category)
            }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), Instrument.entries.dropLast(1).associateWith { true })

    /** Whether tunings have been loaded from file. */
    private var loaded = false

    /** Event indicating the specified tuning was deleted. */
    private val _deletedTuning = MutableSharedFlow<Tuning>(extraBufferCapacity = 1)

    /** Event indicating the specified tuning was deleted. */
    val deletedTuning = _deletedTuning.asSharedFlow()

    /**
     * Loads the custom and favourite tunings from file if not yet loaded.
     * @param context Android system context used to access the file-system.
     * @return `true` if tunings were successfully loaded, `false` if they were already loaded.
     */
    fun loadTunings(context: Context): Boolean {
        if (!loaded) {
            val customTunings = TuningFileIO.loadCustomTunings(context)
            val favouriteTunings = TuningFileIO.loadFavouriteTunings(context)
            val initial = TuningFileIO.loadInitialTunings(context)
            _custom.update { customTunings }
            _favourites.update { favouriteTunings }
            _lastUsed.update { initial.first }
            initial.second?.let { i -> _pinned.update { i } }
            loaded = true
            return true
        } else return false
    }

    /**
     * Saves the custom and favourite tunings to file.
     *
     * @param context Android system context used to access the file-system
     */
    fun saveTunings(context: Context) {
        TuningFileIO.saveTunings(context, favourites.value, custom.value, current.value, pinned.value)
    }

    /**
     * Sets the current tuning to the specified [tuning], or its existing equivalent.
     */
    fun setCurrent(tuning: Tuning) {
        _current.update {
            if (tuning.hasName()) tuning else {
                tuning.findEquivalentIn(custom.value + Tunings.TUNINGS) ?: tuning
            }
        }
    }

    /**
     * Marks the specified [tuning] as a favourite if [fav] set to true, otherwise un-marks it.
     */
    fun setFavourited(tuning: Tuning, fav: Boolean) {
        if (fav) {
            _favourites.update { it.plusElement(tuning) }
        } else {
            _favourites.update { it.minusElement(tuning) }
        }
    }

    /**
     * Saves the specified custom [tuning] under the given [name].
     */
    fun addCustom(name: String?, tuning: Tuning) {
        val newTuning = Tuning(name, tuning)
        _custom.update { it.plusElement(newTuning) }
        if (current.value?.equivalentTo(tuning) == true) {
            _current.update { newTuning }
        }
        if (pinned.value.equivalentTo(tuning)) {
            _pinned.update { newTuning }
        }
    }

    /**
     * Removes the specified custom [tuning].
     */
    fun removeCustom(tuning: Tuning) {
        _custom.update { it.minusElement(tuning) }
        _favourites.update { it.minusElement(tuning) }
        if (current.value?.equivalentTo(tuning) == true) {
            _current.update { Tuning(null, tuning) }
        }
        if (pinned.value.equivalentTo(tuning)) {
            unpinTuning()
        }
        _deletedTuning.tryEmit(tuning)
    }

    /** Sets the pinned [tuning]. */
    fun setPinned(tuning: Tuning) {
        _pinned.update { tuning }
    }

    /** Unpins the pinned tuning. */
    fun unpinTuning() {
        _pinned.update { Tuning.STANDARD }
    }

    /**
     * Filters the collection of tunings by the specified [instrument] and/or [category] if compatible.
     * If either is not specified the current filter will remain active.
     *
     * @throws IllegalArgumentException If the specified filters are incompatible with each other or the current filters.
     */
    fun filterBy(
        instrument: Instrument? = instrumentFilter.value,
        category: Category? = categoryFilter.value
    ) {
        if (instrument?.isValidFilterWith(category) == false) {
            throw IllegalArgumentException("$instrument and $category are not compatible filters.")
        }

        _instrumentFilter.update { instrument }
        _categoryFilter.update { category }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TuningList

        if (current.value != other.current.value) return false
        if (favourites.value != other.favourites.value) return false
        if (custom.value != other.custom.value) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(current.value, favourites.value, custom.value)
    }

    companion object {
        /** Common tunings, grouped by instrument and category. */
        val GROUPED_TUNINGS = Tunings.TUNINGS.groupAndSort()

        /**
         * Groups this collection of tunings by instrument - category pairs
         * and sorts the groups by instrument then category.
         *
         * @return Map of tuning groups and their list of tunings.
         */
        fun Collection<Tuning>.groupAndSort(): SortedMap<Pair<Instrument, Category?>, List<Tuning>> {
            return groupBy {
                it.instrument to it.category
            }.toSortedMap(
                compareBy ({ it.first }, { it.second })
            )
        }

        /** @return True if this instrument filter is compatible with the specified category filter. */
        private fun Instrument.isValidFilterWith(category: Category?): Boolean {
            return category == null || GROUPED_TUNINGS.contains(this to category)
        }

        /** @return True if this category filter is compatible with the specified instrument filter. */
        private fun Category.isValidFilterWith(instrument: Instrument?): Boolean {
            return instrument == null || GROUPED_TUNINGS.contains(instrument to this)
        }
    }
}