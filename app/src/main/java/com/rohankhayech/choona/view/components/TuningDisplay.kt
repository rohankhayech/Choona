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

package com.rohankhayech.choona.view.components

import kotlin.math.abs
import kotlin.math.sign
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rohankhayech.choona.R
import com.rohankhayech.choona.controller.tuner.Tuner
import com.rohankhayech.choona.model.preferences.TuningDisplayType
import com.rohankhayech.choona.view.LightDarkPreview
import com.rohankhayech.choona.view.PreviewWrapper
import com.rohankhayech.choona.view.theme.Yellow500

/**
 * UI component consisting of a visual meter and
 * text label displaying the current tuning [offset][noteOffset].
 *
 * @param noteOffset The offset between the currently playing note and the selected string.
 * @param displayType Type of tuning offset value to display.
 * @param onTuned Called when the detected note is held in tune.
 *
 * @author Rohan Khayech
 */
@Composable
fun TuningDisplay(
    noteOffset: State<Double?>,
    displayType: TuningDisplayType,
    onTuned: () -> Unit
) {
    val offset = noteOffset.value

    // Calculate meter position.
    val meterPosition by animateFloatAsState(
        targetValue = remember(offset) { derivedStateOf {
            if (offset != null) {
                (offset.toFloat() / 4f).coerceIn(-1f..1f)
            } else {
                0f
            }
        }
        }.value,
        label = "Tuning Meter Position"
    )
    val absPosition = abs(meterPosition)

    // Calculate colour of meter and label.
    val color by animateColorAsState(
        targetValue = run {
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
            }
            }.value
        },
        label = "Tuning Meter Color"
    )

    val inTune = offset != null && abs(offset) < Tuner.TUNED_OFFSET_THRESHOLD

    val indicatorSize by animateFloatAsState(
        targetValue = if (inTune) 1f else 2/180f,
        animationSpec = if (inTune) tween(Tuner.TUNED_SUSTAIN_TIME-50, 50) else spring(),
        finishedListener = {
            if(it == 1f) { onTuned() }
        },
        label = "Tuning Indicator Size"
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

// PREVIEWS

@LightDarkPreview
@Composable
private fun ListeningPreview() {
    PreviewWrapper {
        TuningDisplay(noteOffset = remember { mutableStateOf(null) }, TuningDisplayType.SEMITONES) {}
    }
}

@LightDarkPreview
@Composable
private fun InTunePreview() {
    PreviewWrapper {
        TuningDisplay(noteOffset = remember { mutableStateOf(0.09) }, TuningDisplayType.SEMITONES) {}
    }
}

@LightDarkPreview
@Composable
private fun YellowPreview() {
    PreviewWrapper {
        TuningDisplay(noteOffset = remember { mutableStateOf(2.07) }, TuningDisplayType.SIMPLE) {}
    }
}

@LightDarkPreview
@Composable
private fun RedPreview() {
    PreviewWrapper {
        TuningDisplay(noteOffset = remember { mutableStateOf(-27.0) }, TuningDisplayType.CENTS) {}
    }
}