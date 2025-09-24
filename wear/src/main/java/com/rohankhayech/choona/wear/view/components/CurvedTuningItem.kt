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

package com.rohankhayech.choona.wear.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.CurvedAlignment
import androidx.wear.compose.foundation.CurvedDirection
import androidx.wear.compose.foundation.CurvedLayout
import androidx.wear.compose.foundation.CurvedModifier
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.foundation.curvedBox
import androidx.wear.compose.foundation.curvedColumn
import androidx.wear.compose.foundation.curvedRow
import androidx.wear.compose.foundation.size
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.curvedText
import com.rohankhayech.choona.R
import com.rohankhayech.choona.model.tuning.TuningEntry

/**
 * UI component displaying the name and strings of the specified tuning.
 *
 * @param modifier The modifier to apply to this layout.
 * @param tuning The tuning to display.
 * @param fontWeight The font weight of the tuning name text.
 * @param getCanonicalName Gets the name of the tuning if it is saved as a custom tuning.
 *
 * @author Rohan Khayech
 */
@Composable
fun CurvedTuningItem(
    modifier: Modifier = Modifier,
    tuning: TuningEntry,
    fontWeight: FontWeight = FontWeight.Normal,
    getCanonicalName: (TuningEntry.InstrumentTuning) -> String,
) {
    if (LocalConfiguration.current.isScreenRound) {
        val tuningName = when (tuning) {
            is TuningEntry.ChromaticTuning -> stringResource(R.string.chromatic)
            is TuningEntry.InstrumentTuning ->
                if (tuning.tuning.hasName()) {
                    tuning.tuning.name
                } else {
                    getCanonicalName(tuning)
                }
        }

        val strings = remember(tuning) {
            tuning.tuning?.strings
                ?.reversed()
                ?.joinToString("") { it.toString() }
        } ?: ""

        val desc = when (tuning) {
            is TuningEntry.ChromaticTuning -> stringResource(R.string.chromatic_desc)
            is TuningEntry.InstrumentTuning -> strings
        }

        val titleStyle = MaterialTheme.typography.titleSmall
        val descStyle = MaterialTheme.typography.bodySmall

        CurvedLayout(
            modifier,
            anchor = 90f,
            radialAlignment = CurvedAlignment.Radial.Outer,
            angularDirection = CurvedDirection.Angular.Reversed
        ) {
            curvedColumn(angularAlignment = CurvedAlignment.Angular.Center) {
                curvedRow {
                    curvedText(
                        tuningName,
                        style = CurvedTextStyle(titleStyle),
                        fontWeight = fontWeight,
                        overflow = TextOverflow.Ellipsis
                    )
                    curvedBox(CurvedModifier.size(5.0f, 0.dp)) {}
                    curvedText(
                        desc,
                        style = CurvedTextStyle(descStyle),
                        overflow = TextOverflow.Ellipsis
                    )
                    curvedBox(CurvedModifier.size(5.0f, 0.dp)) {}
                }
            }
        }
    } else {
        TuningItem(
            modifier,
            tuning,
            fontWeight,
            getCanonicalName
        )
    }
}

/**
 * UI component displaying the name and strings of the specified tuning.
 *
 * @param modifier The modifier to apply to this layout.
 * @param tuning The tuning to display.
 * @param fontWeight The font weight of the tuning name text.
 * @param getCanonicalName Gets the name of the tuning if it is saved as a custom tuning.
 *
 * @author Rohan Khayech
 */
@Composable
fun TuningItem(
    modifier: Modifier = Modifier,
    tuning: TuningEntry,
    fontWeight: FontWeight = FontWeight.Normal,
    getCanonicalName: (TuningEntry.InstrumentTuning) -> String,
) {
    val tuningName = when (tuning) {
        is TuningEntry.ChromaticTuning -> stringResource(R.string.chromatic)
        is TuningEntry.InstrumentTuning ->
            if (tuning.tuning.hasName()) {
                tuning.tuning.name
            } else {
                getCanonicalName(tuning)
            }
    }

    val strings = remember(tuning) {
        tuning.tuning?.strings
            ?.reversed()
            ?.joinToString("") { it.toString() }
    } ?: ""

    val desc = when (tuning) {
        is TuningEntry.ChromaticTuning -> stringResource(R.string.chromatic_desc)
        is TuningEntry.InstrumentTuning -> strings
    }

    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, alignment = Alignment.CenterHorizontally),
    ) {
        Text(
            tuningName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = fontWeight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            desc,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * UI component displaying the name and strings of the specified tuning.
 *
 * @param modifier The modifier to apply to this layout.
 * @param tuning The tuning to display.
 * @param fontWeight The font weight of the tuning name text.
 * @param getCanonicalName Gets the name of the tuning if it is saved as a custom tuning.
 *
 * @author Rohan Khayech
 */
@Composable
fun VerticalTuningItem(
    modifier: Modifier = Modifier,
    tuning: TuningEntry,
    fontWeight: FontWeight = FontWeight.Normal,
    getCanonicalName: (TuningEntry.InstrumentTuning) -> String,
) {
    val tuningName = when (tuning) {
        is TuningEntry.ChromaticTuning -> stringResource(R.string.chromatic)
        is TuningEntry.InstrumentTuning ->
            if (tuning.tuning.hasName()) {
                tuning.tuning.name
            } else {
                getCanonicalName(tuning)
            }
    }

    val strings = remember(tuning) {
        tuning.tuning?.strings
            ?.reversed()
            ?.joinToString("") { it.toString() }
    } ?: ""

    val desc = when (tuning) {
        is TuningEntry.ChromaticTuning -> stringResource(R.string.chromatic_desc)
        is TuningEntry.InstrumentTuning -> strings
    }

    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            tuningName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = fontWeight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            desc,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
