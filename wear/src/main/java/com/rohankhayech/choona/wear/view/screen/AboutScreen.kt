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

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.rohankhayech.choona.lib.R
import com.rohankhayech.choona.wear.BuildConfig
import com.rohankhayech.choona.wear.view.components.LibrariesContainer
import com.rohankhayech.choona.wear.view.components.SectionLabel
import com.rohankhayech.choona.wear.view.theme.AppTheme

@Composable
fun AboutScreen(
    onLicencesPressed: () -> Unit
) {
    val listState = rememberScalingLazyListState()

    ScreenScaffold(
        scrollState = listState
    ) { padding ->
        ScalingLazyColumn(
            state = listState,
            contentPadding = padding
        ) {
            // Version and Copyright
            item {
                ListHeader {
                    Text(
                        "${stringResource(R.string.about)}\n${stringResource(R.string.app_name)}",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
            item {
                Text(
                    "v${BuildConfig.VERSION_NAME}",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
            item {
                Text(
                    "Wear Build",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
            item {
                Text(
                    "© ${stringResource(R.string.copyright)} 2025\nRohan Khayech",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            // License
            item {
                SectionLabel(stringResource(R.string.licence))
            }
            item {
                Text(
                    "${stringResource(R.string.app_name)} ${stringResource(R.string.license_desc)}",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            item {
                LinkButton(text = stringResource(R.string.licence_terms), url = "https://github.com/rohankhayech/Choona/blob/main/LICENSE")
            }
            item {
                LinkButton(text = stringResource(R.string.source_code), url = "https://github.com/rohankhayech/Choona") }

            item {
                Button(
                    label = {
                        Text(stringResource(R.string.third_party_licences))
                    },
                    onClick = onLicencesPressed,
                )
            }
            item {SectionLabel(stringResource(R.string.privacy))}
            item {LinkButton(text = stringResource(R.string.privacy_policy), url = "https://github.com/rohankhayech/Choona/blob/main/PRIVACY.md")}

            item {SectionLabel(stringResource(R.string.help_feedback))}
            item {LinkButton(text = stringResource(R.string.send_feedback), url = "https://github.com/rohankhayech/Choona/issues/new/choose")}
        }
    }
}

/**
 * List item with the specified [text] that directs to the specified [url] when pressed.
 */
@Composable
private fun LinkButton(text: String, url: String) {
    val uriHandler = LocalUriHandler.current
    Button(
        label = {
            Text(text)
        },
        onClick = remember {{ uriHandler.openUri(url) }},
    )
}


@Composable
fun LicencesScreen(
) {
    val listState = rememberScalingLazyListState(
        initialCenterItemIndex = 0
    )

    ScreenScaffold(
        scrollState = listState
    ) { padding ->
        LibrariesContainer(
            listState,
            contentPadding = padding
        )
    }
}

/** Preview */
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Preview(device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Preview(device = WearDevices.SQUARE, showSystemUi = true)
@Composable
private fun Preview() {
    AppTheme { AboutScreen {} }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Preview(device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Preview(device = WearDevices.SQUARE, showSystemUi = true)
@Composable
private fun LicensesPreview() {
    AppTheme { LicencesScreen() }
}