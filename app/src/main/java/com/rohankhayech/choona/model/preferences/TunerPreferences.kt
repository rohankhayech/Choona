/*
 * Copyright (c) 2023 Rohan Khayech
 */

package com.rohankhayech.choona.model.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

/**
 * Data class used to store preferences for the guitar tuner.
 *
 * @property enableStringSelectSound Whether to play a note sound when a string is selected.
 * @property enableInTuneSound Whether to play a sound once the string is in tune.
 * @property displayType Type of tuning offset value to display.
 * @property stringLayout Layout to display string controls.
 * @property useBlackTheme Whether to use full black theme when in dark mode.
 *
 * @author Rohan Khayech
 */
data class TunerPreferences(
    val enableStringSelectSound: Boolean = DEFAULT_ENABLE_STRING_SELECT_SOUND,
    val enableInTuneSound: Boolean = DEFAULT_IN_TUNE_SOUND,
    val displayType: TuningDisplayType = DEFAULT_DISPLAY_TYPE,
    val stringLayout: StringLayout = DEFAULT_STRING_LAYOUT,
    val useBlackTheme: Boolean = DEFAULT_USE_BLACK_THEME,
) {
    companion object {
        // Keys
        val ENABLE_STRING_SELECT_SOUND_KEY = booleanPreferencesKey("enable_string_select_sound")
        val ENABLE_IN_TUNE_SOUND_KEY = booleanPreferencesKey("enable_in_tune_sound")
        val DISPLAY_TYPE_KEY = stringPreferencesKey("display_type")
        val STRING_LAYOUT_KEY = stringPreferencesKey("string_layout")
        val USE_BLACK_THEME_KEY = booleanPreferencesKey("use_black_theme")

        // Defaults
        const val DEFAULT_ENABLE_STRING_SELECT_SOUND = true
        const val DEFAULT_IN_TUNE_SOUND = true
        val DEFAULT_DISPLAY_TYPE = TuningDisplayType.SIMPLE
        val DEFAULT_STRING_LAYOUT = StringLayout.INLINE
        const val DEFAULT_USE_BLACK_THEME = false

        /**
         * Maps the specified android [preferences][prefs] to a [TunerPreferences] object.
         */
        fun fromAndroidPreferences(prefs: Preferences): TunerPreferences {
            return TunerPreferences(
                enableStringSelectSound = prefs[ENABLE_STRING_SELECT_SOUND_KEY] ?: DEFAULT_ENABLE_STRING_SELECT_SOUND,
                enableInTuneSound = prefs[ENABLE_IN_TUNE_SOUND_KEY] ?: DEFAULT_IN_TUNE_SOUND,
                displayType = prefs[DISPLAY_TYPE_KEY]?.let { TuningDisplayType.valueOf(it) } ?: DEFAULT_DISPLAY_TYPE,
                stringLayout = prefs[STRING_LAYOUT_KEY]?.let { StringLayout.valueOf(it) } ?: DEFAULT_STRING_LAYOUT,
                useBlackTheme = prefs[USE_BLACK_THEME_KEY] ?: DEFAULT_USE_BLACK_THEME
            )
        }
    }
}

/** Enum representing the available options for displaying tuning offset. */
enum class TuningDisplayType(val multiplier: Int) {
    /** Displays a simple offset value and whether to tune up or down. */
    SIMPLE(10),
    /** Displays tuning offset in semitones. */
    SEMITONES(1),
    /** Displays tuning offset in cents. */
    CENTS(100)
}

/** Enum representing the available layouts to display string controls. */
enum class StringLayout {
    /** Displays string controls in-line (for electric guitars). */
    INLINE,
    /** Displays string controls side by side (for acoustic guitars). */
    SIDE_BY_SIDE
}

/** Provides the data store for tuner preferences. */
val Context.tunerPreferenceDataStore by preferencesDataStore(name = "tuner_preferences")