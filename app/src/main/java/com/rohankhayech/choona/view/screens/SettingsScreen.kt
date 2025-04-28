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

package com.rohankhayech.choona.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rohankhayech.android.util.ui.preview.ThemePreview
import com.rohankhayech.android.util.ui.theme.primarySurfaceBackground
import com.rohankhayech.choona.R
import com.rohankhayech.choona.model.preferences.StringLayout
import com.rohankhayech.choona.model.preferences.TunerPreferences
import com.rohankhayech.choona.model.preferences.TuningDisplayType
import com.rohankhayech.choona.view.components.SectionLabel
import com.rohankhayech.choona.view.theme.AppTheme

/**
 * A UI screen that displays and allows selection of the user's tuner preferences.
 *
 * @param prefs The tuner preferences.
 * @param onSelectStringLayout Called when the user selects a string control layout.
 * @param onSelectDisplayType Called when the user selects a tuning display type.
 * @param onEnableStringSelectSound Called when the user toggles the string select sound.
 * @param onEnableInTuneSound Called when the user toggles the in-tune sound.
 * @param onSetUseBlackTheme Called when the user toggles the full black theme.
 * @param onToggleEditModeDefault Called when the user toggles the edit mode feature.
 * @param onBackPressed Called when the user presses the back navigation button.
 *
 * @author Rohan Khayech
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(
    prefs: TunerPreferences,
    onSelectStringLayout: (StringLayout) -> Unit,
    onSelectDisplayType: (TuningDisplayType) -> Unit,
    onEnableStringSelectSound: (Boolean) -> Unit,
    onEnableInTuneSound: (Boolean) -> Unit,
    onSetUseBlackTheme: (Boolean) -> Unit,
    onToggleEditModeDefault: (Boolean) -> Unit,
    onAboutPressed: () -> Unit,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(
            // Back navigation button.
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.Default.ArrowBack, stringResource(R.string.nav_back))
                }
            },
            backgroundColor = MaterialTheme.colors.primarySurfaceBackground(prefs.useBlackTheme),
            title = { Text(stringResource(R.string.tuner_settings)) }
        )}
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(top = padding.calculateTopPadding())
        ) {
            // String layout selection.
            SectionLabel(title = stringResource(R.string.pref_string_layout))

            // Inline
            ListItem(
                text = { Text(stringResource(R.string.pref_string_layout_inline)) },
                secondaryText = { Text(stringResource(R.string.pref_string_layout_inline_desc)) },
                trailing = {
                    RadioButton(
                        selected = prefs.stringLayout == StringLayout.INLINE,
                        onClick = { onSelectStringLayout(StringLayout.INLINE) }
                    )
                },
                modifier = Modifier.clickable { onSelectStringLayout(StringLayout.INLINE) }
            )

            // Side by Side
            ListItem(
                text = { Text(stringResource(R.string.pref_string_layout_side_by_side)) },
                secondaryText = { Text(stringResource(R.string.pref_string_layout_side_by_side_desc)) },
                trailing = {
                    RadioButton(
                        selected = prefs.stringLayout == StringLayout.SIDE_BY_SIDE,
                        onClick = { onSelectStringLayout(StringLayout.SIDE_BY_SIDE) }
                    )
                },
                modifier = Modifier.clickable { onSelectStringLayout(StringLayout.SIDE_BY_SIDE) }
            )
            Divider()

            // Display type selection.
            SectionLabel(title = stringResource(R.string.pref_display_type))

            // Simple
            ListItem(
                text = { Text(stringResource(R.string.pref_display_type_simple)) },
                trailing = {
                    RadioButton(
                        selected = prefs.displayType == TuningDisplayType.SIMPLE,
                        onClick = { onSelectDisplayType(TuningDisplayType.SIMPLE) }
                    )
                },
                modifier = Modifier.clickable { onSelectDisplayType(TuningDisplayType.SIMPLE) }
            )

            // Semitones
            ListItem(
                text = { Text(stringResource(R.string.pref_display_type_semitones)) },
                secondaryText = { Text(stringResource(R.string.pref_display_type_semitones_desc)) },
                trailing = {
                    RadioButton(
                        selected = prefs.displayType == TuningDisplayType.SEMITONES,
                        onClick = { onSelectDisplayType(TuningDisplayType.SEMITONES) }
                    )
                },
                modifier = Modifier.clickable { onSelectDisplayType(TuningDisplayType.SEMITONES) }
            )

            // Cents
            ListItem(
                text = { Text(stringResource(R.string.pref_display_type_cents)) },
                secondaryText = { Text(stringResource(R.string.pref_display_type_cents_desc)) },
                trailing = {
                    RadioButton(
                        selected = prefs.displayType == TuningDisplayType.CENTS,
                        onClick = { onSelectDisplayType(TuningDisplayType.CENTS) }
                    )
                },
                modifier = Modifier.clickable { onSelectDisplayType(TuningDisplayType.CENTS) }
            )
            Divider()

            // Sound preferences
            SectionLabel(title = stringResource(R.string.prefs_sound))

            // String selection sound
            ListItem(
                text = { Text(stringResource(R.string.pref_enable_string_select_sound)) },
                secondaryText = { Text(stringResource(R.string.pref_enable_string_select_sound_desc))},
                trailing = {
                    Switch(
                        checked = prefs.enableStringSelectSound,
                        onCheckedChange = onEnableStringSelectSound
                    )
                },
                modifier = Modifier.clickable { onEnableStringSelectSound(!prefs.enableStringSelectSound) }
            )

            // In tune sound
            ListItem(
                text = { Text(stringResource(R.string.pref_enable_in_tune_sound)) },
                secondaryText = { Text(stringResource(R.string.pref_enable_in_tune_sound_desc))},
                trailing = {
                    Switch(
                        checked = prefs.enableInTuneSound,
                        onCheckedChange = onEnableInTuneSound
                    )
                },
                modifier = Modifier.clickable { onEnableInTuneSound(!prefs.enableInTuneSound) }
            )

            Divider()

            // Display preferences
            SectionLabel(title = stringResource(R.string.prefs_display))

            // Full black theme
            ListItem(
                text = { Text(stringResource(R.string.pref_use_black_theme)) },
                secondaryText = { Text(stringResource(R.string.pref_use_black_theme_desc))},
                trailing = {
                    Switch(
                        checked = prefs.useBlackTheme,
                        onCheckedChange = onSetUseBlackTheme
                    )
                },
                modifier = Modifier.clickable { onSetUseBlackTheme(!prefs.useBlackTheme) }
            )

            ListItem(
                text = { Text(stringResource(R.string.pref_edit_mode_default)) },
                secondaryText = { Text(stringResource(R.string.pref_edit_mode_default_desc)) },
                trailing = {
                    Switch(
                        checked = prefs.editModeDefault,
                        onCheckedChange = onToggleEditModeDefault
                    )
                },
                modifier = Modifier.clickable { onToggleEditModeDefault(!prefs.editModeDefault) }
            )

            // About
            SectionLabel(stringResource(R.string.about))
            ListItem(
                text = { Text("${stringResource(R.string.about)} ${stringResource(R.string.app_name)}") },
                modifier = Modifier.clickable(onClick = onAboutPressed)
            )
        }
    }
}

// Preview
@ThemePreview
@Composable
private fun Preview() {
    AppTheme {
        SettingsScreen(
            prefs = TunerPreferences(),
            onSelectDisplayType = {},
            onSelectStringLayout = {},
            onEnableStringSelectSound = {},
            onEnableInTuneSound = {},
            onSetUseBlackTheme = {},
            onToggleEditModeDefault = {},
            onAboutPressed = {},
            onBackPressed = {},
        )
    }
}