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

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.rohankhayech.android.util.ui.theme.m3.isLight
import com.rohankhayech.android.util.ui.theme.m3.isTrueDark
import com.rohankhayech.android.util.ui.theme.m3.vibrantContainer
import com.rohankhayech.choona.R
import com.rohankhayech.choona.model.preferences.StringLayout
import com.rohankhayech.choona.model.preferences.TunerPreferences
import com.rohankhayech.choona.model.tuning.Tunings
import com.rohankhayech.choona.view.components.CompactStringSelector
import com.rohankhayech.choona.view.components.StatusBarColor
import com.rohankhayech.choona.view.components.StatusBarIconColor
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
 * @param editModeEnabled Whether tuning editing is enabled.
 * @param onEditModeChanged Called when the edit mode toggle button is pressed.
 *
 * @author Rohan Khayech
 */
@OptIn(ExperimentalMaterial3Api::class)
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
    onConfigurePressed: () -> Unit,
    editModeEnabled: Boolean,
    onEditModeChanged: (Boolean) -> Unit
) {
    val scrollBehavior = if (!compact) TopAppBarDefaults.pinnedScrollBehavior() else TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold (
        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (expanded) {
                ExpandedAppBar(scrollBehavior, onSettingsPressed, editModeEnabled, onEditModeChanged)
            } else if (!compact) {
                AppBar(onSettingsPressed, showEditToggle = true, editModeEnabled, onEditModeChanged)
            } else {
                CompactAppBar(
                    scrollBehavior,
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
            editModeEnabled,
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
                    modifier = Modifier.fillMaxSize()
                        .padding(padd)
                        .consumeWindowInsets(padd)
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    tuningDisplay()
                    stringControls(prefs.stringLayout == StringLayout.INLINE)
                    autoDetectSwitch(Modifier)
                    tuningSelector(Modifier.padding(vertical = 8.dp))
                }
            },

            // Landscape layout
            landscape = { padd, tuningDisplay, stringControls, autoDetectSwitch, tuningSelector ->
                Column (
                    Modifier.fillMaxSize()
                        .padding(padd)
                        .consumeWindowInsets(padd)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start)),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            tuningDisplay()
                            autoDetectSwitch(Modifier)
                        }
                        stringControls(
                            Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.End)),
                            windowSizeClass.heightSizeClass > WindowHeightSizeClass.Compact
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
                        VerticalDivider()
                        Box(Modifier.padding(horizontal = 8.dp)) {
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
    editModeEnabled: Boolean,
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
    compactLayout: TunerBodyLayout,
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
        padding,
        // Tuning Display
        {
            TuningDisplay(
                noteOffset = noteOffset,
                displayType = prefs.displayType,
                onTuned = onTuned
            )
        },
        // String controls
        { inline ->
            StringControls(
                inline = inline,
                tuning = tuning,
                selectedString = selectedString,
                tuned = tuned,
                onSelect = onSelectString,
                onTuneDown = onTuneDownString,
                onTuneUp = onTuneUpString,
                editModeEnabled = editModeEnabled
            )
        },
        // Auto Detect Switch
        { modifier ->
            AutoDetectSwitch(
                modifier = modifier,
                autoDetect = autoDetect,
                onAutoChanged = onAutoChanged
            )
        },
        // Tuning Selector
        { modifier ->
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
                enabled = !expanded,
                editModeEnabled = editModeEnabled,
                compact = compact
            )
        }
    )
}

/**
 * UI screen shown to the user when the audio permission is not granted.
 *
 * @param canRequest Whether the permission can be requested.
 * @param onSettingsPressed Called when the settings navigation button is pressed.
 * @param onRequestPermission Called when the request permission button is pressed.
 * @param onOpenPermissionSettings Called when the open permission settings button is pressed.
 */
@Composable
fun TunerPermissionScreen(
    canRequest: Boolean,
    onSettingsPressed: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenPermissionSettings: () -> Unit,
) {
    Scaffold(
        topBar = { AppBar(onSettingsPressed, false) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val title: String
            val rationale: String
            val buttonLabel: String
            val buttonAction: () -> Unit
            if (canRequest) {
                title = stringResource(R.string.permission_needed)
                rationale = stringResource(R.string.tuner_audio_permission_rationale)
                buttonLabel = stringResource(R.string.request_permission)
                buttonAction = onRequestPermission
            } else {
                title = stringResource(R.string.permission_denied)
                rationale = stringResource(R.string.tuner_audio_permission_rationale_denied)
                buttonLabel = stringResource(R.string.open_permission_settings)
                buttonAction = onOpenPermissionSettings
            }

            Text( // Title
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            Text( // Rationale
                text = rationale,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 256.dp)
            )
            // Action Button
            Button(onClick = buttonAction) {
                Text(buttonLabel, textAlign = TextAlign.Center)
            }
        }
    }
}

/**
 * App bar for the tuning screen.
 * @param onSettingsPressed Called when the settings button is pressed.
 * @param editModeEnabled Whether tuning editing is enabled.
 * @param onEditModeChanged Called when the edit mode toggle button is pressed.
 * @param showEditToggle Whether to show the edit mode toggle button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    onSettingsPressed: () -> Unit,
    showEditToggle: Boolean,
    editModeEnabled: Boolean = false,
    onEditModeChanged: ((Boolean) -> Unit) = {}
) {
    StatusBarColor(iconColor = StatusBarIconColor.LIGHT)
    TopAppBar(
        title = {
            Text(stringResource(R.string.app_name),
                 fontWeight = FontWeight.Bold,
                 color = if (MaterialTheme.isTrueDark && !MaterialTheme.isLight)
                     MaterialTheme.colorScheme.primary else Color.Unspecified
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.vibrantContainer,
            titleContentColor = contentColorFor(MaterialTheme.colorScheme.vibrantContainer),
            actionIconContentColor = contentColorFor(MaterialTheme.colorScheme.vibrantContainer)
        ),
        actions = {
            if (showEditToggle) {
                // Toggle tuning button
                IconToggleButton(
                    checked = editModeEnabled,
                    onCheckedChange = onEditModeChanged
                ) {
                    Icon(
                        imageVector = if (editModeEnabled) Icons.Default.EditOff else Icons.Default.Edit,
                        contentDescription = stringResource(R.string.toggle_edit_mode)
                    )
                }
            }

            // Settings button
            IconButton(onClick = onSettingsPressed) {
                Icon(Icons.Default.Settings, stringResource(R.string.tuner_settings))
            }
        }
    )
}

/**
 * App bar for the tuning screen.
 * @param onSettingsPressed Called when the settings button is pressed.
 * @param editModeEnabled Whether tuning editing is enabled.
 * @param onEditModeChanged Called when the edit mode toggle button is pressed.
 * @param showEditToggle Whether to show the edit mode toggle button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandedAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onSettingsPressed: () -> Unit,
    editModeEnabled: Boolean = false,
    onEditModeChanged: ((Boolean) -> Unit) = {}
) {
    LargeTopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        /*colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primarySurfaceBackground(
                MaterialTheme.isTrueDark
            ),
        ),*/
        actions = {
            // Toggle tuning button
            IconToggleButton(
                checked = editModeEnabled,
                onCheckedChange = onEditModeChanged
            ) {
                Icon(
                    imageVector = if (editModeEnabled) Icons.Default.EditOff else Icons.Default.Edit,
                    contentDescription = stringResource(R.string.toggle_edit_mode)
                )
            }


            // Settings button
            IconButton(onClick = onSettingsPressed) {
                Icon(Icons.Default.Settings, stringResource(R.string.tuner_settings))
            }
        },
        scrollBehavior = scrollBehavior
    )
}

/**
 * App bar for the tuning screen.
 * @param onSettingsPressed Called when the settings button is pressed.
 * @param onConfigurePressed Called when the configure tuning button is pressed.
 * @param tuning Current tuning.
 * @param customTunings Set of custom tunings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onConfigurePressed: () -> Unit,
    tuning: Tuning,
    customTunings: State<Set<Tuning>>
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            Text(text = stringResource(R.string.app_name), modifier = Modifier.padding(start = 16.dp), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
        },
        title = {
            TuningItem(tuning = tuning, compact = true, customTunings = customTunings, fontWeight = FontWeight.Bold, horizontalAlignment = Alignment.CenterHorizontally)
        },
        actions = {
            // Configure tuning button.
            IconButton(onClick = onConfigurePressed) {
                Icon(Icons.Default.Tune, contentDescription = stringResource(R.string.configure_tuning))
            }
        },
        scrollBehavior = scrollBehavior
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
        modifier.clickable { onAutoChanged(!autoDetect) }.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.auto_detect_label).uppercase(),
            style = MaterialTheme.typography.titleSmall,
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
            noteOffset = remember { mutableDoubleStateOf(1.3) },
            selectedString = 1,
            tuned = BooleanArray(6) { it==4 },
            autoDetect = true,
            favTunings = remember { mutableStateOf(emptySet()) },
            customTunings = remember { mutableStateOf(emptySet()) },
            prefs,
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
            editModeEnabled = true,
            onEditModeChanged = {}
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
            canRequest = true,
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
            canRequest = false,
            onSettingsPressed = {},
            onRequestPermission = {},
            onOpenPermissionSettings = {}
        )
    }
}