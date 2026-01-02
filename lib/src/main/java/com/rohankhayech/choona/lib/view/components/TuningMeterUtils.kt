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

package com.rohankhayech.choona.lib.view.components

import kotlin.math.abs
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.rohankhayech.choona.lib.controller.tuner.Tuner
import com.rohankhayech.choona.lib.view.theme.Green500
import com.rohankhayech.choona.lib.view.theme.Red500
import com.rohankhayech.choona.lib.view.theme.Yellow500

/**
 * Shared draw and animation methods for the Tuning Meter component.
 * @author Rohan Khayech
 */
object TuningMeterUtils {
    private const val SLIDER_INACTIVE_TRACK_ALPHA = 0.24f

    /**
     * Draws a circular meter with a variable-size indicator and background track.
     *
     * @param indicatorColor Color of the indicator.
     * @param trackColor Color of the background track. Defaults to a faded copy of the indicator color.
     * @param indicatorPosition Position of the indicator on the track, as a percentage value from -1.0 (leftmost) to 1.0 (rightmost).
     * @param indicatorSize Size of the indicator as a percentage value from 0.0 (no width) to 1.0 (full width of meter).
     */
    fun DrawScope.drawMeter(
        indicatorColor: Color,
        trackColor: Color = indicatorColor.copy(alpha = SLIDER_INACTIVE_TRACK_ALPHA),
        indicatorPosition: Float,
        indicatorSize: Float,
    ) {
        // Arc size
        val strokeWidth = 20.dp.toPx()
        val arcSize = size.copy(height = size.height * 2 - strokeWidth * 2, width = size.width - strokeWidth)
        val offset = Offset(strokeWidth / 2, strokeWidth / 2)

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
        val indicatorAngle = indicatorPosition * (90f - (indicatorSpan / 2)) - (indicatorSpan / 2)
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
     * Animates the position of the tuning meter indicator on the track.
     * @param offset Offset, in semitones,  between the currently playing note and the selected string/note.
     * @param showNote Whether to display the note in the label.
     */
    @Composable
    fun animateTuningMeterIndicatorPosition(offset: Double?, showNote: Boolean): State<Float> {
        return animateFloatAsState(
            targetValue = remember(offset, showNote) {
                derivedStateOf {
                    val semitoneRange = if (showNote) 0.5 else 4
                    if (offset != null) {
                        (offset.toFloat() / semitoneRange.toFloat()).coerceIn(-1f..1f)
                    } else {
                        0f
                    }
                }
            }.value,
            label = "Tuning Meter Position"
        )
    }

    /**
     * Animates the size of the tuning meter indicator.
     * @param inTune Whether the currently playing note is in tune.
     * @param onTuned Called when the currently playing note is held in tune.
     */
    @Composable
    fun animateTuningMeterIndicatorWidth(inTune: Boolean, onTuned: () -> Unit): State<Float> {
        return animateFloatAsState(
            targetValue = if (inTune) 1f else 2 / 180f,
            animationSpec = if (inTune) tween(Tuner.TUNED_SUSTAIN_TIME - 50, 50) else spring(),
            finishedListener = {
                if (it == 1f) {
                    onTuned()
                }
            },
            label = "Tuning Indicator Width"
        )
    }

    /**
     * Animates the color of the tuning meter indicator.
     * @param meterPosition Position of the indicator on the track, as a percentage value from -1.0 (leftmost) to 1.0 (rightmost).
     * @param onBack The theme's onBackground color.
     * @param back The theme's background color.
     * @param dynamicColors Whether to use dynamic colors.
     * @param harmonisedWith Function used to harmonise the resulting color with a dynamic color scheme.
     */
    @Composable
    fun animateTuningMeterColor(
        meterPosition: Float,
        onBack: Color,
        back: Color,
        dynamicColors: Boolean,
        harmonisedWith: Color.() -> Color
    ): State<Color> {
        val absPosition = abs(meterPosition)

        return animateColorAsState(
            targetValue = run {
                val green = Green500
                val yellow = Yellow500
                val red = Red500

                remember(absPosition, dynamicColors, harmonisedWith) {
                    derivedStateOf {
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
                            if (dynamicColors) harmonisedWith()
                            else this
                        }
                    }
                }.value
            },
            label = "Tuning Meter Color"
        )
    }

    /**
     * Checks if the currently playing note is considered in tune.
     * @param offset Offset, in semitones, between the currently playing note and the selected string/note.
     */
    fun isInTune(offset: Double?): Boolean {
        return offset != null && abs(offset) < Tuner.TUNED_OFFSET_THRESHOLD
    }
}