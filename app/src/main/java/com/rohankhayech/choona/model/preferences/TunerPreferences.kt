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

package com.rohankhayech.choona.model.preferences

import android.content.Context
import androidx.compose.runtime.Immutable
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
 * @property useDynamicColor Whether to use dynamic color for the app theme.
 * @property editModeDefault Whether to enable tuning edit functionality.
 * @property initialTuning The default tuning used when opening the app.
 *
 * @author Rohan Khayech
 */
@Immutable
data class TunerPreferences(
    val enableStringSelectSound: Boolean = DEFAULT_ENABLE_STRING_SELECT_SOUND,
    val enableInTuneSound: Boolean = DEFAULT_IN_TUNE_SOUND,
    val displayType: TuningDisplayType = DEFAULT_DISPLAY_TYPE,
    val stringLayout: StringLayout = DEFAULT_STRING_LAYOUT,
    val useBlackTheme: Boolean = DEFAULT_USE_BLACK_THEME,
    val useDynamicColor: Boolean = DEFAULT_USE_DYNAMIC_COLOR,
    val editModeDefault: Boolean = DEFAULT_EDIT_MODE_DEFAULT,
    val initialTuning: InitialTuningType = DEFAULT_INITIAL_TUNING,
    val reviewPromptLaunches: Int = 0
) {
    companion object {
        // Keys
        val ENABLE_STRING_SELECT_SOUND_KEY = booleanPreferencesKey("enable_string_select_sound")
        val ENABLE_IN_TUNE_SOUND_KEY = booleanPreferencesKey("enable_in_tune_sound")
        val DISPLAY_TYPE_KEY = stringPreferencesKey("display_type")
        val STRING_LAYOUT_KEY = stringPreferencesKey("string_layout")
        val USE_BLACK_THEME_KEY = booleanPreferencesKey("use_black_theme")
        val USE_DYNAMIC_COLOR_KEY = booleanPreferencesKey("use_dynamic_color")
        val EDIT_MODE_DEFAULT_KEY = booleanPreferencesKey("edit_mode_default")
        val INITIAL_TUNING_KEY = stringPreferencesKey("initial_tuning")
        val REVIEW_PROMPT_LAUNCHES_KEY = stringPreferencesKey("review_prompt_launches")

        // Defaults
        const val DEFAULT_ENABLE_STRING_SELECT_SOUND = true
        const val DEFAULT_IN_TUNE_SOUND = true
        val DEFAULT_DISPLAY_TYPE = TuningDisplayType.SIMPLE
        val DEFAULT_STRING_LAYOUT = StringLayout.INLINE
        const val DEFAULT_USE_BLACK_THEME = false
        const val DEFAULT_EDIT_MODE_DEFAULT = false
        val DEFAULT_INITIAL_TUNING = InitialTuningType.PINNED
        const val DEFAULT_USE_DYNAMIC_COLOR = false
        /** Maximum number of times to prompt the user for a review. */
        const val REVIEW_PROMPT_ATTEMPTS = 3

        /**
         * Maps the specified android [preferences][prefs] to a [TunerPreferences] object.
         */
        fun fromAndroidPreferences(prefs: Preferences): TunerPreferences {
            return TunerPreferences(
                enableStringSelectSound = prefs[ENABLE_STRING_SELECT_SOUND_KEY] ?: DEFAULT_ENABLE_STRING_SELECT_SOUND,
                enableInTuneSound = prefs[ENABLE_IN_TUNE_SOUND_KEY] ?: DEFAULT_IN_TUNE_SOUND,
                displayType = prefs[DISPLAY_TYPE_KEY]?.let { TuningDisplayType.valueOf(it) } ?: DEFAULT_DISPLAY_TYPE,
                stringLayout = prefs[STRING_LAYOUT_KEY]?.let { StringLayout.valueOf(it) } ?: DEFAULT_STRING_LAYOUT,
                useBlackTheme = prefs[USE_BLACK_THEME_KEY] ?: DEFAULT_USE_BLACK_THEME,
                useDynamicColor = prefs[USE_DYNAMIC_COLOR_KEY] ?: DEFAULT_USE_DYNAMIC_COLOR,
                editModeDefault = prefs[EDIT_MODE_DEFAULT_KEY] ?: DEFAULT_EDIT_MODE_DEFAULT,
                initialTuning = prefs[INITIAL_TUNING_KEY]?.let { InitialTuningType.valueOf(it) } ?: DEFAULT_INITIAL_TUNING
                initialTuning = prefs[INITIAL_TUNING_KEY]?.let { InitialTuningType.valueOf(it) } ?: DEFAULT_INITIAL_TUNING,
                reviewPromptLaunches = prefs[REVIEW_PROMPT_LAUNCHES_KEY]?.toIntOrNull() ?: 0
            )
        }
    }
}

/** Enum representing the available options for displaying tuning offset. */
@Immutable
enum class TuningDisplayType(val multiplier: Int) {
    /** Displays a simple offset value and whether to tune up or down. */
    SIMPLE(10),
    /** Displays tuning offset in semitones. */
    SEMITONES(1),
    /** Displays tuning offset in cents. */
    CENTS(100)
}

/** Enum representing the available layouts to display string controls. */
@Immutable
enum class StringLayout {
    /** Displays string controls in-line (for electric guitars). */
    INLINE,
    /** Displays string controls side by side (for acoustic guitars). */
    SIDE_BY_SIDE
}

/** Enum representing the available options for the default tuning used when opening the app. */
@Immutable
enum class InitialTuningType {
    /** Default to the pinned tuning. */
    PINNED,

    /** Default to the last used tuning. */
    LAST_USED,
}

/** Provides the data store for tuner preferences. */
val Context.tunerPreferenceDataStore by preferencesDataStore(name = "tuner_preferences")