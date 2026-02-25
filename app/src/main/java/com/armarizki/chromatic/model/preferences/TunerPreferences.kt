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

package com.armarizki.chromatic.model.preferences

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.doublePreferencesKey


@Immutable
data class TunerPreferences(
    val a4Pitch: Double = DEFAULT_A4_PITCH,
    val enableStringSelectSound: Boolean = DEFAULT_ENABLE_STRING_SELECT_SOUND,
    val enableInTuneSound: Boolean = DEFAULT_IN_TUNE_SOUND,
    val displayType: TuningDisplayType = DEFAULT_DISPLAY_TYPE,
    val stringLayout: StringLayout = DEFAULT_STRING_LAYOUT,
    val useBlackTheme: Boolean = DEFAULT_USE_BLACK_THEME,
    val useDynamicColor: Boolean = DEFAULT_USE_DYNAMIC_COLOR,
    val editModeDefault: Boolean = DEFAULT_EDIT_MODE_DEFAULT,
    val initialTuning: InitialTuningType = DEFAULT_INITIAL_TUNING,
    val showReviewPrompt: Boolean = DEFAULT_SHOW_REVIEW_PROMPT,
    val reviewPromptLaunches: Int = 0
) {
    companion object {
        val A4_PITCH_KEY = doublePreferencesKey("a4_pitch")
        val ENABLE_STRING_SELECT_SOUND_KEY = booleanPreferencesKey("enable_string_select_sound")
        val ENABLE_IN_TUNE_SOUND_KEY = booleanPreferencesKey("enable_in_tune_sound")
        val DISPLAY_TYPE_KEY = stringPreferencesKey("display_type")
        val STRING_LAYOUT_KEY = stringPreferencesKey("string_layout")
        val USE_BLACK_THEME_KEY = booleanPreferencesKey("use_black_theme")
        val USE_DYNAMIC_COLOR_KEY = booleanPreferencesKey("use_dynamic_color")
        val EDIT_MODE_DEFAULT_KEY = booleanPreferencesKey("edit_mode_default")
        val INITIAL_TUNING_KEY = stringPreferencesKey("initial_tuning")
        val SHOW_REVIEW_PROMPT_KEY = booleanPreferencesKey("show_review_prompt")
        val REVIEW_PROMPT_LAUNCHES_KEY = stringPreferencesKey("review_prompt_launches")

        // Defaults
        const val DEFAULT_A4_PITCH = 440.0
        const val DEFAULT_ENABLE_STRING_SELECT_SOUND = true
        const val DEFAULT_IN_TUNE_SOUND = true
        val DEFAULT_DISPLAY_TYPE = TuningDisplayType.SIMPLE
        val DEFAULT_STRING_LAYOUT = StringLayout.INLINE
        const val DEFAULT_USE_BLACK_THEME = false
        const val DEFAULT_EDIT_MODE_DEFAULT = false
        val DEFAULT_INITIAL_TUNING = InitialTuningType.PINNED
        const val DEFAULT_USE_DYNAMIC_COLOR = false
        const val DEFAULT_SHOW_REVIEW_PROMPT = true

         
        const val REVIEW_PROMPT_ATTEMPTS = 3

         
        fun fromAndroidPreferences(prefs: Preferences): TunerPreferences {
            return TunerPreferences(
                a4Pitch = prefs[A4_PITCH_KEY] ?: DEFAULT_A4_PITCH,
                enableStringSelectSound = prefs[ENABLE_STRING_SELECT_SOUND_KEY] ?: DEFAULT_ENABLE_STRING_SELECT_SOUND,
                enableInTuneSound = prefs[ENABLE_IN_TUNE_SOUND_KEY] ?: DEFAULT_IN_TUNE_SOUND,
                displayType = prefs[DISPLAY_TYPE_KEY]?.let { TuningDisplayType.valueOf(it) } ?: DEFAULT_DISPLAY_TYPE,
                stringLayout = prefs[STRING_LAYOUT_KEY]?.let { StringLayout.valueOf(it) } ?: DEFAULT_STRING_LAYOUT,
                useBlackTheme = prefs[USE_BLACK_THEME_KEY] ?: DEFAULT_USE_BLACK_THEME,
                useDynamicColor = prefs[USE_DYNAMIC_COLOR_KEY] ?: DEFAULT_USE_DYNAMIC_COLOR,
                editModeDefault = prefs[EDIT_MODE_DEFAULT_KEY] ?: DEFAULT_EDIT_MODE_DEFAULT,
                initialTuning = prefs[INITIAL_TUNING_KEY]?.let { InitialTuningType.valueOf(it) } ?: DEFAULT_INITIAL_TUNING,
                showReviewPrompt = prefs[SHOW_REVIEW_PROMPT_KEY] ?: DEFAULT_SHOW_REVIEW_PROMPT,
                reviewPromptLaunches = prefs[REVIEW_PROMPT_LAUNCHES_KEY]?.toIntOrNull() ?: 0
            )
        }
    }
}

 
@Immutable
enum class TuningDisplayType(val multiplier: Int) {
     
    SIMPLE(10),
     
    SEMITONES(1),
     
    CENTS(100)
}

 
@Immutable
enum class StringLayout {
     
    INLINE,
     
    SIDE_BY_SIDE
}

 
@Immutable
enum class InitialTuningType {
     
    PINNED,

     
    LAST_USED,
}

 
val Context.tunerPreferenceDataStore by preferencesDataStore(name = "tuner_preferences")