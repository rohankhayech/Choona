/*
 * ChromaticTuner - Arma Rizki
 *
 * Includes modified source code from Choona Guitar Tuner
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


package com.armarizki.chromatic.model.tuning

import java.util.Objects
import java.util.SortedMap
import android.content.Context
import com.armarizki.chromatic.controller.fileio.TuningFileIO
import com.armarizki.music.Instrument
import com.armarizki.music.Tuning
import com.armarizki.music.Tuning.Category
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


class TuningList(
    initialCurrentTuning: Tuning? = null,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

     
    private val _current = MutableStateFlow<TuningEntry?>(initialCurrentTuning?.let { TuningEntry.InstrumentTuning(it) })

     
    val current = _current.asStateFlow()

     
    private val _chromatic = MutableStateFlow(false)

     
    val chromatic = _chromatic.asStateFlow()

     
    private val _favourites = MutableStateFlow(setOf(
        TuningEntry.InstrumentTuning(Tuning.STANDARD),
        TuningEntry.ChromaticTuning
    ))

     
    val favourites = _favourites.asStateFlow()

     
    val instrFavs = _favourites.map { favs ->
        favs.filterIsInstance<TuningEntry.InstrumentTuning>()
            .map {it.tuning}
    }.stateIn(coroutineScope, SharingStarted.Eagerly, listOf(Tuning.STANDARD))

     
    private val _custom = MutableStateFlow<Set<Tuning>>(emptySet())

     
    val custom = _custom.map { c -> c.map { TuningEntry.InstrumentTuning(it) }.toSet() }
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), emptySet())

     
    private val _pinned = MutableStateFlow<TuningEntry>(TuningEntry.InstrumentTuning(Tuning.STANDARD))

     
    val pinned = _pinned.asStateFlow()

     
    private val _chromaticPinned = MutableStateFlow(false)

     
    val chromaticPinned = _chromaticPinned.asStateFlow()

     
    private val _lastUsed = MutableStateFlow<TuningEntry?>(null)

     
    val lastUsed = _lastUsed.asStateFlow()

     
    private val _instrumentFilter = MutableStateFlow<Instrument?>(null)

     
    val instrumentFilter = _instrumentFilter.asStateFlow()

     
    private val _categoryFilter = MutableStateFlow<Category?>(null)

     
    val categoryFilter = _categoryFilter.asStateFlow()

     
    val filteredTunings = combine(instrumentFilter, categoryFilter) { instrument, category ->
        TUNINGS.filter {
            (instrument == null || it.tuning.instrument == instrument)
                && (category == null || it.tuning.category == category)
        }.groupAndSort()
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), TUNINGS.groupAndSort())

     
    val categoryFilters = instrumentFilter.map { instrument ->
        Category.entries.associateWith {
            it.isValidFilterWith(instrument)
        }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), Category.entries.associateWith { true })

     
    val instrumentFilters = categoryFilter.map { category ->
        Instrument.entries
            .associateWith {
                it.isValidFilterWith(category)
            }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), Instrument.entries.dropLast(1).associateWith { true })

     
    val currentSaved = combine(current, _custom) { current, custom ->
        current is TuningEntry.ChromaticTuning || current?.tuning?.hasEquivalentIn(custom + Tunings.TUNINGS) == true
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), true)

     
    private var loaded = false

     
    private val _deletedTuning = MutableSharedFlow<Tuning>(extraBufferCapacity = 1)

     
    val deletedTuning = _deletedTuning.asSharedFlow()

     
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

     
    fun saveTunings(context: Context) {
        TuningFileIO.saveTunings(context, favourites.value, _custom.value, current.value, pinned.value)
    }

     
    fun setCurrent(tuning: TuningEntry) {
        _current.update {
            when (tuning) {
                is TuningEntry.ChromaticTuning -> tuning
                is TuningEntry.InstrumentTuning -> if (tuning.hasName()) tuning else {
                    tuning.tuning.findEquivalentIn(_custom.value + Tunings.TUNINGS)?.let {
                        TuningEntry.InstrumentTuning(it)
                    } ?: tuning
                }
            }
        }
    }

     
    fun setFavourited(tuning: TuningEntry, fav: Boolean) {
        if (fav) {
            _favourites.update { it.plusElement(tuning) }
        } else {
            _favourites.update { it.minusElement(tuning) }
        }
    }

     
    fun addCustom(name: String?, tuning: Tuning): Tuning {
        val newTuning = Tuning(name, tuning)
        _custom.update { it.plusElement(newTuning) }
        if (current.value?.tuning?.equivalentTo(tuning) == true) {
            _current.update { TuningEntry.InstrumentTuning(newTuning) }
        }
        if (pinned.value.tuning?.equivalentTo(tuning) == true) {
            _pinned.update { TuningEntry.InstrumentTuning(newTuning) }
        }
        return newTuning
    }

     
    fun removeCustom(tuning: Tuning) {
        _custom.update { it.minusElement(tuning) }
        _favourites.update { it.minusElement(TuningEntry.InstrumentTuning(tuning)) }
        if (current.value?.tuning?.equivalentTo(tuning) == true) {
            _current.update { TuningEntry.InstrumentTuning(Tuning(null, tuning)) }
        }
        if (pinned.value.tuning?.equivalentTo(tuning) == true) {
            unpinTuning()
        }
        _deletedTuning.tryEmit(tuning)
    }

     
    fun setPinned(tuning: TuningEntry) {
        _pinned.update { tuning }
    }

     
    fun unpinTuning() {
        _pinned.update { TuningEntry.InstrumentTuning(Tuning.STANDARD) }
    }

     
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

     
    fun TuningEntry.isFavourite(): Boolean {
        return (this is TuningEntry.ChromaticTuning && favourites.value.contains(this)) ||
            this.tuning?.hasEquivalentIn(instrFavs.value) == true
    }

     
    fun TuningEntry.InstrumentTuning.getCanonicalName(): String {
        return this.tuning.findEquivalentIn(_custom.value + Tunings.TUNINGS)?.name
            ?: this.tuning.toString()
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
         
        private val TUNINGS = Tunings.TUNINGS.map {
            TuningEntry.InstrumentTuning(it)
        }

         
        val GROUPED_TUNINGS = TUNINGS.groupAndSort()

         
        fun Collection<TuningEntry.InstrumentTuning>.groupAndSort(): SortedMap<Pair<Instrument, Category?>, List<TuningEntry.InstrumentTuning>> {
            return groupBy {
                it.tuning.instrument to it.tuning.category
            }.toSortedMap(
                compareBy ({ it.first }, { it.second })
            )
        }

         
        private fun Instrument.isValidFilterWith(category: Category?): Boolean {
            return category == null || GROUPED_TUNINGS.contains(this to category)
        }

         
        private fun Category.isValidFilterWith(instrument: Instrument?): Boolean {
            return instrument == null || GROUPED_TUNINGS.contains(instrument to this)
        }
    }
}