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

package com.rohankhayech.choona.view.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rohankhayech.choona.R
import com.rohankhayech.music.Instrument

/** @return The localised name of this instrument. */
@Composable
fun Instrument.getLocalisedName(): String {
    return stringResource(when (this) {
        Instrument.GUITAR -> R.string.instr_guitar
        Instrument.BASS -> R.string.instr_bass
        Instrument.UKULELE -> R.string.instr_ukulele
        else -> R.string.instr_other
    })
}