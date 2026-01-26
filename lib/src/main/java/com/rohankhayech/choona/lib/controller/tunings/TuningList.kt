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

package com.rohankhayech.choona.lib.controller.tunings

import java.util.Objects
import java.util.SortedMap
import android.content.Context
import com.rohankhayech.choona.lib.controller.fileio.TuningFileIO
import com.rohankhayech.choona.lib.model.tuning.ChromaticTuning
import com.rohankhayech.choona.lib.model.error.ExistingTuningException
import com.rohankhayech.choona.lib.model.tuning.Instrument
import com.rohankhayech.choona.lib.model.tuning.InstrumentTuning
import com.rohankhayech.choona.lib.model.tuning.Tuning
import com.rohankhayech.choona.lib.model.tuning.Tuning.Category
import com.rohankhayech.choona.lib.model.tuning.Tunings
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
    initialCurrentTuning: InstrumentTuning? = null,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

    /** Mutable backing property for [current]. */
    private val _current = MutableStateFlow<Tuning?>(initialCurrentTuning)

    /** The current tuning, or null if N/A. */
    val current = _current.asStateFlow()

    /** Mutable backing property for [chromatic]. */
    private val _chromatic = MutableStateFlow(false)

    /** Whether chromatic tuning is currently selected. */
    val chromatic = _chromatic.asStateFlow()

    /** Mutable backing property for [favourites]. */
    private val _favourites = MutableStateFlow(
        setOf(
            Tunings.STANDARD,
            ChromaticTuning
        )
    )

    /** Set of tunings marked as favourite by the user. */
    val favourites = _favourites.asStateFlow()

    /** Set of instrument tunings marked as favourite by the user. */
    val instrFavs = _favourites.map { favs ->
        favs.filterIsInstance<InstrumentTuning>()
            .map {it}
    }.stateIn(coroutineScope, SharingStarted.Eagerly, listOf(Tunings.STANDARD))

    /** Mutable backing property for [custom]. */
    private val _custom = MutableStateFlow<Set<InstrumentTuning>>(emptySet())

    /** Set of custom tunings added by the user. */
    val custom = _custom.map { c -> c.toSet() }
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), emptySet())

    /** Mutable backing property for [pinned]. */
    private val _pinned = MutableStateFlow<Tuning>(Tunings.STANDARD)

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
        TUNINGS.filter {
            (instrument == null || it.instrument == instrument)
                && (category == null || it.category == category)
        }.groupAndSort()
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), TUNINGS.groupAndSort())

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

    /** Whether the current tuning has been saved (or is a built-in tuning). */
    val currentSaved = combine(current, _custom) { current, custom ->
        current is ChromaticTuning || (current as? InstrumentTuning)?.hasEquivalentIn(custom + Tunings.TUNINGS) == true
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), true)

    /** Whether tunings have been loaded from file. */
    private var loaded = false

    /** Event indicating the specified tuning was deleted. */
    private val _deletedTuning = MutableSharedFlow<InstrumentTuning>(extraBufferCapacity = 1)

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
        TuningFileIO.saveTunings(context, favourites.value, _custom.value, current.value, pinned.value)
    }

    /**
     * Sets the current tuning to the specified [tuning], or its existing equivalent.
     */
    fun setCurrent(tuning: Tuning) {
        _current.update {
            when (tuning) {
                is ChromaticTuning -> tuning
                is InstrumentTuning -> if (tuning.hasName()) tuning else {
                    tuning.findEquivalentIn(_custom.value + Tunings.TUNINGS)?: tuning
                }
                else -> tuning
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
     * @return The named tuning.
     * @throws ExistingTuningException If an equivalent tuning already exists.
     */
    @Throws(ExistingTuningException::class)
    fun addCustom(name: String?, tuning: InstrumentTuning): InstrumentTuning {
        // Check if the tuning already exists.
        tuning.findEquivalentIn(Tunings.TUNINGS)?.let {
            throw ExistingTuningException(it.name, true)
        }
        tuning.findEquivalentIn(_custom.value)?.let {
            throw ExistingTuningException(it.name, false)
        }

        // Add the custom tuning.
        val newTuning = InstrumentTuning(name, tuning)
        _custom.update { it.plusElement(newTuning) }
        if ((current.value as? InstrumentTuning)?.equivalentTo(tuning) == true) {
            _current.update { newTuning }
        }
        if ((pinned.value as? InstrumentTuning)?.equivalentTo(tuning) == true) {
            _pinned.update { newTuning }
        }
        return newTuning
    }

    /**
     * Updates an existing custom [tuning] with the new [updatedTuning].
     * @throws ExistingTuningException If an equivalent tuning already exists (other than the one being updated).
     * @throws IllegalStateException If the tuning to be updated does not exist.
     */
    @Throws(ExistingTuningException::class, IllegalStateException::class)
    fun updateCustom(tuning: Tuning, updatedTuning: Tuning) {
        // Assert that the tuning to be replaced exists.
        check(_custom.value.contains(tuning)) { "The specified tuning does not exist in the custom tuning list." }

        // Check if the updated tuning already exists.
        updatedTuning.findEquivalentIn(Tunings.TUNINGS)?.let {
            throw ExistingTuningException(it.name, true)
        }
        updatedTuning.findEquivalentIn(_custom.value.minusElement(tuning))?.let {
            throw ExistingTuningException(it.name, false)
        }

        // Update the custom tuning.
        _custom.update { it.minusElement(tuning).plusElement(updatedTuning) }
        if (current.value?.tuning?.equivalentTo(tuning) == true) {
            _current.update { TuningEntry.InstrumentTuning(updatedTuning) }
        }
        if (pinned.value.tuning?.equivalentTo(tuning) == true) {
            _pinned.update { TuningEntry.InstrumentTuning(updatedTuning) }
        }
        _favourites.update {
            if (it.contains(TuningEntry.InstrumentTuning(tuning))) {
                it.minusElement(TuningEntry.InstrumentTuning(tuning)).plusElement(TuningEntry.InstrumentTuning(updatedTuning))
            } else it
        }
    }

    /**
     * Removes the specified custom [tuning].
     */
    fun removeCustom(tuning: InstrumentTuning) {
        _custom.update { it.minusElement(tuning) }
        _favourites.update { it.minusElement(tuning) }
        if ((current.value as? InstrumentTuning)?.equivalentTo(tuning) == true) {
            _current.update { InstrumentTuning(null, tuning) }
        }
        if ((pinned.value as? InstrumentTuning)?.equivalentTo(tuning) == true) {
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
        _pinned.update { Tunings.STANDARD }
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

    /** @return Whether this tuning is favourited in the tuning list. */
    fun Tuning.isFavourite(): Boolean {
        return (this is ChromaticTuning && favourites.value.contains(this))
            || this is InstrumentTuning && this.hasEquivalentIn(instrFavs.value)
    }

    /** @return The name of the specified [tuning] if it is saved as a built-in or custom tuning. */
    fun getCanonicalName(tuning: InstrumentTuning): String {
        return tuning.findEquivalentIn(_custom.value + Tunings.TUNINGS)?.name
            ?: tuning.toString()
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
        /** List of built-in tuning entries. */
        private val TUNINGS = Tunings.TUNINGS.toList()

        /** Common tunings, grouped by instrument and category. */
        val GROUPED_TUNINGS = TUNINGS.groupAndSort()

        /**
         * Groups this collection of tunings by instrument - category pairs
         * and sorts the groups by instrument then category.
         *
         * @return Map of tuning groups and their list of tunings.
         */
        fun Collection<InstrumentTuning>.groupAndSort(): SortedMap<Pair<Instrument, Category?>, List<InstrumentTuning>> {
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
