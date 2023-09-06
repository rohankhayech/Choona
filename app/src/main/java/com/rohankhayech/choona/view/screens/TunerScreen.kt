/*
 * Choona - Guitar Tuner
 * Copyright (C) 2023 Rohan Khayech
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

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.rohankhayech.android.util.ui.preview.CompactThemePreview
import com.rohankhayech.android.util.ui.preview.LandscapePreview
import com.rohankhayech.android.util.ui.preview.LargeFontPreview
import com.rohankhayech.android.util.ui.preview.ThemePreview
import com.rohankhayech.android.util.ui.theme.primarySurfaceBackground
import com.rohankhayech.choona.R
import com.rohankhayech.choona.model.preferences.StringLayout
import com.rohankhayech.choona.model.preferences.TunerPreferences
import com.rohankhayech.choona.model.tuning.Tunings
import com.rohankhayech.choona.view.components.CompactStringSelector
import com.rohankhayech.choona.view.components.StringControls
import com.rohankhayech.choona.view.components.TuningDisplay
import com.rohankhayech.choona.view.components.TuningItem
import com.rohankhayech.choona.view.components.TuningSelector
import com.rohankhayech.choona.view.theme.AppTheme
import com.rohankhayech.music.Tuning

/**
 * A UI screen that allows selection of a tuning and string and displays the current tuning status.
 *
 * @param compact Whether to use compact layout.
 * @param expanded Whether the current window is expanded width.
 * @param windowSizeClass Size class of the activity window.
 * @param tuning Guitar tuning used for comparison.
 * @param noteOffset The offset between the currently playing note and the selected string.
 * @param selectedString Index of the currently selected string within the tuning.
 * @param tuned Whether each string has been tuned.
 * @param autoDetect Whether the tuner will automatically detect the currently playing string.
 * @param favTunings Set of tunings marked as favourite by the user.
 * @param customTunings Set of custom tunings added by the user.
 * @param prefs User preferences for the tuner.
 * @param onSelectString Called when a string is selected.
 * @param onSelectTuning Called when a tuning is selected.
 * @param onTuneUpString Called when a string is tuned up.
 * @param onTuneDownString Called when a string is tuned down.
 * @param onTuneUpTuning Called when the tuning is tuned up.
 * @param onTuneDownTuning Called when the tuning is tuned down.
 * @param onAutoChanged Called when the auto detect switch is toggled.
 * @param onTuned Called when the detected note is held in tune.
 * @param onOpenTuningSelector Called when the user opens the tuning selector screen.
 * @param onSettingsPressed Called when the settings button is pressed.
 * @param onConfigurePressed Called when the configure tuning button is pressed.
 *
 * @author Rohan Khayech
 */
@Composable
fun TunerScreen(
    compact: Boolean = false,
    expanded: Boolean = false,
    windowSizeClass: WindowSizeClass,
    tuning: Tuning,
    noteOffset: State<Double?>,
    selectedString: Int,
    tuned: BooleanArray,
    autoDetect: Boolean,
    favTunings: State<Set<Tuning>>,
    customTunings: State<Set<Tuning>>,
    prefs: TunerPreferences,
    onSelectString: (Int) -> Unit,
    onSelectTuning: (Tuning) -> Unit,
    onTuneUpString: (Int) -> Unit,
    onTuneDownString: (Int) -> Unit,
    onTuneUpTuning: () -> Unit,
    onTuneDownTuning: () -> Unit,
    onAutoChanged: (Boolean) -> Unit,
    onTuned: () -> Unit,
    onOpenTuningSelector: () -> Unit,
    onSettingsPressed: () -> Unit,
    onConfigurePressed: () -> Unit
) {
    Scaffold (
        topBar = {
            if (!compact) {
                AppBar(prefs.useBlackTheme, onSettingsPressed)
            } else {
                CompactAppBar(
                    fullBlack = prefs.useBlackTheme,
                    onSettingsPressed = onSettingsPressed,
                    tuning = tuning,
                    customTunings = customTunings,
                    onConfigurePressed = onConfigurePressed
                )
            }
        }
    ) { padding ->
        TunerBodyScaffold(
            padding,
            compact,
            expanded,
            windowSizeClass,
            tuning,
            noteOffset,
            selectedString,
            tuned,
            autoDetect,
            favTunings,
            customTunings,
            prefs,
            onSelectString,
            onSelectTuning,
            onTuneUpString,
            onTuneDownString,
            onTuneUpTuning,
            onTuneDownTuning,
            onAutoChanged,
            onTuned,
            onOpenTuningSelector,

            // Portrait layout
            portrait = { padd, tuningDisplay, stringControls, autoDetectSwitch, tuningSelector ->
                Column(
                    modifier = Modifier
                        .padding(padd)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    tuningDisplay()
                    stringControls(inline = prefs.stringLayout == StringLayout.INLINE)
                    autoDetectSwitch(Modifier)
                    tuningSelector(Modifier.padding(vertical = 8.dp))
                }
            },

            // Landscape layout
            landscape = { padd, tuningDisplay, stringControls, autoDetectSwitch, tuningSelector ->
                ConstraintLayout(
                    modifier = Modifier
                        .padding(padd)
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    val (display, tuningSelectorBox, stringsSelector, autoSwitch) = createRefs()

                    Box(Modifier.constrainAs(display) {
                        top.linkTo(parent.top)
                        bottom.linkTo(autoSwitch.top)
                        start.linkTo(parent.start)
                        end.linkTo(stringsSelector.start)
                    }) {
                        tuningDisplay()
                    }

                    Box(Modifier.constrainAs(tuningSelectorBox) {
                        top.linkTo(stringsSelector.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }) {
                        tuningSelector(Modifier)
                    }

                    Box(Modifier.constrainAs(stringsSelector) {
                        top.linkTo(parent.top)
                        bottom.linkTo(tuningSelectorBox.top)
                        start.linkTo(display.end)
                        end.linkTo(parent.end)
                    }) {
                        stringControls(
                            inline = windowSizeClass.heightSizeClass > WindowHeightSizeClass.Compact
                                && prefs.stringLayout == StringLayout.INLINE,
                        )
                    }

                    Box(Modifier.constrainAs(autoSwitch) {
                        top.linkTo(display.bottom)
                        bottom.linkTo(tuningSelectorBox.top)
                        start.linkTo(tuningSelectorBox.start)
                        end.linkTo(stringsSelector.start)
                    }) {
                        autoDetectSwitch(Modifier)
                    }
                }
            },

            // Compact layout
            compactLayout = { padd, tuningDisplay, _, autoDetectSwitch, _ ->
                Column(
                    modifier = Modifier
                        .padding(padd)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center,
                    ) {
                        tuningDisplay()
                    }
                    Row(
                        Modifier
                            .height(IntrinsicSize.Min)
                            .padding(bottom = 8.dp)) {
                        CompactStringSelector(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp),
                            tuning = tuning,
                            selectedString = selectedString,
                            tuned = tuned,
                            onSelect = onSelectString,
                        )
                        Divider(
                            Modifier
                                .width((1f / LocalDensity.current.density).dp)
                                .fillMaxHeight())
                        Box(Modifier.padding(horizontal = 16.dp)) {
                            autoDetectSwitch(Modifier.fillMaxHeight())
                        }
                    }
                }
            }
        )
    }
}

/**
 * Type of layout for the tuner screen body.
 * Should place all appropriate components provided,
 * and must use the provided padding for the root composable.
 */
private typealias TunerBodyLayout = @Composable (
    padding: PaddingValues,
    tuningDisplay: @Composable () -> Unit,
    stringControls: @Composable (inline: Boolean) -> Unit,
    autoDetectSwitch: @Composable (modifier: Modifier) -> Unit,
    tuningSelector: @Composable (modifier: Modifier) -> Unit
) -> Unit

/**
 * Scaffold containing the main UI components of the tuner screen body.
 * The scaffold places these components in the appropriate layout.
 *
 * [Portrait][portrait], [landscape] and [compact][compactLayout] layouts
 * must be defined to determine the placement of the UI components.
 *
 * @param compact Whether to use compact layout.
 * @param expanded Whether the current window is an expanded width.
 * @param windowSizeClass Size class of the activity window.
 * @param tuning Guitar tuning used for comparison.
 * @param noteOffset The offset between the currently playing note and the selected string.
 * @param selectedString Index of the currently selected string within the tuning.
 * @param tuned Whether each string has been tuned.
 * @param autoDetect Whether the tuner will automatically detect the currently playing string.
 * @param favTunings Set of tunings marked as favourite by the user.
 * @param customTunings Set of custom tunings added by the user.
 * @param prefs User preferences for the tuner.
 * @param onSelectString Called when a string is selected.
 * @param onSelectTuning Called when a tuning is selected.
 * @param onTuneUpString Called when a string is tuned up.
 * @param onTuneDownString Called when a string is tuned down.
 * @param onTuneUpTuning Called when the tuning is tuned up.
 * @param onTuneDownTuning Called when the tuning is tuned down.
 * @param onAutoChanged Called when the auto detect switch is toggled.
 * @param onTuned Called when the detected note is held in tune.
 * @param onOpenTuningSelector Called when the user opens the tuning selector screen.
 */
@Composable
private fun TunerBodyScaffold(
    padding: PaddingValues,
    compact: Boolean = false,
    expanded: Boolean,
    windowSizeClass: WindowSizeClass,
    tuning: Tuning,
    noteOffset: State<Double?>,
    selectedString: Int,
    tuned: BooleanArray,
    autoDetect: Boolean,
    favTunings: State<Set<Tuning>>,
    customTunings: State<Set<Tuning>>,
    prefs: TunerPreferences,
    onSelectString: (Int) -> Unit,
    onSelectTuning: (Tuning) -> Unit,
    onTuneUpString: (Int) -> Unit,
    onTuneDownString: (Int) -> Unit,
    onTuneUpTuning: () -> Unit,
    onTuneDownTuning: () -> Unit,
    onAutoChanged: (Boolean) -> Unit,
    onTuned: () -> Unit,
    onOpenTuningSelector: () -> Unit,
    portrait: TunerBodyLayout,
    landscape: TunerBodyLayout,
    compactLayout: TunerBodyLayout
) {
    val layout = if (!compact) {
        if ((windowSizeClass.heightSizeClass >= WindowHeightSizeClass.Medium && windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact)
            || (windowSizeClass.heightSizeClass == WindowHeightSizeClass.Expanded && windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium)) {
            portrait
        } else {
            landscape
        }
    } else {
        compactLayout
    }

    layout(
        padding = padding,
        tuningDisplay = {
            TuningDisplay(
                noteOffset = noteOffset,
                displayType = prefs.displayType,
                onTuned = onTuned
            )
        },
        stringControls = { inline ->
            StringControls(
                inline = inline,
                tuning = tuning,
                selectedString = selectedString,
                tuned = tuned,
                onSelect = onSelectString,
                onTuneDown = onTuneDownString,
                onTuneUp = onTuneUpString
            )
        },
        autoDetectSwitch = { modifier ->
            AutoDetectSwitch(
                modifier = modifier,
                autoDetect = autoDetect,
                onAutoChanged = onAutoChanged
            )
        },
        tuningSelector = { modifier ->
            TuningSelector(
                modifier = modifier,
                tuning = tuning,
                favTunings = favTunings,
                customTunings = customTunings,
                openDirect = false,
                onSelect = onSelectTuning,
                onTuneDown = onTuneDownTuning,
                onTuneUp = onTuneUpTuning,
                onOpenTuningSelector = onOpenTuningSelector,
                enabled = !expanded
            )
        }
    )
}

/**
 * UI screen shown to the user when the audio permission is not granted.
 *
 * @param requestAgain Whether the permission should be requested again.
 * @param fullBlack Whether the app is in full black mode.
 * @param onSettingsPressed Called when the settings navigation button is pressed.
 * @param onRequestPermission Called when the request permission button is pressed.
 * @param onOpenPermissionSettings Called when the open permission settings button is pressed.
 */
@Composable
fun TunerPermissionScreen(
    requestAgain: Boolean,
    fullBlack: Boolean,
    onSettingsPressed: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenPermissionSettings: () -> Unit,
) {
    Scaffold(
        topBar = { AppBar(fullBlack, onSettingsPressed) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val title: String
            val rationale: String
            val buttonLabel: String
            val buttonAction: () -> Unit
            if (requestAgain) {
                title = stringResource(R.string.permission_needed)
                rationale = stringResource(R.string.tuner_audio_permission_rationale)
                buttonLabel = stringResource(R.string.request_permission).uppercase()
                buttonAction = onRequestPermission
            } else {
                title = stringResource(R.string.permission_denied)
                rationale = stringResource(R.string.tuner_audio_permission_rationale_denied)
                buttonLabel = stringResource(R.string.open_permission_settings).uppercase()
                buttonAction = onOpenPermissionSettings
            }

            Text( // Title
                text = title,
                style = MaterialTheme.typography.h6
            )
            Text( // Rationale
                text = rationale,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 72.dp)
            )
            // Action Button
            Button(onClick = buttonAction) {
                Text(buttonLabel)
            }
        }
    }
}

/**
 * App bar for the tuning screen.
 * @param fullBlack Whether the app bar should be displayed with a full black background.
 * @param onSettingsPressed Called when the settings button is pressed.
 */
@Composable
private fun AppBar(
    fullBlack: Boolean,
    onSettingsPressed: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        backgroundColor = MaterialTheme.colors.primarySurfaceBackground(fullBlack),
        actions = {
            // Settings button
            IconButton(onClick = onSettingsPressed) {
                Icon(Icons.Default.Settings, stringResource(R.string.tuner_settings))
            }
        }
    )
}

/**
 * App bar for the tuning screen.
 * @param fullBlack Whether the app bar should be displayed with a full black background.
 * @param onSettingsPressed Called when the settings button is pressed.
 */
@Composable
private fun CompactAppBar(
    fullBlack: Boolean,
    onSettingsPressed: () -> Unit,
    onConfigurePressed: () -> Unit,
    tuning: Tuning,
    customTunings: State<Set<Tuning>>
) {
    TopAppBar(
        title = {
            TuningItem(tuning = tuning, customTunings = customTunings, fontWeight = FontWeight.Bold)
        },
        backgroundColor = MaterialTheme.colors.primarySurfaceBackground(fullBlack),
        actions = {
            // Configure tuning button.
            IconButton(onClick = onConfigurePressed) {
                Icon(Icons.Default.Tune, contentDescription = stringResource(R.string.configure_tuning))
            }

            // Settings button
            IconButton(onClick = onSettingsPressed) {
                Icon(Icons.Default.Settings, stringResource(R.string.tuner_settings))
            }
        }
    )
}

/**
 * Switch control allowing auto detection of string to be enabled/disabled.
 *
 * @param autoDetect Whether auto detection is enabled.
 * @param onAutoChanged Called when the switch is toggled.
 */
@Composable
private fun AutoDetectSwitch(
    modifier: Modifier = Modifier,
    autoDetect: Boolean,
    onAutoChanged: (Boolean) -> Unit
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(R.string.auto_detect_label).uppercase(),
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.paddingFromBaseline(bottom = 6.dp)
        )
        Switch(checked = autoDetect, onCheckedChange = onAutoChanged)
    }
}

// PREVIEWS

@Composable
private fun BasePreview(
    compact: Boolean = false,
    windowSizeClass: WindowSizeClass,
    prefs: TunerPreferences = TunerPreferences(),
) {
    AppTheme {
        TunerScreen(
            compact,
            expanded = false,
            windowSizeClass,
            tuning = Tunings.HALF_STEP_DOWN,
            noteOffset = remember { mutableStateOf(1.3)},
            selectedString = 1,
            tuned = BooleanArray(6) { it==4 },
            autoDetect = true,
            favTunings = remember { mutableStateOf(emptySet()) },
            customTunings = remember { mutableStateOf(emptySet()) },
            prefs,
            {}, {},{},{},{}, {}, {}, {}, {}, {}, {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@ThemePreview
@Composable
private fun TunerPreview() {
    BasePreview(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
    )
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@CompactThemePreview
@Composable
private fun CompactPreview() {
    BasePreview(
        compact = true,
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(1.dp, 1.dp)),
    )
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@LandscapePreview
@Composable
private fun LandscapePreview() {
    BasePreview(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(891.dp, 411.dp))
    )
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@LargeFontPreview
@Composable
private fun LargeFontPreview() {
    BasePreview(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
    )
}

@Preview
@Composable
private fun PermissionRequestPreview() {
    AppTheme {
        TunerPermissionScreen(
            fullBlack = false,
            requestAgain = true,
            onSettingsPressed = {},
            onRequestPermission = {},
            onOpenPermissionSettings = {}
        )
    }
}

@Preview
@Composable
private fun PermissionDeniedPreview() {
    AppTheme {
        TunerPermissionScreen(
            fullBlack = false,
            requestAgain = false,
            onSettingsPressed = {},
            onRequestPermission = {},
            onOpenPermissionSettings = {}
        )
    }
}