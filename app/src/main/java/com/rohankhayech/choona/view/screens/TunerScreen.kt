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

import kotlin.math.abs
import kotlin.math.sign
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.rohankhayech.choona.R
import com.rohankhayech.choona.controller.tuner.Tuner
import com.rohankhayech.choona.model.preferences.StringLayout
import com.rohankhayech.choona.model.preferences.TunerPreferences
import com.rohankhayech.choona.model.preferences.TuningDisplayType
import com.rohankhayech.choona.model.tuning.Tunings
import com.rohankhayech.choona.view.theme.AppTheme
import com.rohankhayech.choona.view.theme.Yellow500
import com.rohankhayech.music.GuitarString
import com.rohankhayech.music.Tuning

/**
 * A UI screen that allows selection of a tuning and string and displays the current tuning status.
 *
 * @param windowHeightSizeClass Height size class of the activity window.
 * @param tuning Guitar tuning used for comparison.
 * @param noteOffset The offset between the currently playing note and the selected string.
 * @param selectedString Index of the currently selected string within the tuning.
 * @param tuned: Whether each string has been tuned.
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
 *
 * @author Rohan Khayech
 */
@Composable
fun TunerScreen(
    compact: Boolean = false,
    windowHeightSizeClass: WindowHeightSizeClass,
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
            windowHeightSizeClass,
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
                            inline = windowHeightSizeClass > WindowHeightSizeClass.Compact
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
                        .padding(padding)
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

private typealias TunerBodyLayout = @Composable (
    padding: PaddingValues,
    tuningDisplay: @Composable () -> Unit,
    stringControls: @Composable (inline: Boolean) -> Unit,
    autoDetectSwitch: @Composable (modifier: Modifier) -> Unit,
    tuningSelector: @Composable (modifier: Modifier) -> Unit
) -> Unit

@Composable
private fun TunerBodyScaffold(
    padding: PaddingValues,
    compact: Boolean = false,
    windowHeightSizeClass: WindowHeightSizeClass,
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
        if (windowHeightSizeClass != WindowHeightSizeClass.Compact) {
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
                onSelect = onSelectTuning,
                onTuneDown = onTuneDownTuning,
                onTuneUp = onTuneUpTuning,
                onOpenTuningSelector = onOpenTuningSelector,
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
        backgroundColor = if (fullBlack && !MaterialTheme.colors.isLight) MaterialTheme.colors.background
            else MaterialTheme.colors.primarySurface,
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
        backgroundColor = if (fullBlack && !MaterialTheme.colors.isLight) MaterialTheme.colors.background
        else MaterialTheme.colors.primarySurface,
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
 * Visual meter and text label displaying the current [offset][noteOffset].
 *
 * @param noteOffset The offset between the currently playing note and the selected string.
 * @param displayType Type of tuning offset value to display.
 * @param onTuned Called when the detected note is held in tune.
 */
@Composable
private fun TuningDisplay(
    noteOffset: State<Double?>,
    displayType: TuningDisplayType,
    onTuned: () -> Unit
) {
    val offset = noteOffset.value

    // Calculate meter position.
    val meterPosition by animateFloatAsState(
        remember(offset) { derivedStateOf {
            if (offset != null) {
                (offset.toFloat() / 4f).coerceIn(-1f..1f)
            } else {
                0f
            }
        }}.value
    )
    val absPosition = abs(meterPosition)

    // Calculate colour of meter and label.
    val color by animateColorAsState(run {
        val pri = MaterialTheme.colors.primary
        val err = MaterialTheme.colors.error
        val onBack = MaterialTheme.colors.onBackground
        val back = MaterialTheme.colors.background

        remember(absPosition) { derivedStateOf {
            if (absPosition != 0f) {
                // Gradient from green to red based on offset.
                if (absPosition < 0.5) {
                    lerp(pri, Yellow500, absPosition * 2f)
                } else {
                    lerp(Yellow500, err, (absPosition - 0.5f) * 2f)
                }
            } else {
                // Listening color.
                onBack.copy(alpha = 0.2f).compositeOver(back)
            }
        }}.value
    })

    val inTune = offset != null && abs(offset) < Tuner.TUNED_OFFSET_THRESHOLD

    val indicatorSize by animateFloatAsState(
        targetValue = if (inTune) 1f else 2/180f,
        animationSpec = if (inTune) tween(Tuner.TUNED_SUSTAIN_TIME-50, 50) else spring(),
        finishedListener = {
            if(it == 1f) { onTuned() }
        }
    )

    // Content
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TuningMeter(
            indicatorPosition = meterPosition,
            indicatorSize = indicatorSize,
            color = color
        ) {
            TuningMeterLabel(noteOffset = offset, color = color, displayType = displayType)
        }
    }
}

/**
 * Meter visually displaying the current tuning offset.
 *
 * @param indicatorPosition Position of the indicator on the track, as a percentage value from -1.0 (leftmost) to 1.0 (rightmost).
 * @param indicatorSize Size of the indicator as a percentage value from 0.0 (no width) to 1.0 (full width of meter).
 * @param color Color of the indicator and track.
 * @param labelContent Label to display inside the meter arc.
 */
@Composable
private fun TuningMeter(
    indicatorPosition: Float,
    indicatorSize: Float,
    color: Color,
    labelContent: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .size(210.dp, 116.dp)
            .drawBehind {
                drawMeter(
                    indicatorColor = color,
                    indicatorPosition = indicatorPosition,
                    indicatorSize = indicatorSize
                )
            },
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        labelContent()
    }
}

/**
 * Draws a circular meter with a variable-size indicator and background track.
 *
 * @param indicatorColor Color of the indicator.
 * @param trackColor Color of the background track. Defaults to a faded copy of the indicator color.
 * @param indicatorPosition Position of the indicator on the track, as a percentage value from -1.0 (leftmost) to 1.0 (rightmost).
 * @param indicatorSize Size of the indicator as a percentage value from 0.0 (no width) to 1.0 (full width of meter).
 */
private fun DrawScope.drawMeter(
    indicatorColor: Color,
    trackColor: Color = indicatorColor.copy(alpha = SliderDefaults.InactiveTrackAlpha),
    indicatorPosition: Float,
    indicatorSize: Float,
) {
    // Arc size
    val strokeWidth = 20.dp.toPx()
    val arcSize = size.copy(height = size.height*2 - strokeWidth*2, width = size.width - strokeWidth)
    val offset = Offset(strokeWidth/2, strokeWidth/2)

    // Background Track
    drawArc(
        color = trackColor,
        startAngle = -180f,
        sweepAngle = 180f,
        size = arcSize,
        topLeft = offset,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round
        ),
        useCenter = false
    )

    // Indicator
    val startAngle = -90f
    val indicatorSpan = indicatorSize * 180f
    val indicatorAngle = indicatorPosition * (90f - (indicatorSpan/2)) - (indicatorSpan/2)
    drawArc(
        color = indicatorColor,
        startAngle = startAngle + indicatorAngle,
        sweepAngle = indicatorSpan,
        size = arcSize,
        topLeft = offset,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round
        ),
        useCenter = false
    )
}

/**
 * Label displaying the [note offset][noteOffset]
 * and tuning state with the specified [color] and [displayType].
 */
@Composable
private fun TuningMeterLabel(
    noteOffset: Double?,
    displayType: TuningDisplayType,
    color: Color
) {
    Spacer(modifier = Modifier.height(24.dp))

    // Listening
    if (noteOffset == null) {
        Icon(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .size(48.dp),
            tint = color,
            imageVector = Icons.Default.GraphicEq,
            contentDescription = null
        )
        Text(text = stringResource(R.string.listening))

    // In Tune
    } else if (abs(noteOffset) < Tuner.TUNED_OFFSET_THRESHOLD) {
        Icon(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .size(48.dp),
            tint = color,
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null
        )
        Text(text = stringResource(R.string.in_tune))

    // Out of Tune
    } else {
        val offset = noteOffset * displayType.multiplier
        val dp = if (displayType == TuningDisplayType.SEMITONES) 1 else 0 // decimal places

        Text( // Offset Value
            color = color,
            text = "%+.${dp}f".format(offset),
            style = MaterialTheme.typography.h3
        )
        Text(text = when (displayType) {
            TuningDisplayType.SIMPLE -> if (noteOffset.sign > 0) stringResource(R.string.tune_down) else stringResource(R.string.tune_up)
            TuningDisplayType.SEMITONES -> stringResource(R.string.semitones)
            TuningDisplayType.CENTS -> stringResource(R.string.cents)
        })
    }
}

/**
 * Component displaying each string in the current [tuning] and allowing selection of a string for tuning.
 * @param inline Whether to display the string controls inline or side-by-side.
 * @param tuning Current guitar tuning used for comparison.
 * @param selectedString Index of the selected string in the tuning.
 * @param tuned Whether each string has been tuned.
 * @param onSelect Called when a string is selected.
 * @param onTuneDown Called when a string is tuned down.
 * @param onTuneUp Called when a string is tuned up.
 */
@Composable
fun StringControls(
    inline: Boolean,
    tuning: Tuning,
    selectedString: Int?,
    tuned: BooleanArray?,
    onSelect: (Int) -> Unit,
    onTuneDown: (Int) -> Unit,
    onTuneUp: (Int) -> Unit
) {
    Box(Modifier.padding(8.dp)) {
        if (inline) {
            InlineStringControls(
                tuning = tuning,
                selectedString = selectedString,
                tuned = tuned,
                onSelect = onSelect,
                onTuneDown = onTuneDown,
                onTuneUp = onTuneUp
            )
        } else {
            SideBySideStringControls(
                tuning = tuning,
                selectedString = selectedString,
                tuned = tuned,
                onSelect = onSelect,
                onTuneDown = onTuneDown,
                onTuneUp = onTuneUp
            )
        }
    }
}

/**
 * Component displaying each string in the current [tuning] side-by-side and allowing selection of a string for tuning.
 * @param tuning Current guitar tuning used for comparison.
 * @param selectedString Index of the selected string in the tuning.
 * @param tuned Whether each string has been tuned.
 * @param onSelect Called when a string is selected.
 * @param onTuneDown Called when a string is tuned down.
 * @param onTuneUp Called when a string is tuned up.
 */
@Composable
private fun SideBySideStringControls(
    tuning: Tuning,
    selectedString: Int?,
    tuned: BooleanArray?,
    onSelect: (Int) -> Unit,
    onTuneDown: (Int) -> Unit,
    onTuneUp: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = spacedBy(16.dp)
    ) {
        val splitTuning = remember(tuning) {
            tuning.mapIndexed { n, gs -> Pair(n, gs) }
                .reversed()
                .chunked(tuning.numStrings()/2)
        }

        InlineStringControls(
            tuning = tuning,
            strings = remember(tuning) { splitTuning[0].reversed() },
            selectedString = selectedString,
            tuned = tuned,
            onSelect = onSelect,
            onTuneDown = onTuneDown,
            onTuneUp = onTuneUp
        )
        InlineStringControls(
            tuning = tuning,
            strings = splitTuning[1],
            selectedString = selectedString,
            tuned = tuned,
            onSelect = onSelect,
            onTuneDown = onTuneDown,
            onTuneUp = onTuneUp
        )
    }
}

/**
 * Component displaying the specified [strings] inline and allowing selection of a string for tuning.
 * @param tuning Current guitar tuning used for comparison.
 * @param strings Strings to display in this selector and their indexes within the tuning. Defaults to [tuning].
 * @param selectedString Index of the selected string in the tuning.
 * @param tuned Whether each string has been tuned.
 * @param onSelect Called when a string is selected.
 * @param onTuneDown Called when a string is tuned down.
 * @param onTuneUp Called when a string is tuned up.
 */
@Composable
private fun InlineStringControls(
    tuning: Tuning,
    strings: List<Pair<Int, GuitarString>> = remember(tuning) { tuning.mapIndexed { n, gs -> Pair(n, gs) } },
    selectedString: Int?,
    tuned: BooleanArray?,
    onSelect: (Int) -> Unit,
    onTuneDown: (Int) -> Unit,
    onTuneUp: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        strings.forEach {
            val (index, string) = it
            StringControl(
                index = index,
                string = string,
                selected = selectedString == index,
                tuned = tuned?.get(index) ?: false,
                onSelect = onSelect,
                onTuneDown = onTuneDown,
                onTuneUp = onTuneUp,
            )
        }
    }
}

/**
 * Component displaying the specified [strings] inline horizontally and allowing selection of a string for tuning.
 * @param tuning Current guitar tuning used for comparison.
 * @param strings Strings to display in this selector and their indexes within the tuning. Defaults to [tuning].
 * @param selectedString Index of the selected string in the tuning.
 * @param tuned Whether each string has been tuned.
 * @param onSelect Called when a string is selected.
 */
@Composable
private fun CompactStringSelector(
    modifier: Modifier = Modifier,
    tuning: Tuning,
    strings: List<Pair<Int, GuitarString>> = remember(tuning) { tuning.mapIndexed { n, gs -> Pair(n, gs) }.reversed() },
    selectedString: Int,
    tuned: BooleanArray,
    onSelect: (Int) -> Unit,
) {
    val scrollState = rememberScrollState()

    val selectedStringButtonPosition = with(LocalDensity.current) {
        remember(tuning, selectedString) {
            (72.dp * (strings.size -1 - selectedString)).toPx()
        }
    }
    LaunchedEffect(key1 = selectedString) {
        scrollState.animateScrollTo(selectedStringButtonPosition.toInt())
    }

    Row(
        modifier = modifier.horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(8.dp))
        strings.forEach {
            val (index, string) = it
            StringSelectionButton(
                index = index,
                string = string,
                selected = selectedString == index,
                tuned = tuned[index],
                onSelect = onSelect
            )
        }
        Spacer(Modifier.width(8.dp))
    }
}

/**
 * Row of buttons allowing selection and retuning of the specified string.
 *
 * @param index Index of the string within the tuning.
 * @param string The guitar string.
 * @param selected Whether the string is currently selected for tuning.
 * @param onSelect Called when the string is selected.
 * @param onTuneDown Called when the string is tuned down.
 * @param onTuneUp Called when the string is tuned up.
 */
@Composable
private fun StringControl(
    index: Int,
    string: GuitarString,
    selected: Boolean,
    tuned: Boolean,
    onSelect: (Int) -> Unit,
    onTuneDown: (Int) -> Unit,
    onTuneUp: (Int) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Tune Down Button
        IconButton(
            onClick = remember(onTuneDown, index) { { onTuneDown(index) } },
            enabled = remember(string) { derivedStateOf { string.rootNoteIndex > Tuner.LOWEST_NOTE } }.value
        ) {
            Icon(Icons.Default.Remove, stringResource(R.string.tune_down))
        }

        StringSelectionButton(tuned, selected, onSelect, index, string)

        // Tune Up Button
        IconButton(
            onClick = remember(onTuneUp, index) { { onTuneUp(index) } },
            enabled = remember(string) { derivedStateOf { string.rootNoteIndex < Tuner.HIGHEST_NOTE } }.value
        ) {
            Icon(Icons.Default.Add, stringResource(R.string.tune_up))
        }
    }
}

/**
 * Buttons displaying and allowing user selection of the specified string.
 *
 * @param index Index of the string within the tuning.
 * @param string The guitar string.
 * @param selected Whether the string is currently selected for tuning.
 * @param onSelect Called when the string is selected.
 */
@Composable
private fun StringSelectionButton(
    tuned: Boolean,
    selected: Boolean,
    onSelect: (Int) -> Unit,
    index: Int,
    string: GuitarString
) {
    // Animate content color by selected and tuned state.
    val contentColor by animateColorAsState(
        if (tuned) MaterialTheme.colors.primary
        else if (selected) MaterialTheme.colors.secondaryVariant
        else LocalContentColor.current
    )

    // Animate background color by selected state.
    val backgroundColor by animateColorAsState(
        if (selected) {
            contentColor.copy(alpha = 0.12f)
                .compositeOver(MaterialTheme.colors.background)
        } else MaterialTheme.colors.background
    )

    // Selection Button
    OutlinedButton(
        modifier = Modifier.size(72.dp, 48.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
        ),
        shape = RoundedCornerShape(100),
        onClick = remember(onSelect, index) { { onSelect(index) } }
    ) {
        Text(string.toFullString(), modifier = Modifier.padding(4.dp))
    }
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

/**
 * Row displaying and allowing selection and retuning of the current tuning.
 *
 * @param tuning The current guitar tuning.
 * @param favTunings Set of tunings marked as favourite by the user.
 * @param customTunings Set of custom tunings added by the user.
 * @param onSelect Called when a tuning is selected.
 * @param onTuneDown Called when the tuning is tuned down.
 * @param onTuneUp Called when the tuning is tuned up.
 * @param onOpenTuningSelector Called when the user opens the tuning selector screen.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TuningSelector(
    modifier: Modifier = Modifier,
    tuning: Tuning,
    favTunings: State<Set<Tuning>>,
    customTunings: State<Set<Tuning>>,
    onSelect: (Tuning) -> Unit,
    onTuneDown: () -> Unit,
    onTuneUp: () -> Unit,
    onOpenTuningSelector: () -> Unit,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Tune Down Button
        IconButton(
            onClick = onTuneDown,
            enabled = remember(tuning) { derivedStateOf { tuning.min().rootNoteIndex > Tuner.LOWEST_NOTE } }.value
        ) {
            Icon(Icons.Default.Remove, stringResource(R.string.tune_down))
        }

        // Tuning Display and Selection
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            modifier = Modifier.weight(1f),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {

            // Current Tuning
            CurrentTuningField(
                tuning = tuning,
                customTunings = customTunings,
                expanded = expanded
            )

            // Dropdown Menu
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                for (tuningOption in favTunings.value) {
                    DropdownMenuItem(
                        onClick = {
                            onSelect(tuningOption)
                            expanded = false
                        }
                    ) {
                        TuningItem(modifier = Modifier.padding(vertical = 8.dp), tuning = tuningOption, fontWeight = FontWeight.Normal, customTunings = customTunings)
                    }
                }
                DropdownMenuItem(onClick = {
                    onOpenTuningSelector()
                    expanded = false
                }) {
                    Text(stringResource(R.string.open_tuning_selector))
                }
            }
        }

        // Tune Up Button
        IconButton(
            onClick = onTuneUp,
            enabled = remember(tuning) { derivedStateOf { tuning.max().rootNoteIndex < Tuner.HIGHEST_NOTE } }.value
        ) {
            Icon(Icons.Default.Add, stringResource(R.string.tune_up))
        }
    }
}

/**
 * Outlined dropdown box field showing the current tuning.
 *
 * @param tuning The current guitar tuning.
 * @param customTunings Set of custom tunings added by the user.
 * @param expanded Whether the dropdown box is expanded.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CurrentTuningField(
    tuning: Tuning,
    customTunings: State<Set<Tuning>>,
    expanded: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = TextFieldDefaults.OutlinedTextFieldShape,
        color = MaterialTheme.colors.background,
        border = BorderStroke(
            width = if (expanded) TextFieldDefaults.FocusedBorderThickness
            else TextFieldDefaults.UnfocusedBorderThickness,
            color = if (expanded) MaterialTheme.colors.primary
            else MaterialTheme.colors.onBackground.copy(alpha = TextFieldDefaults.UnfocusedIndicatorLineOpacity)
        ),
    ) {
        Row(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TuningItem(modifier = Modifier.weight(1f), tuning = tuning, customTunings = customTunings, fontWeight = FontWeight.Bold)
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
        }
    }
}

/**
 * UI component displaying the name and strings of the specified tuning.
 *
 * @param modifier The modifier to apply to this layout.
 * @param tuning The tuning to display.
 * @param fontWeight The font weight of the tuning name text.
 * @param customTunings Set of custom tunings added by the user.
 */
@Composable
private fun TuningItem(
    modifier: Modifier = Modifier,
    tuning: Tuning,
    fontWeight: FontWeight,
    customTunings: State<Set<Tuning>>,
) {
    val tuningName = remember(tuning, customTunings) {
        if (tuning.hasName()) {
            tuning.name
        } else {
            tuning.findEquivalentIn(customTunings.value + Tunings.COMMON)?.name
                ?: tuning.toString()
        }
    }

    val strings = remember(tuning) {
        tuning.strings
            .reversed()
            .joinToString(
                separator = ", ",
            ) { it.toFullString() }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            tuningName,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = fontWeight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            strings,
            style = MaterialTheme.typography.body2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// PREVIEWS

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun TunerPreview() {
    AppTheme {
        TunerScreen(
            windowHeightSizeClass = WindowHeightSizeClass.Medium,
            tuning = Tunings.HALF_STEP_DOWN,
            noteOffset = remember { mutableStateOf(1.3)},
            selectedString = 1,
            tuned = BooleanArray(6) { it==4 },
            autoDetect = true,
            favTunings = remember { mutableStateOf(emptySet()) },
            customTunings = remember { mutableStateOf(emptySet()) },
            prefs = TunerPreferences(),
            onSelectString = {},
            onSelectTuning = {},
            onTuneUpString = {},
            onTuneDownString = {},
            onTuneUpTuning = {},
            onTuneDownTuning = {},
            onAutoChanged = {},
            onTuned = {},
            onOpenTuningSelector = {},
            onSettingsPressed = {}
        ) {}
    }
}

@Preview(device = "spec:width=411dp,height=320dp")
@Preview(device = "spec:width=320dp,height=411dp")
@Preview(device = "spec:width=411dp,height=320dp", uiMode = UI_MODE_NIGHT_YES)
@Preview(device = "spec:width=320dp,height=411dp", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun CompactPreview() {
    AppTheme {
        TunerScreen(
            compact = true,
            windowHeightSizeClass = WindowHeightSizeClass.Compact,
            tuning = Tunings.HALF_STEP_DOWN,
            noteOffset = remember { mutableStateOf(1.3)},
            selectedString = 1,
            tuned = BooleanArray(6) { it==4 },
            autoDetect = true,
            favTunings = remember { mutableStateOf(emptySet()) },
            customTunings = remember { mutableStateOf(emptySet()) },
            prefs = TunerPreferences(),
            onSelectString = {},
            onSelectTuning = {},
            onTuneUpString = {},
            onTuneDownString = {},
            onTuneUpTuning = {},
            onTuneDownTuning = {},
            onAutoChanged = {},
            onTuned = {},
            onOpenTuningSelector = {},
            onSettingsPressed = {}
        ) {}
    }
}

@Preview(device = "spec:width=891dp,height=411dp")
@Composable
private fun LandscapePreview() {
    AppTheme {
        TunerScreen(
            windowHeightSizeClass = WindowHeightSizeClass.Compact,
            tuning = Tuning.STANDARD,
            noteOffset = remember { mutableStateOf(1.3)},
            selectedString = 1,
            tuned = BooleanArray(6) { it==4 },
            autoDetect = true,
            favTunings = remember { mutableStateOf(emptySet()) },
            customTunings = remember { mutableStateOf(emptySet()) },
            prefs = TunerPreferences(),
            onSelectString = {},
            onSelectTuning = {},
            onTuneUpString = {},
            onTuneDownString = {},
            onTuneUpTuning = {},
            onTuneDownTuning = {},
            onAutoChanged = {},
            onTuned = {},
            onOpenTuningSelector = {},
            onSettingsPressed = {}
        ) {}
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ListeningPreview() {
    AppTheme { Surface {
        TuningDisplay(noteOffset = remember { mutableStateOf(null)}, TuningDisplayType.SEMITONES) {}
    }}
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun InTunePreview() {
    AppTheme { Surface {
            TuningDisplay(noteOffset = remember { mutableStateOf(0.09)}, TuningDisplayType.SEMITONES) {}
    }}
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun YellowPreview() {
    AppTheme { Surface {
            TuningDisplay(noteOffset = remember { mutableStateOf(2.07)}, TuningDisplayType.SIMPLE) {}
    }}
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun RedPreview() {
    AppTheme { Surface {
            TuningDisplay(noteOffset = remember { mutableStateOf(-27.0)}, TuningDisplayType.CENTS) {}
    }}
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