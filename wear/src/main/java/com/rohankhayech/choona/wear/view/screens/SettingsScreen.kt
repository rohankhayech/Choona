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

package com.rohankhayech.choona.wear.view.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.RadioButton
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SwitchButton
import androidx.wear.compose.material3.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.rohankhayech.choona.lib.R
import com.rohankhayech.choona.lib.model.preferences.InitialTuningType
import com.rohankhayech.choona.lib.model.preferences.StringLayout
import com.rohankhayech.choona.lib.model.preferences.TunerPreferences
import com.rohankhayech.choona.lib.model.preferences.TuningDisplayType
import com.rohankhayech.choona.wear.view.components.SectionLabel
import com.rohankhayech.choona.wear.view.theme.AppTheme
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
 * @param onToggleEditModeDefault Called when the user toggles the edit mode feature.
 * @param onSelectInitialTuning Called when the user selects the initial tuning type.
 * @param onAboutPressed Called when the user presses the about option.
 * @param onBackPressed Called when the user presses the back navigation button.
 *
 * @author Rohan Khayech
 */
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
    val listState = rememberScalingLazyListState()

    ScreenScaffold(
        scrollState = listState,
        edgeButton = {
            EdgeButton(
                buttonSize = EdgeButtonSize.Large,
                onClick = onAboutPressed
            ) {
                Text("${stringResource(R.string.about)} ${stringResource(R.string.app_name)}")
            }
        }
    ) { padding ->
        ScalingLazyColumn(
            state = listState,
            contentPadding = padding
        ) {
            item {
                ListHeader {
                    Text(stringResource(R.string.tuner_settings))
                }
            }

            // Display type selection.
            item { SectionLabel(stringResource(R.string.pref_display_type)) }

            // Simple
            item {
                val selected = prefs.displayType == TuningDisplayType.SIMPLE
                RadioButton (
                    modifier = Modifier.fillMaxWidth(),
                    selected = selected,
                    onSelect = { onSelectDisplayType(TuningDisplayType.SIMPLE) },
                    label = { Text(stringResource(R.string.pref_display_type_simple)) },
                )
            }

            // Semitones
            item {
                val selected = prefs.displayType == TuningDisplayType.SEMITONES
                RadioButton (
                    selected = selected,
                    onSelect = { onSelectDisplayType(TuningDisplayType.SEMITONES) },
                    label = { Text(stringResource(R.string.pref_display_type_semitones)) },
                    secondaryLabel = { Text(stringResource(R.string.pref_display_type_semitones_desc)) },
                )
            }

            // Cents
            item {
                val selected = prefs.displayType == TuningDisplayType.CENTS
                RadioButton (
                    selected = selected,
                    onSelect = { onSelectDisplayType(TuningDisplayType.CENTS) },
                    label = { Text(stringResource(R.string.pref_display_type_cents)) },
                    secondaryLabel = { Text(stringResource(R.string.pref_display_type_cents_desc)) },
                )
            }

            // Sound preferences
            item { SectionLabel(stringResource(R.string.prefs_sound)) }

            // String selection sound
            item {
                SwitchButton(
                    checked = prefs.enableStringSelectSound,
                    onCheckedChange = onEnableStringSelectSound,
                    label = { Text(stringResource(R.string.pref_enable_string_select_sound)) }
                )
            }
            item { Text(stringResource(R.string.pref_enable_string_select_sound_desc),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyMedium) }

            // In tune sound
            item {
                SwitchButton(
                    checked = prefs.enableInTuneSound,
                    onCheckedChange = onEnableInTuneSound,
                    label = { Text(stringResource(R.string.pref_enable_in_tune_sound)) }
                )
            }
            item { Text(stringResource(R.string.pref_enable_in_tune_sound_desc),
                                       modifier = Modifier.padding(horizontal = 16.dp),
                                       style = MaterialTheme.typography.bodyMedium) }

            // Default tuning.
            item { SectionLabel(stringResource(R.string.pref_initial_tuning)) }

            item { Text("Set the tuning used on app launch:",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyMedium) }

            // Last Used
            item {
                val selected = prefs.initialTuning == InitialTuningType.LAST_USED
                RadioButton (
                    modifier = Modifier.fillMaxWidth(),
                    selected = selected,
                    onSelect = { onSelectInitialTuning(InitialTuningType.LAST_USED) },
                    label = { Text(stringResource(R.string.pref_initial_tuning_last)) }
                )
            }

            // Pinned
            item {
                val selected = prefs.initialTuning == InitialTuningType.PINNED
                RadioButton (
                    modifier = Modifier.fillMaxWidth(),
                    selected = selected,
                    onSelect = { onSelectInitialTuning(InitialTuningType.PINNED) },
                    label = { Text(stringResource(R.string.pref_initial_tuning_pinned)) },
                )
            }
            item {
                Text(
                    if (pinnedTuning == Tuning.STANDARD.fullName) stringResource(R.string.pref_initial_tuning_pinned_desc_standard) else stringResource(R.string.pref_initial_tuning_pinned_desc, pinnedTuning),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// Preview
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Preview(device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Preview(device = WearDevices.SQUARE, showSystemUi = true)
@Composable
private fun Preview() {
    AppTheme {
        AppScaffold {
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
}
