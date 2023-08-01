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

package com.rohankhayech.choona.view

import android.content.res.Configuration
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rohankhayech.choona.view.theme.AppTheme

@Preview(group = "Light")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, group = "Dark")
annotation class LightDarkPreview

@Composable
fun PreviewWrapper(content: @Composable () -> Unit) {
    AppTheme {
        Surface(content = content)
    }
}