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

package com.rohankhayech.choona.wear.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.rohankhayech.choona.R
import com.rohankhayech.choona.wear.view.theme.AppTheme

@Composable
fun PermissionScreen(
    canRequest: Boolean,
    onRequestPermission: () -> Unit,
    onOpenPermissionSettings: () -> Unit
) {
    val scrollState = rememberScrollState()

    ScreenScaffold(
        scrollState = scrollState,
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(scrollState)
                .padding(contentPadding)
                .padding(16.dp)
            ,
            verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterVertically),
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
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                // Rationale
                text = rationale,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )
            // Action Button
            Button(modifier = Modifier.padding(top = 8.dp), onClick = buttonAction) {
                Text(buttonLabel, textAlign = TextAlign.Center)
            }
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Preview(device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Preview(device = WearDevices.SQUARE, showSystemUi = true)
@Composable
private fun Preview() {
    AppTheme {
        AppScaffold {
            PermissionScreen(
                canRequest = true,
                onRequestPermission = {},
                onOpenPermissionSettings = {}
            )
        }
    }
}
