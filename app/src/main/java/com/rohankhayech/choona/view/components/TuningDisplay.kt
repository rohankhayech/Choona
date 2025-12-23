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

package com.rohankhayech.choona.view.components

import kotlin.math.abs
import kotlin.math.sign
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.Wallpapers.BLUE_DOMINATED_EXAMPLE
import androidx.compose.ui.tooling.preview.Wallpapers.GREEN_DOMINATED_EXAMPLE
import androidx.compose.ui.tooling.preview.Wallpapers.RED_DOMINATED_EXAMPLE
import androidx.compose.ui.tooling.preview.Wallpapers.YELLOW_DOMINATED_EXAMPLE
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.rohankhayech.android.util.ui.preview.LargeFontPreview
import com.rohankhayech.android.util.ui.preview.ThemePreview
import com.rohankhayech.android.util.ui.theme.m3.harmonisedWith
import com.rohankhayech.android.util.ui.theme.m3.isDynamicColor
import com.rohankhayech.choona.lib.R
import com.rohankhayech.choona.lib.controller.tuner.Tuner
import com.rohankhayech.choona.lib.model.preferences.TuningDisplayType
import com.rohankhayech.choona.lib.view.components.animateTuningMeterIndicatorPosition
import com.rohankhayech.choona.lib.view.components.animateTuningMeterIndicatorSize
import com.rohankhayech.choona.lib.view.components.drawMeter
import com.rohankhayech.choona.lib.view.components.isInTune
import com.rohankhayech.choona.lib.view.theme.Green500
import com.rohankhayech.choona.lib.view.theme.Red500
import com.rohankhayech.choona.lib.view.theme.Yellow500
import com.rohankhayech.choona.view.theme.PreviewWrapper
import com.rohankhayech.music.Notes

/**
 * UI component consisting of a visual meter and
 * text label displaying the current tuning [offset][noteOffset].
 *
 * @param noteIndex The index of the currently selected/detected note.
 * @param noteOffset The offset between the currently playing note and the selected string.
 * @param displayType Type of tuning offset value to display.
 * @param showNote Whether to display the note and octave in the label.
 * @param onTuned Called when the detected note is held in tune.
 *
 * @author Rohan Khayech
 */
@Composable
fun TuningDisplay(
    noteIndex: Int,
    noteOffset: State<Double?>,
    displayType: TuningDisplayType,
    showNote: Boolean,
    onTuned: () -> Unit
) {
    val offset = noteOffset.value

    // Calculate meter position.
    val meterPosition by animateTuningMeterIndicatorPosition(offset, showNote)
    val absPosition = abs(meterPosition)

    // Calculate colour of meter and label.
    val color by animateColorAsState(
        targetValue = run {
            val green = Green500
            val yellow = Yellow500
            val red = Red500
            val onBack = MaterialTheme.colorScheme.onBackground
            val back = MaterialTheme.colorScheme.background
            val themeColors = MaterialTheme.colorScheme
            val dynamicColors = MaterialTheme.isDynamicColor

            remember(absPosition, themeColors, dynamicColors) { derivedStateOf {
                (if (absPosition != 0f) {
                    // Gradient from green to red based on offset.
                    if (absPosition < 0.5) {
                        lerp(green, yellow, absPosition * 2f)
                    } else {
                        lerp(yellow, red, (absPosition - 0.5f) * 2f)
                    }
                } else {
                    // Listening color.
                    onBack.copy(alpha = 0.2f).compositeOver(back)
                }).run {
                    if (dynamicColors) harmonisedWith(themeColors)
                    else this
                }
            }}.value
        },
        label = "Tuning Meter Color"
    )

    val inTune = isInTune(offset)

    val indicatorSize by animateTuningMeterIndicatorSize(inTune, onTuned)

    // Content
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AccidentalIcon(R.drawable.music_accidental_flat, contentDescription = "Flat")
        TuningMeter(
            indicatorPosition = meterPosition,
            indicatorSize = indicatorSize,
            color = color
        ) {
            TuningMeterLabel(
                noteOffset = offset, color = color, displayType = displayType,
                noteIndex = noteIndex,
                showNote = showNote
            )
        }
        AccidentalIcon(R.drawable.music_accidental_sharp, contentDescription = "Sharp")
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
    val dirIndicatorPos = when (LocalLayoutDirection.current) {
        LayoutDirection.Ltr -> indicatorPosition
        LayoutDirection.Rtl -> -indicatorPosition
    }

    Column(
        modifier = Modifier
            .defaultMinSize(210.dp, 116.dp)
            .drawBehind {
                drawMeter(
                    indicatorColor = color,
                    indicatorPosition = dirIndicatorPos,
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
 * Displays the detected/selected musical note, including its root and octave.
 *
 * @param noteIndex The index of the note to display.
 * @param color The color of the text.
 */
@Composable
private fun NoteDisplay(noteIndex: Int, color: Color) {
    Row (
        verticalAlignment = Alignment.Bottom
    ) {
        Text( // Root Note
            color = color,
            text = Notes.getRootNote(Notes.getSymbol(noteIndex)),
            style = MaterialTheme.typography.displayMedium
        )
        Text( // Octave
            color = color,
            text = Notes.getOctave(Notes.getSymbol(noteIndex)).toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Label displaying the [note offset][noteOffset] (or the specified [note][noteIndex] if [showNote] is true),
 * and tuning state with the specified [color] and [displayType].
 */
@Composable
private fun TuningMeterLabel(
    noteIndex: Int,
    noteOffset: Double?,
    displayType: TuningDisplayType,
    showNote: Boolean = true,
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
        if (showNote) {
            NoteDisplay(noteIndex = noteIndex, color = color)
        } else {
            Icon(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .size(48.dp),
                tint = color,
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null
            )
        }
        Text(text = stringResource(R.string.in_tune))

    // Out of Tune
    } else {
        val offset = noteOffset * displayType.multiplier
        val dp = if (displayType == TuningDisplayType.SEMITONES) 1 else 0 // decimal places
        val formattedOffset = "%+.${dp}f".format(offset)

        if (showNote) {
            NoteDisplay(noteIndex = noteIndex, color = color)
            Text(text = when (displayType) {
                TuningDisplayType.SIMPLE -> if (noteOffset.sign > 0) stringResource(R.string.tune_down) else stringResource(R.string.tune_up)
                TuningDisplayType.SEMITONES -> "$formattedOffset ${stringResource(R.string.semitones)}"
                TuningDisplayType.CENTS -> "$formattedOffset ${stringResource(R.string.cents)}"
            })
        } else {
            Text( // Offset Value
                color = color,
                text = formattedOffset,
                style = MaterialTheme.typography.displayMedium
            )
            Text(text = when (displayType) {
                TuningDisplayType.SIMPLE -> if (noteOffset.sign > 0) stringResource(R.string.tune_down) else stringResource(R.string.tune_up)
                TuningDisplayType.SEMITONES -> stringResource(R.string.semitones)
                TuningDisplayType.CENTS -> stringResource(R.string.cents)
            })
        }

    }
}

/**
 * Composable displaying an accidental (sharp or flat) icon.
 * @param icon The icon resource.
 * @param contentDescription Description of the icon for accessibility.
 */
@Composable
private fun AccidentalIcon(
    @DrawableRes icon: Int,
    contentDescription: String
) {
    Icon(
        painter = painterResource(icon),
        contentDescription = contentDescription,
        modifier = Modifier.requiredSize(24.dp),
        tint = LocalContentColor.current.copy(alpha = 0.38f)
    )
}

// PREVIEWS

@ThemePreview
@Composable
private fun ListeningPreview() {
    PreviewWrapper {
        TuningDisplay(noteIndex = -29, noteOffset = remember { mutableStateOf(null) }, displayType = TuningDisplayType.SEMITONES, showNote = false) {}
    }
}

@ThemePreview
@Composable
private fun InTunePreview() {
    PreviewWrapper {
        TuningDisplay(noteIndex = -29, noteOffset = remember { mutableDoubleStateOf(0.09) }, displayType = TuningDisplayType.SEMITONES, showNote = false) {}
    }
}

@PreviewDynamicColors
@Preview(name = "Red", wallpaper = RED_DOMINATED_EXAMPLE, uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Blue", wallpaper = BLUE_DOMINATED_EXAMPLE, uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Green", wallpaper = GREEN_DOMINATED_EXAMPLE, uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Yellow", wallpaper = YELLOW_DOMINATED_EXAMPLE, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DynamicInTunePreview() {
    PreviewWrapper(dynamicColor = true) {
        TuningDisplay(noteIndex = -29, noteOffset = remember { mutableDoubleStateOf(0.09) }, displayType = TuningDisplayType.SEMITONES, showNote = false) {}
    }
}

@ThemePreview
@Composable
private fun YellowPreview() {
    PreviewWrapper(dynamicColor = true) {
        TuningDisplay(noteIndex = -29, noteOffset = remember { mutableDoubleStateOf(2.07) }, displayType = TuningDisplayType.SEMITONES, showNote = false) {}
    }
}

@PreviewDynamicColors
@Preview(name = "Red", wallpaper = RED_DOMINATED_EXAMPLE, uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Blue", wallpaper = BLUE_DOMINATED_EXAMPLE, uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Green", wallpaper = GREEN_DOMINATED_EXAMPLE, uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Yellow", wallpaper = YELLOW_DOMINATED_EXAMPLE, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DynamicYellowPreview() {
    PreviewWrapper(dynamicColor = true) {
        TuningDisplay(noteIndex = -29, noteOffset = remember { mutableDoubleStateOf(2.07) }, displayType = TuningDisplayType.SIMPLE, showNote = false) {}
    }
}

@ThemePreview
@Composable
private fun RedPreview() {
    PreviewWrapper {
        TuningDisplay(noteIndex = -29, noteOffset = remember { mutableDoubleStateOf(-27.0) }, displayType = TuningDisplayType.CENTS, showNote = false) {}
    }
}

@PreviewDynamicColors
@Preview(name = "Red", wallpaper = RED_DOMINATED_EXAMPLE, uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Blue", wallpaper = BLUE_DOMINATED_EXAMPLE, uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Green", wallpaper = GREEN_DOMINATED_EXAMPLE, uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Yellow", wallpaper = YELLOW_DOMINATED_EXAMPLE, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DynamicRedPreview() {
    PreviewWrapper(dynamicColor = true) {
        TuningDisplay(noteIndex = -29, noteOffset = remember { mutableDoubleStateOf(-27.0) }, displayType = TuningDisplayType.CENTS, showNote = false) {}
    }
}

@LargeFontPreview
@Composable
private fun LargeFontLabelPreview() {
    PreviewWrapper {
        TuningDisplay(noteIndex = -29, noteOffset = remember { mutableDoubleStateOf(2.7) }, displayType = TuningDisplayType.SIMPLE, showNote = false) {}
    }
}

@LargeFontPreview
@Composable
private fun LargeFontIconPreview() {
    PreviewWrapper {
        TuningDisplay(noteIndex = -29, noteOffset = remember { mutableDoubleStateOf(0.09) }, displayType = TuningDisplayType.SEMITONES, showNote = false) {}
    }
}

@ThemePreview
@Composable
private fun InTuneNotePreview() {
    PreviewWrapper {
        TuningDisplay(noteIndex = -29, noteOffset = remember { mutableDoubleStateOf(0.09) }, displayType = TuningDisplayType.SEMITONES, showNote = true) {}
    }
}

@ThemePreview
@Composable
private fun NoteCentsPreview() {
    PreviewWrapper {
        TuningDisplay(noteIndex = -29, noteOffset = remember { mutableDoubleStateOf(0.3) }, displayType = TuningDisplayType.CENTS, showNote = true) {}
    }
}

@ThemePreview
@Composable
private fun NoteSemitonesPreview() {
    PreviewWrapper {
        TuningDisplay(noteIndex = -29, noteOffset = remember { mutableDoubleStateOf(0.5) }, displayType = TuningDisplayType.SEMITONES, showNote = true) {}
    }
}