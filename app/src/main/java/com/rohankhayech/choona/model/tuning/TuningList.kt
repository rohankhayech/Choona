/*
 * Copyright (c) 2023 Rohan Khayech
 */

package com.rohankhayech.choona.model.tuning

import java.util.*
import android.content.Context
import com.rohankhayech.choona.controller.fileio.TuningFileIO
import com.rohankhayech.music.Tuning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * State holder class which contains lists of favourite and custom tunings.
 *
 * @author Rohan Khayech
 */
class TuningList {

    /** Mutable backing property for [current]. */
    private val _current = MutableStateFlow<Tuning?>(null)

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

    /** Whether tunings have been loaded from file. */
    private var loaded = false

    /**
     * Loads the custom and favourite tunings from file if not yet loaded.
     */
    fun loadTunings(context: Context) {
        if (!loaded) {
            val customTunings = TuningFileIO.loadCustomTunings(context)
            val favouriteTunings = TuningFileIO.loadFavouriteTunings(context)
            _custom.update { customTunings }
            _favourites.update { favouriteTunings }
            loaded = true
        }
    }

    /**
     * Saves the custom and favourite tunings to file.
     */
    fun saveTunings(context: Context) {
        TuningFileIO.saveTunings(context, favourites.value, custom.value)
    }

    /**
     * Sets the current tuning to the specified [tuning], or its existing equivalent.
     */
    fun setCurrent(tuning: Tuning) {
        _current.update {
            if (tuning.hasName()) tuning else {
                tuning.findEquivalentIn(custom.value + Tunings.COMMON) ?: tuning
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
}