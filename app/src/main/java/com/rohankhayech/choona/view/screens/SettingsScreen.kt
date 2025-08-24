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

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.rohankhayech.android.util.ui.preview.ThemePreview
import com.rohankhayech.android.util.ui.theme.m3.isLight
import com.rohankhayech.android.util.ui.theme.m3.isTrueDark
import com.rohankhayech.choona.R
import com.rohankhayech.choona.model.preferences.InitialTuningType
import com.rohankhayech.choona.model.preferences.StringLayout
import com.rohankhayech.choona.model.preferences.TunerPreferences
import com.rohankhayech.choona.model.preferences.TuningDisplayType
import com.rohankhayech.choona.view.components.SectionLabel
import com.rohankhayech.choona.view.theme.AppTheme
import com.rohankhayech.music.Tuning

/**
 * A UI screen that displays and allows selection of the user's tuner preferences.
 *
 * @param prefs The tuner preferences.
 * @param pinnedTuning The full name of the currently pinned tuning.
 * @param onSelectStringLayout Called when the user selects a string control layout.
 * @param onSelectDisplayType Called when the user selects a tuning display type.
 * @param onEnableStringSelectSound Called when the user toggles the string select sound.
 * @param onEnableInTuneSound Called when the user toggles the in-tune sound.
 * @param onSetUseBlackTheme Called when the user toggles the full black theme.
 * @param onSetUseDynamicColor Called when the user toggles the dynamic color feature.
 * @param onSelectInitialTuning Called when the user selects the initial tuning type.
 * @param onToggleEditModeDefault Called when the user toggles the edit mode feature.
 * @param onBackPressed Called when the user presses the back navigation button.
 *
 * @author Rohan Khayech
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    prefs: TunerPreferences,
    pinnedTuning: String,
    onSelectStringLayout: (StringLayout) -> Unit,
    onSelectDisplayType: (TuningDisplayType) -> Unit,
    onEnableStringSelectSound: (Boolean) -> Unit,
    onEnableInTuneSound: (Boolean) -> Unit,
    onSetUseBlackTheme: (Boolean) -> Unit,
    onSetUseDynamicColor: (Boolean) -> Unit,
    onToggleEditModeDefault: (Boolean) -> Unit,
    onSelectInitialTuning: (InitialTuningType) -> Unit,
    onAboutPressed: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { TopAppBar(
            // Back navigation button.
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.nav_back))
                }
            },
            colors = if (!MaterialTheme.isLight && MaterialTheme.isTrueDark) {
                TopAppBarDefaults.topAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.background)
            } else {
                TopAppBarDefaults.topAppBarColors()
            },
            title = { Text(stringResource(R.string.tuner_settings)) },
            scrollBehavior = scrollBehavior
        )}
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            // String layout selection.
            SectionLabel(title = stringResource(R.string.pref_string_layout))

            // Inline
            ListItem(
                headlineContent = { Text(stringResource(R.string.pref_string_layout_inline)) },
                supportingContent = { Text(stringResource(R.string.pref_string_layout_inline_desc)) },
                trailingContent = {
                    RadioButton(
                        selected = prefs.stringLayout == StringLayout.INLINE,
                        onClick = { onSelectStringLayout(StringLayout.INLINE) }
                    )
                },
                modifier = Modifier.clickable { onSelectStringLayout(StringLayout.INLINE) }
            )

            // Side by Side
            ListItem(
                headlineContent =  { Text(stringResource(R.string.pref_string_layout_side_by_side)) },
                supportingContent =  { Text(stringResource(R.string.pref_string_layout_side_by_side_desc)) },
                trailingContent = {
                    RadioButton(
                        selected = prefs.stringLayout == StringLayout.SIDE_BY_SIDE,
                        onClick = { onSelectStringLayout(StringLayout.SIDE_BY_SIDE) }
                    )
                },
                modifier = Modifier.clickable { onSelectStringLayout(StringLayout.SIDE_BY_SIDE) }
            )
            HorizontalDivider()

            // Display type selection.
            SectionLabel(title = stringResource(R.string.pref_display_type))

            // Simple
            ListItem(
                headlineContent =  { Text(stringResource(R.string.pref_display_type_simple)) },
                trailingContent = {
                    RadioButton(
                        selected = prefs.displayType == TuningDisplayType.SIMPLE,
                        onClick = { onSelectDisplayType(TuningDisplayType.SIMPLE) }
                    )
                },
                modifier = Modifier.clickable { onSelectDisplayType(TuningDisplayType.SIMPLE) }
            )

            // Semitones
            ListItem(
                headlineContent =  { Text(stringResource(R.string.pref_display_type_semitones)) },
                supportingContent =  { Text(stringResource(R.string.pref_display_type_semitones_desc)) },
                trailingContent = {
                    RadioButton(
                        selected = prefs.displayType == TuningDisplayType.SEMITONES,
                        onClick = { onSelectDisplayType(TuningDisplayType.SEMITONES) }
                    )
                },
                modifier = Modifier.clickable { onSelectDisplayType(TuningDisplayType.SEMITONES) }
            )

            // Cents
            ListItem(
                headlineContent =  { Text(stringResource(R.string.pref_display_type_cents)) },
                supportingContent =  { Text(stringResource(R.string.pref_display_type_cents_desc)) },
                trailingContent = {
                    RadioButton(
                        selected = prefs.displayType == TuningDisplayType.CENTS,
                        onClick = { onSelectDisplayType(TuningDisplayType.CENTS) }
                    )
                },
                modifier = Modifier.clickable { onSelectDisplayType(TuningDisplayType.CENTS) }
            )
            HorizontalDivider()

            // Sound preferences
            SectionLabel(title = stringResource(R.string.prefs_sound))

            // String selection sound
            ListItem(
                headlineContent =  { Text(stringResource(R.string.pref_enable_string_select_sound)) },
                supportingContent =  { Text(stringResource(R.string.pref_enable_string_select_sound_desc))},
                trailingContent = {
                    Switch(
                        checked = prefs.enableStringSelectSound,
                        onCheckedChange = onEnableStringSelectSound
                    )
                },
                modifier = Modifier.clickable { onEnableStringSelectSound(!prefs.enableStringSelectSound) }
            )

            // In tune sound
            ListItem(
                headlineContent =  { Text(stringResource(R.string.pref_enable_in_tune_sound)) },
                supportingContent =  { Text(stringResource(R.string.pref_enable_in_tune_sound_desc))},
                trailingContent = {
                    Switch(
                        checked = prefs.enableInTuneSound,
                        onCheckedChange = onEnableInTuneSound
                    )
                },
                modifier = Modifier.clickable { onEnableInTuneSound(!prefs.enableInTuneSound) }
            )

            HorizontalDivider()

            // Default tuning.
            SectionLabel(title = stringResource(R.string.pref_initial_tuning))

            // Pinned
            ListItem(
                headlineContent = { Text(stringResource(R.string.pref_initial_tuning_pinned)) },
                supportingContent = { Text(if (pinnedTuning == Tuning.STANDARD.fullName) stringResource(R.string.pref_initial_tuning_pinned_desc_standard) else stringResource(R.string.pref_initial_tuning_pinned_desc, pinnedTuning)) },
                trailingContent = {
                    RadioButton(
                        selected = prefs.initialTuning == InitialTuningType.PINNED,
                        onClick = { onSelectInitialTuning(InitialTuningType.PINNED) }
                    )
                },
                modifier = Modifier.clickable { onSelectInitialTuning(InitialTuningType.PINNED) }
            )

            // Last Used
            ListItem(
                headlineContent = { Text(stringResource(R.string.pref_initial_tuning_last)) },
                supportingContent = { Text(stringResource(R.string.pref_initial_tuning_used_desc)) },
                trailingContent = {
                    RadioButton(
                        selected = prefs.initialTuning == InitialTuningType.LAST_USED,
                        onClick = { onSelectInitialTuning(InitialTuningType.LAST_USED) }
                    )
                },
                modifier = Modifier.clickable { onSelectInitialTuning(InitialTuningType.LAST_USED) }
            )
            HorizontalDivider()

            // Display preferences
            SectionLabel(title = stringResource(R.string.prefs_display))

            // Dynamic color theme
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.pref_use_dynamic_color)) },
                    supportingContent = { Text(stringResource(R.string.pref_use_dynamic_color_desc)) },
                    trailingContent = {
                        Switch(
                            checked = prefs.useDynamicColor,
                            onCheckedChange = onSetUseDynamicColor
                        )
                    },
                    modifier = Modifier.clickable { onSetUseDynamicColor(!prefs.useDynamicColor) }
                )
            }


            // Full black theme
            ListItem(
                headlineContent =  { Text(stringResource(R.string.pref_use_black_theme)) },
                supportingContent =  { Text(stringResource(R.string.pref_use_black_theme_desc))},
                trailingContent = {
                    Switch(
                        checked = prefs.useBlackTheme,
                        onCheckedChange = onSetUseBlackTheme
                    )
                },
                modifier = Modifier.clickable { onSetUseBlackTheme(!prefs.useBlackTheme) }
            )

            ListItem(
                headlineContent =  { Text(stringResource(R.string.pref_edit_mode_default)) },
                supportingContent =  { Text(stringResource(R.string.pref_edit_mode_default_desc)) },
                trailingContent = {
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
                headlineContent =  { Text("${stringResource(R.string.about)} ${stringResource(R.string.app_name)}") },
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
            pinnedTuning = Tuning.STANDARD.fullName,
            onSelectDisplayType = {},
            onSelectStringLayout = {},
            onEnableStringSelectSound = {},
            onEnableInTuneSound = {},
            onSetUseBlackTheme = {},
            onSetUseDynamicColor = {},
            onToggleEditModeDefault = {},
            onSelectInitialTuning = {},
            onAboutPressed = {},
            onBackPressed = {}
        )
    }
}