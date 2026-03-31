/*
 * Choona - Guitar Tuner
 * Copyright (C) 2026 Rohan Khayech
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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HdrAuto
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconToggleButton
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.rohankhayech.android.util.ui.preview.wear.WearSizePreview
import com.rohankhayech.choona.lib.R
import com.rohankhayech.choona.lib.model.preferences.TunerPreferences
import com.rohankhayech.choona.lib.model.tuning.TuningEntry
import com.rohankhayech.choona.lib.model.tuning.Tunings
import com.rohankhayech.choona.wear.view.components.CompactNoteSelector
import com.rohankhayech.choona.wear.view.components.CompactStringSelector
import com.rohankhayech.choona.wear.view.components.CurvedTuningItem
import com.rohankhayech.choona.wear.view.components.TuningDisplay
import com.rohankhayech.choona.wear.view.theme.AppTheme

/**
 * A UI screen that allows selection of a tuning and string and displays the current tuning status.
 *
 * @param granted Whether the audio permission is granted.
 * @param tuning Guitar tuning used for comparison.
 * @param noteOffset The offset between the currently playing note and the selected string.
 * @param selectedString Index of the currently selected string within the tuning.
 * @param selectedNote Index of the currently selected note within the string when in chromatic mode.
 * @param chromatic Whether the tuning is chromatic.
 * @param tuned Whether each string has been tuned.
 * @param autoDetect Whether the tuner will automatically detect the currently playing string.
 * @param getCanonicalName Gets the name of the tuning if it is saved as a custom tuning.
 * @param prefs User preferences for the tuner.
 * @param canRequest Whether the permission can be requested.
 * @param error The error to display.
 * @param onSelectString Called when a string is selected.
 * @param onAutoChanged Called when the auto detect switch is toggled.
 * @param onTuned Called when the detected note is held in tune.
 * @param onOpenConfigurePanel Called when the configure tuning panel is opened.
 * @param onRequestPermission Called when the request permission button is pressed.
 * @param onOpenPermissionSettings Called when the open permission settings button is pressed
 *
 * @author Rohan Khayech
 */
@Composable
fun TunerScreen(
    granted: Boolean,
    tuning: TuningEntry,
    prefs: TunerPreferences,
    noteOffset: State<Double?>,
    selectedString: Int,
    selectedNote: Int,
    autoDetect: Boolean,
    chromatic: Boolean,
    tuned: BooleanArray,
    noteTuned: Boolean,
    canRequest: Boolean,
    error: Exception?,
    onSelectString: (Int) -> Unit,
    onSelectNote: (Int) -> Unit,
    onAutoChanged: (Boolean) -> Unit,
    getCanonicalName: (TuningEntry.InstrumentTuning) -> String,
    onTuned: () -> Unit,
    onOpenConfigurePanel: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenPermissionSettings: () -> Unit
) {
    ScreenScaffold(timeText = if (error == null && granted) {{}} else null) { contentPadding ->
        if (error == null && granted) {
            TunerBody(
                tuning,
                prefs,
                selectedNote,
                noteOffset,
                selectedString,
                selectedNote,
                autoDetect,
                chromatic,
                tuned,
                noteTuned,
                onSelectString,
                onSelectNote,
                onAutoChanged,
                getCanonicalName,
                onTuned,
                onOpenConfigurePanel
            )
        } else if (error != null) {
            TunerErrorBody(contentPadding, error)
        } else {
            TunerPermissionBody(
                contentPadding,
                canRequest = canRequest,
                onRequestPermission = onRequestPermission,
                onOpenPermissionSettings = onOpenPermissionSettings
            )
        }
    }
}

@Composable
private fun TunerBody(
    tuning: TuningEntry,
    prefs: TunerPreferences,
    noteIndex: Int,
    noteOffset: State<Double?>,
    selectedString: Int,
    selectedNote: Int,
    autoDetect: Boolean,
    chromatic: Boolean,
    tuned: BooleanArray,
    noteTuned: Boolean,
    onSelectString: (Int) -> Unit,
    onSelectNote: (Int) -> Unit,
    onAutoChanged: (Boolean) -> Unit,
    getCanonicalName: (TuningEntry.InstrumentTuning) -> String,
    onTuned: () -> Unit,
    onOpenConfigurePanel: () -> Unit,
) {
    val round = LocalConfiguration.current.isScreenRound

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TuningDisplay(
            noteIndex = noteIndex,
            noteOffset = noteOffset,
            displayType = prefs.displayType,
            showNote = chromatic && autoDetect,
            onTuned = onTuned
        )
        Row(
            Modifier.padding(end = if (round) 48.dp else 8.dp)
        ) {
            if (chromatic) {
                CompactNoteSelector(
                    modifier = Modifier.weight(1f),
                    selectedNoteIndex = selectedNote,
                    contentPadding = PaddingValues(if (round) 48.dp else 8.dp, end = 8.dp),
                    tuned = noteTuned,
                    onSelect = onSelectNote
                )
            } else {
                CompactStringSelector(
                    modifier = Modifier.weight(1f),
                    tuning = tuning.tuning!!,
                    selectedString = selectedString,
                    contentPadding = PaddingValues(if (round) 48.dp else 8.dp, end = 8.dp),
                    tuned = tuned,
                    onSelect = onSelectString
                )
            }
            Box(
                Modifier
                    .height(32.dp)
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.outline)
            )
            Box(Modifier.padding(start = 8.dp)) {
                IconToggleButton(
                    modifier = Modifier.size(32.dp),
                    checked = autoDetect,
                    onCheckedChange = onAutoChanged
                ) {
                    Icon(
                        Icons.Default.HdrAuto,
                        stringResource(R.string.auto_detect_label)
                    )
                }
            }
        }
        if (!round) {
            CurvedTuningItem(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp).clickable(onClick = onOpenConfigurePanel),
                tuning = tuning,
                getCanonicalName = getCanonicalName,
                fontWeight = FontWeight.Bold
            )
        } else {
            Spacer(Modifier.fillMaxWidth().height(30.dp).clickable(onClick = onOpenConfigurePanel))
        }
    }
    if (round) {
        CurvedTuningItem(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            tuning = tuning,
            getCanonicalName = getCanonicalName,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * UI body shown to the user when the audio permission is not granted.
 *
 * @param contentPadding Padding to apply to the screen.
 * @param canRequest Whether the permission can be requested.
 * @param onRequestPermission Called when the request permission button is pressed.
 * @param onOpenPermissionSettings Called when the open permission settings button is pressed.
 */
@Composable
private fun TunerPermissionBody(
    contentPadding: PaddingValues,
    canRequest: Boolean,
    onRequestPermission: () -> Unit,
    onOpenPermissionSettings: () -> Unit
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

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        item {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
        // Rationale
        item {
            Text(
                text = rationale,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )
        }
        // Action Button
        item {
            Button(modifier = Modifier.padding(top = 8.dp), onClick = buttonAction) {
                Text(buttonLabel, textAlign = TextAlign.Center)
            }
        }
    }
}


/**
 * UI body shown to the user when the tuner has failed to start.
 * @param contentPadding Padding to apply to the screen.
 * @param error The error to display.
 */
@Composable
private fun TunerErrorBody(
    contentPadding: PaddingValues,
    error: Exception,
) {
    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text( // Title
                text = stringResource(R.string.error_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
        item {
            Text( // Rationale
                text = stringResource(R.string.error_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        if (error.message != null) {
            item {
                Text( // Error message
                    text = error.message!!,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        item {
            Text( // Rationale
                text = stringResource(R.string.error_action_call),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

// PREVIEWS

@Composable
private fun BasePreview(
    granted: Boolean = true,
    canRequest: Boolean = true,
    error: Exception? = null,
) {
    AppTheme {
        AppScaffold {
            TunerScreen(
                granted = granted,
                tuning = TuningEntry.InstrumentTuning(Tunings.HALF_STEP_DOWN),
                prefs = TunerPreferences(),
                noteOffset = remember { mutableDoubleStateOf(2.0) },
                selectedString = 0,
                selectedNote = 0,
                autoDetect = true,
                chromatic = false,
                tuned = booleanArrayOf(false, true, false, false, false, true),
                noteTuned = false,
                canRequest = canRequest,
                error = error,
                onTuned = {},
                onSelectString = {},
                onSelectNote = {},
                onAutoChanged = {},
                getCanonicalName = { "" },
                onOpenConfigurePanel = {},
                onRequestPermission = {},
                onOpenPermissionSettings = {}
            )
        }
    }
}

@WearSizePreview
@Composable
private fun TunerPreview() {
    BasePreview()
}

@WearSizePreview
@Composable
private fun PermissionRequestPreview() {
    BasePreview(granted = false)
}

@WearSizePreview
@Composable
private fun PermissionDeniedPreview() {
    BasePreview(granted = false, canRequest = false)
}

@WearSizePreview
@Composable
private fun ErrorPreview() {
    BasePreview(
        error = Exception("Something went wrong."),
    )
}