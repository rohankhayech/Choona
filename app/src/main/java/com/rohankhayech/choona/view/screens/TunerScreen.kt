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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
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
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.Wallpapers.BLUE_DOMINATED_EXAMPLE
import androidx.compose.ui.tooling.preview.Wallpapers.GREEN_DOMINATED_EXAMPLE
import androidx.compose.ui.tooling.preview.Wallpapers.RED_DOMINATED_EXAMPLE
import androidx.compose.ui.tooling.preview.Wallpapers.YELLOW_DOMINATED_EXAMPLE
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.rohankhayech.android.util.ui.preview.CompactThemePreview
import com.rohankhayech.android.util.ui.preview.DarkPreview
import com.rohankhayech.android.util.ui.preview.LandscapePreview
import com.rohankhayech.android.util.ui.preview.LargeFontPreview
import com.rohankhayech.android.util.ui.preview.ThemePreview
import com.rohankhayech.android.util.ui.theme.m3.isLight
import com.rohankhayech.android.util.ui.theme.m3.isTrueDark
import com.rohankhayech.android.util.ui.theme.m3.vibrantContainer
import com.rohankhayech.choona.R
import com.rohankhayech.choona.model.preferences.StringLayout
import com.rohankhayech.choona.model.preferences.TunerPreferences
import com.rohankhayech.choona.model.tuning.TuningEntry
import com.rohankhayech.choona.model.tuning.Tunings
import com.rohankhayech.choona.view.components.CompactNoteSelector
import com.rohankhayech.choona.view.components.CompactStringSelector
import com.rohankhayech.choona.view.components.NoteSelector
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
 * @param getCustomName Gets the name of the tuning if it is saved as a custom tuning.
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
    tuning: TuningEntry,
    noteOffset: State<Double?>,
    selectedString: Int,
    selectedNote: Int,
    tuned: BooleanArray,
    noteTuned: Boolean,
    autoDetect: Boolean,
    chromatic: Boolean,
    favTunings: State<Set<TuningEntry>>,
    getCustomName: TuningEntry.InstrumentTuning.() -> String,
    prefs: TunerPreferences,
    onSelectString: (Int) -> Unit,
    onSelectTuning: (Tuning) -> Unit,
    onSelectChromatic: () -> Unit,
    onSelectNote: (Int) -> Unit,
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
                ExpandedAppBar(onSettingsPressed, editModeEnabled, !chromatic, onEditModeChanged)
            } else if (!compact) {
                AppBar(onSettingsPressed, showEditToggle = !chromatic, editModeEnabled, onEditModeChanged)
            } else {
                CompactAppBar(
                    scrollBehavior,
                    tuning = tuning,
                    getCustomName = getCustomName,
                    onConfigurePressed = onConfigurePressed
                )
            }
        }
    ) { padding ->
        TunerBodyScaffold(
            padding,
            compact,
            expanded,
            tuning,
            noteOffset,
            selectedString,
            selectedNote,
            tuned,
            noteTuned,
            autoDetect,
            chromatic,
            favTunings,
            getCustomName,
            prefs,
            editModeEnabled,
            onSelectString,
            onSelectTuning,
            onSelectChromatic,
            onSelectNote,
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
                    stringControls(Modifier, prefs.stringLayout == StringLayout.INLINE)
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
                        Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier.fillMaxHeight().windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start)),
                        ) {
                            tuningDisplay()
                        }
                        stringControls(
                            Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.End)),
                            windowSizeClass.heightSizeClass > WindowHeightSizeClass.Compact
                                && prefs.stringLayout == StringLayout.INLINE,
                        )
                    }
                    Row(
                        Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        tuningSelector(Modifier.weight(1f))
                        autoDetectSwitch(Modifier.padding(end = 20.dp))
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
                            .height(72.dp)
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (chromatic) {
                            CompactNoteSelector(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 8.dp),
                                selectedNoteIndex = selectedNote,
                                tuned = noteTuned,
                                onSelect = onSelectNote,
                            )
                        } else {
                        CompactStringSelector(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp),
                            tuning = tuning.tuning!!,
                            selectedString = selectedString,
                            tuned = tuned,
                            onSelect = onSelectString,
                        )
                        }
                        VerticalDivider()
                        Box(Modifier.padding(horizontal = 8.dp)) {
                            autoDetectSwitch(Modifier)
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
    stringControls: @Composable (modifier: Modifier, inline: Boolean) -> Unit,
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
 * @param tuning Guitar tuning used for comparison.
 * @param noteOffset The offset between the currently playing note and the selected string.
 * @param selectedString Index of the currently selected string within the tuning.
 * @param tuned Whether each string has been tuned.
 * @param autoDetect Whether the tuner will automatically detect the currently playing string.
 * @param favTunings Set of tunings marked as favourite by the user.
 * @param getCustomName Gets the name of the tuning if it is saved as a custom tuning.
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
    tuning: TuningEntry,
    noteOffset: State<Double?>,
    selectedString: Int,
    selectedNote: Int,
    tuned: BooleanArray,
    noteTuned: Boolean,
    autoDetect: Boolean,
    chromatic: Boolean,
    favTunings: State<Set<TuningEntry>>,
    getCustomName: TuningEntry.InstrumentTuning.() -> String,
    prefs: TunerPreferences,
    editModeEnabled: Boolean,
    onSelectString: (Int) -> Unit,
    onSelectTuning: (Tuning) -> Unit,
    onSelectChromatic: () -> Unit,
    onSelectNote: (Int) -> Unit,
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
    // Calculate size of tuning pane.
    val paneSize = with(LocalDensity.current) {
        val windowSize = LocalWindowInfo.current.containerSize.toSize().toDpSize()
        DpSize(
            // Subtract width of selection pane in expanded layout
            width = if (expanded) windowSize.width * 0.7f else windowSize.width,
            height = windowSize.height
        )
    }

    // Determine layout to use
    val layout = if (!compact) {
        if (paneSize.height < paneSize.width || (expanded && prefs.stringLayout == StringLayout.INLINE)) {
            landscape
        } else {
            portrait
        }
    } else {
        compactLayout
    }

    // Inject components into layout.
    layout(
        padding,
        // Tuning Display
        {
            TuningDisplay(
                noteIndex = selectedNote,
                noteOffset = noteOffset,
                showNote = chromatic && autoDetect,
                displayType = prefs.displayType,
                onTuned = onTuned
            )
        },
        // String controls
        { modifier, inline ->
            if (chromatic) {
                NoteSelector(
                    modifier = modifier,
                    selectedNoteIndex = selectedNote,
                    tuned = noteTuned,
                    onSelect = onSelectNote,
                )
            } else {
                StringControls(
                    modifier = modifier,
                    inline = inline,
                    tuning = tuning.tuning!!,
                    selectedString = selectedString,
                    tuned = tuned,
                    onSelect = onSelectString,
                    onTuneDown = onTuneDownString,
                    onTuneUp = onTuneUpString,
                    editModeEnabled = editModeEnabled
                )
            }
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
                getCustomName,
                openDirect = false,
                onSelect = {
                    if (it is TuningEntry.InstrumentTuning) {
                        onSelectTuning(it.tuning)
                    } else {
                        onSelectChromatic()
                    }
                },
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
 * UI screen shown to the user when the tuner has failed to start.
 * @param error The error to display.
 * @param onSettingsPressed Called when the settings navigation button is pressed.
 */
@Composable
fun TunerErrorScreen(
    error: Exception?,
    onSettingsPressed: () -> Unit,
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
            Text( // Title
                text = stringResource(R.string.error_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error
            )
            Text( // Rationale
                text = stringResource(R.string.error_description),
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 256.dp)
            )
            if (error?.message != null) {
                Text( // Error message
                    text = error.message!!,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 256.dp),
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Text( // Rationale
                text = stringResource(R.string.error_action_call),
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 256.dp)
            )
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
            AppBarActions(showEditToggle, editModeEnabled, onEditModeChanged, onSettingsPressed)
        }
    )
}

/**
 * Action buttons for the app bar.
 * @param showEditToggle Whether to show the edit mode toggle button.
 * @param editModeEnabled Whether tuning editing is enabled.
 * @param onEditModeChanged Called when the edit mode toggle button is pressed.
 * @param onSettingsPressed Called when the settings button is pressed.
 */
@Composable
private fun AppBarActions(
    showEditToggle: Boolean,
    editModeEnabled: Boolean,
    onEditModeChanged: (Boolean) -> Unit,
    onSettingsPressed: () -> Unit
) {
    if (showEditToggle) {
        // Toggle tuning button
        IconToggleButton(
            colors = IconButtonDefaults.iconToggleButtonColors().copy(
                checkedContentColor = LocalContentColor.current
            ),
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

/**
 * App bar for the tuning screen in compact layout.
 * @param scrollBehavior Scroll behavior for the app bar.
 * @param onConfigurePressed Called when the configure tuning button is pressed.
 * @param tuning Current tuning.
 * @param getCustomName Gets the name of the tuning if it is saved as a custom tuning.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onConfigurePressed: () -> Unit,
    tuning: TuningEntry,
    getCustomName: TuningEntry.InstrumentTuning.() -> String,
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            Text(text = stringResource(R.string.app_name), modifier = Modifier.padding(start = 16.dp), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
        },
        title = {
            TuningItem(
                tuning = tuning,
                compact = true,
                getCustomName = getCustomName,
                fontWeight = FontWeight.Bold,
                horizontalAlignment = Alignment.CenterHorizontally
            )
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
 * App bar for the tuning screen in expanded layout.
 * @param onSettingsPressed Called when the settings button is pressed.
 * @param editModeEnabled Whether tuning editing is enabled.
 * @param onEditModeChanged Called when the edit mode toggle button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandedAppBar(
    onSettingsPressed: () -> Unit,
    editModeEnabled: Boolean = false,
    showEditToggle: Boolean,
    onEditModeChanged: ((Boolean) -> Unit) = {}
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.app_name),
                 fontWeight = FontWeight.Bold,
                 color = MaterialTheme.colorScheme.primary,
                 style = MaterialTheme.typography.headlineSmall
            )
        },
        actions = {
            AppBarActions(showEditToggle, editModeEnabled, onEditModeChanged, onSettingsPressed)
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
    trueDark: Boolean = false,
    dynamicColor: Boolean = false,
) {
    AppTheme(dynamicColor = dynamicColor, fullBlack = trueDark) {
        TunerScreen(
            compact,
            expanded = false,
            windowSizeClass,
            tuning = TuningEntry.InstrumentTuning(Tunings.HALF_STEP_DOWN),
            noteOffset = remember { mutableDoubleStateOf(1.3) },
            selectedString = 1,
            selectedNote = -28,
            tuned = BooleanArray(6) { it==4 },
            noteTuned = false,
            autoDetect = true,
            chromatic = false,
            favTunings = remember { mutableStateOf(emptySet()) },
            getCustomName = { this.tuning.toString() },
            prefs,
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
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
@DarkPreview
@Composable
private fun TrueDarkPreview() {
    BasePreview(
        trueDark = true,
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp))
    )
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@PreviewDynamicColors
@Preview(name = "Red", wallpaper = RED_DOMINATED_EXAMPLE, uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Blue", wallpaper = BLUE_DOMINATED_EXAMPLE, uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Green", wallpaper = GREEN_DOMINATED_EXAMPLE, uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Yellow", wallpaper = YELLOW_DOMINATED_EXAMPLE, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DynamicPreview() {
    BasePreview(
        dynamicColor = true,
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

@Preview
@Composable
private fun ErrorPreview() {
    AppTheme {
        TunerErrorScreen (
            error = Exception("Something went wrong."),
            onSettingsPressed = {},
        )
    }
}