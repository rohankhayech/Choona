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

package com.rohankhayech.choona.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.rohankhayech.android.util.ui.theme.isTrueDark
import com.rohankhayech.android.util.ui.theme.primarySurfaceBackground
import com.rohankhayech.choona.BuildConfig
import com.rohankhayech.choona.R
import com.rohankhayech.choona.view.components.SectionLabel
import com.rohankhayech.choona.view.theme.AppTheme

/**
 * UI screen displaying version, copyright and license information about the app.
 * @param onBackPressed Called when the back navigation button is pressed.
 * @author Rohan Khayech
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AboutScreen(
    onLicencesPressed: () -> Unit,
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${stringResource(R.string.about)} ${stringResource(R.string.app_name)}") },
                backgroundColor = MaterialTheme.colors.primarySurfaceBackground(MaterialTheme.isTrueDark),
                navigationIcon = { 
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.nav_back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Version and Copyright
            SectionLabel(stringResource(R.string.about))
            Text(
                "${stringResource(R.string.app_name)} v${BuildConfig.VERSION_NAME}\n© ${stringResource(R.string.copyright)} 2025 Rohan Khayech",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.body2
            )
            Divider()

            // License
            SectionLabel(stringResource(R.string.licence))
            Text(
                "${stringResource(R.string.app_name)} ${stringResource(R.string.license_desc)}",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.body2,
            )
            Divider()
            LinkListItem(text = stringResource(R.string.licence_terms), url = "https://github.com/rohankhayech/Choona/blob/main/LICENSE")
            LinkListItem(text = stringResource(R.string.source_code), url = "https://github.com/rohankhayech/Choona")

            ListItem(Modifier.clickable(onClick = onLicencesPressed)) {
                Text(stringResource(R.string.third_party_licences))
            }
            Divider()

            SectionLabel(stringResource(R.string.privacy))
            LinkListItem(text = stringResource(R.string.privacy_policy), url = "https://github.com/rohankhayech/Choona/blob/main/PRIVACY.md")

            SectionLabel(stringResource(R.string.help_feedback))
            LinkListItem(text = stringResource(R.string.send_feedback), url = "https://github.com/rohankhayech/Choona/issues/new/choose")
        }
    }
}

/**
 * List item with the specified [text] that directs to the specified [url] when pressed.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun LinkListItem(text: String, url: String) {
    val uriHandler = LocalUriHandler.current
    ListItem(Modifier.clickable(onClick = remember {{ uriHandler.openUri(url) }})) {
        Text(text)
    }
    Divider()
}

/**
 * UI screen showing the licences of the apps dependencies.
 * @param onBackPressed Called when the back navigation button is pressed.
 */
@Composable
fun LicencesScreen(
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.oss_licences)) },
                backgroundColor = MaterialTheme.colors.primarySurfaceBackground(MaterialTheme.isTrueDark),
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.nav_back))
                    }
                }
            )
        }
    ) { padding ->
        LibrariesContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        )
    }
}

/** Preview */
@Preview
@Composable
private fun Preview() {
    AppTheme { AboutScreen({}) {} }
}

/** Preview */
@Preview
@Composable
private fun LicensesPreview() {
    AppTheme { LicencesScreen {} }
}