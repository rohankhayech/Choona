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

package com.armarizki.chromatic.controller.fileio

import java.io.IOException
import java.util.Objects
import android.content.Context
import androidx.annotation.VisibleForTesting
import com.armarizki.chromatic.model.tuning.TuningEntry
import com.armarizki.music.Instrument
import com.armarizki.music.Tuning
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object TuningFileIO {
     
    fun loadCustomTunings(context: Context): Set<Tuning> {
        try {
            val json = FileIO.readFromFile(context, "tunings_custom" + FileIO.FILE_EXT)
            return parseTunings(json)
                .filterIsInstance<TuningEntry.InstrumentTuning>()
                .map { it -> it.tuning }
                .toHashSet()
        } catch (_: IOException) {
            return LinkedHashSet()
        }
    }

    fun loadFavouriteTunings(context: Context): Set<TuningEntry> {
        try {
            val json = FileIO.readFromFile(context, "tunings_favourite" + FileIO.FILE_EXT)
            return parseTunings(json)
        } catch (_: IOException) {
            val defSet: MutableSet<TuningEntry> = LinkedHashSet()
            defSet.add(TuningEntry.InstrumentTuning(Tuning.STANDARD))
            defSet.add(TuningEntry.ChromaticTuning)
            return defSet
        }
    }

    fun loadInitialTunings(context: Context): Pair<TuningEntry?, TuningEntry?> {
        try {
            val json = FileIO.readFromFile(context, "tunings_initial" + FileIO.FILE_EXT)
            return parseInitialTunings(json)
        } catch (_: IOException) {
            return Pair(null, null)
        }
    }

    fun saveTunings(context: Context, favourites: Set<TuningEntry>, custom: Set<Tuning>, lastUsed: TuningEntry?, initial: TuningEntry?) {
        val customJSON = encodeTunings(custom.map { TuningEntry.InstrumentTuning(it) }.toSet() )
        val favouritesJSON = encodeTunings(favourites)
        val initialJSON = encodeInitialTunings(lastUsed, initial)
        try {
            FileIO.writeToFile(context, "tunings_custom" + FileIO.FILE_EXT, customJSON)
            FileIO.writeToFile(context, "tunings_favourite" + FileIO.FILE_EXT, favouritesJSON)
            FileIO.writeToFile(context, "tunings_initial" + FileIO.FILE_EXT, initialJSON)
        } catch (e: IOException) {
            throw TuningIOException("Tunings could not be saved: " + e.message, e)
        }
    }

    @VisibleForTesting
    fun parseTunings(tuningsJSON: String): Set<TuningEntry> {
        val tunings: MutableSet<TuningEntry> = LinkedHashSet()

        try {
            // Retrieve the JSON object from the JSON string.
            val tuningsObj = JSONObject(tuningsJSON)

            // For each stored tuning.
            val tuningsArr = tuningsObj.getJSONArray("tunings")
            for (i in 0 until tuningsArr.length()) {
                // Retrieve tuning JSON.
                val tuningObj = tuningsArr.getJSONObject(i)

                // Parse the tuning from the JSON object.
                val tuning = parseTuning(tuningObj)

                // Add the tuning to the list.
                tunings.add(tuning)
            }

            return tunings
        } catch (e: JSONException) {
            throw TuningIOException("Tunings could not be loaded: " + e.message, e)
        }
    }

     
    @VisibleForTesting
    fun encodeTunings(tunings: Set<TuningEntry>): String {
        Objects.requireNonNull(tunings)

        val tuningsArr = JSONArray()
        try {
            for (tuning in tunings) {
                // Encode the tuning to JSON.
                val tuningObj = encodeTuning(tuning)

                // Add the tuning to the JSON array.
                tuningsArr.put(tuningObj)
            }

            val tuningsObj = JSONObject()
            tuningsObj.put("tunings", tuningsArr)

            return tuningsObj.toString()
        } catch (e: JSONException) {
            throw TuningIOException("Tunings could not be saved: " + e.message, e)
        }
    }

     
    private fun parseInitialTunings(tuningsJSON: String): Pair<TuningEntry?, TuningEntry?> {
        try {
            // Retrieve the JSON object from the JSON string.
            val tuningsObj = JSONObject(tuningsJSON)

            // Retrieve tuning data
            val lastUsed = if (tuningsObj.has("lastUsed")) {
                parseTuning(tuningsObj.getJSONObject("lastUsed"))
            } else null
            val initial = if (tuningsObj.has("initial")) {
                parseTuning(tuningsObj.getJSONObject("initial"))
            } else null

            return Pair(lastUsed, initial)
        } catch (e: JSONException) {
            throw TuningIOException("Tunings could not be loaded: " + e.message, e)
        }
    }

     
    private fun encodeInitialTunings(lastUsed: TuningEntry?, initial: TuningEntry?): String {
        try {
            val tuningsObj = JSONObject()
            lastUsed?.let {tuningsObj.put("lastUsed", encodeTuning(it)) }
            initial?.let {tuningsObj.put("initial", encodeTuning(it)) }
            return tuningsObj.toString()
        } catch (e: JSONException) {
            throw TuningIOException("Tunings could not be saved: " + e.message, e)
        }
    }

     
    @Throws(JSONException::class)
    private fun parseTuning(tuningObj: JSONObject): TuningEntry {
        // Retrieve tuning data
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") // Name should be null if absent.
        val name: String? = tuningObj.optString("name", null)

        val instr = tuningObj.optString("instrument", Tuning.DEFAULT_INSTRUMENT.toString())

        if (instr == "chromatic") {
            return TuningEntry.ChromaticTuning
        }

        val instrument: Instrument = try {
            Instrument.valueOf(instr.uppercase())
        } catch (_: IllegalArgumentException) {
            Instrument.GUITAR
        }

        val categoryString = tuningObj.optString("category")
        val category = if (categoryString.isNotEmpty()) {
            Tuning.Category.valueOf(categoryString)
        } else {
            null // Category should be null if absent.
        }
        val strings = tuningObj.getString("strings")

        // Create a tuning object.
        val tuning = Tuning.fromString(name, instrument, category, strings)
        return TuningEntry.InstrumentTuning(tuning)
    }

     
    @Throws(JSONException::class)
    private fun encodeTuning(tuningEntry: TuningEntry): JSONObject {
        // Create a new JSON object for the tuning.
        val tuningObj = JSONObject()

        // Encode the tuning data to JSON.
        if (tuningEntry.hasName()) tuningObj.put("name", tuningEntry.name)

        if (tuningEntry is TuningEntry.ChromaticTuning) {
            tuningObj.put("instrument", "chromatic")
        } else if (tuningEntry is TuningEntry.InstrumentTuning) {
            val tuning = tuningEntry.tuning
            tuningObj.put("instrument", tuning.instrument.toString())
            if (tuning.hasCategory()) tuningObj.put("category", tuning.category)
            tuningObj.put("strings", tuning.toFullString())
        }

        return tuningObj
    }
}

 
class TuningIOException(message: String, cause: Throwable) : RuntimeException(message, cause)