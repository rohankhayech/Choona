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

package com.rohankhayech.choona.controller.fileio;

import android.content.Context;

import com.rohankhayech.music.Instrument;
import com.rohankhayech.music.Tuning;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The Tuning File I/O class handles encoding/decoding and saving/loading custom tunings and tuning metadata to/from file.
 *
 * @author Rohan Khayech
 */
public final class TuningFileIO {

    /** Private constructor to prevent instantiation. */
    private TuningFileIO() {}

    /**
     * Loads the user's custom tunings from file.
     * @param context Android system context used to access the filesystem.
     * @return The set of stored custom tunings.
     */
    public static Set<Tuning> loadCustomTunings(Context context) {
        try {
            String json = FileIO.readFromFile(context, "tunings_custom"+FileIO.FILE_EXT);
            return parseTunings(json);
        } catch (IOException e) {
            return new LinkedHashSet<>();
        }
    }

    /**
     * Loads the user's favourite tunings from file.
     * @param context Android system context used to access the filesystem.
     * @return The set of stored favourite tunings.
     */
    public static Set<Tuning> loadFavouriteTunings(Context context) {
        try {
            String json = FileIO.readFromFile(context, "tunings_favourite"+FileIO.FILE_EXT);
            return parseTunings(json);
        } catch (IOException e) {
            Set<Tuning> defSet = new LinkedHashSet<>();
            defSet.add(Tuning.STANDARD);
            return defSet;
        }
    }

    /**
     * Saves the user's favourite and custom tunings to file.
     * @param context Android system context used to access the filesystem.
     * @param favourites Set of favourite tunings to save.
     * @param custom Set of custom tunings to save.
     */
    public static void saveTunings(Context context, Set<Tuning> favourites, Set<Tuning> custom) {
        String customJSON = encodeTunings(custom);
        String favouritesJSON = encodeTunings(favourites);
        try {
            FileIO.writeToFile(context, "tunings_custom"+FileIO.FILE_EXT, customJSON);
            FileIO.writeToFile(context, "tunings_favourite"+FileIO.FILE_EXT, favouritesJSON);
        } catch (IOException e) {
            throw new TuningIOException("Tunings could not be saved: " + e.getMessage(), e);
        }
    }

    /**
     * Parses the set of tunings from the specified JSON string.
     * @param tuningsJSON The JSON string representation of the set of tunings.
     * @return A set of tunings represented by the JSON string.
     */
    static Set<Tuning> parseTunings(String tuningsJSON) {
        Set<Tuning> tunings = new LinkedHashSet<>();

        try {
            // Retrieve the JSON object from the JSON string.
            JSONObject tuningsObj = new JSONObject(tuningsJSON);

            // For each stored tuning.
            JSONArray tuningsArr = tuningsObj.getJSONArray("tunings");
            for (int i=0; i<tuningsArr.length(); i++) {
                // Retrieve tuning JSON.
                JSONObject tuningObj = tuningsArr.getJSONObject(i);

                // Retrieve tuning data
                String name = tuningObj.optString("name", null); // Name should be null if absent.
                Instrument instrument = Instrument.valueOf(tuningObj.optString("instrument", Tuning.DEFAULT_INSTRUMENT.toString()));
                String categoryString = tuningObj.optString("category"); // Category should be null if absent.
                Tuning.Category category;
                if (!categoryString.isEmpty()) {
                    category = Tuning.Category.valueOf(categoryString);
                } else {
                    category = null;
                }
                String strings = tuningObj.getString("strings");

                // Create a tuning object.
                Tuning tuning = Tuning.fromString(name, instrument, category, strings);

                // Add the tuning to the list.
                tunings.add(tuning);
            }

            return tunings;
        } catch (JSONException e) {
            throw new TuningIOException("Tunings could not be loaded: " + e.getMessage(), e);
        }
    }

    /**
     * Encodes the specified set of tunings to JSON.
     * @param tunings The tunings to encode.
     * @return A JSON string representation of the set of tunings.
     */
    static String encodeTunings(Set<Tuning> tunings) {
        Objects.requireNonNull(tunings);

        JSONArray tuningsArr = new JSONArray();
        try {
            for (Tuning tuning : tunings) {
                // Create a new JSON object for the tuning.
                JSONObject tuningObj = new JSONObject();

                // Encode the tuning data to JSON.
                if (tuning.hasName()) tuningObj.put("name", tuning.getName());
                tuningObj.put("instrument", tuning.getInstrument().toString());
                if (tuning.hasCategory()) tuningObj.put("category", tuning.getCategory());
                tuningObj.put("strings", tuning.toFullString());

                // Add the tuning to the JSON array.
                tuningsArr.put(tuningObj);
            }

            JSONObject tuningsObj = new JSONObject();
            tuningsObj.put("tunings", tuningsArr);

            return tuningsObj.toString();
        } catch (JSONException e) {
            throw new TuningIOException("Tunings could not be saved: "+e.getMessage(), e);
        }
    }

    /**
     * Thrown when there is an error saving/encoding or loading/parsing a guitar tab.
     */
    public static class TuningIOException extends RuntimeException {
        private TuningIOException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
