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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.rohankhayech.choona.BuildConfig
import com.rohankhayech.choona.R
import com.rohankhayech.choona.view.components.SectionLabel
import com.rohankhayech.choona.view.theme.AppTheme

/**
 * UI screen displaying version, copyright and license information about the app.
 * @param onBackPressed Called when the back navigation button is pressed.
 * @author Rohan Khayech
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onLicencesPressed: () -> Unit,
    onBackPressed: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("${stringResource(R.string.about)} ${stringResource(R.string.app_name)}") },
                navigationIcon = { 
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.nav_back))
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
        ) {
            // Version and Copyright
            SectionLabel(stringResource(R.string.about))
            Text(
                "${stringResource(R.string.app_name)} v${BuildConfig.VERSION_NAME}\n© ${stringResource(R.string.copyright)} 2025 Rohan Khayech",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            HorizontalDivider()

            // License
            SectionLabel(stringResource(R.string.licence))
            Text(
                "${stringResource(R.string.app_name)} ${stringResource(R.string.license_desc)}",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
            HorizontalDivider()
            LinkListItem(text = stringResource(R.string.licence_terms), url = "https://github.com/rohankhayech/Choona/blob/main/LICENSE")
            LinkListItem(text = stringResource(R.string.source_code), url = "https://github.com/rohankhayech/Choona")

            ListItem(modifier = Modifier.clickable(onClick = onLicencesPressed), headlineContent = {
                Text(stringResource(R.string.third_party_licences))
            })
            HorizontalDivider()

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
@Composable
private fun LinkListItem(text: String, url: String) {
    val uriHandler = LocalUriHandler.current
    ListItem(modifier = Modifier.clickable(onClick = remember {{ uriHandler.openUri(url) }}), headlineContent =  {
        Text(text)
    })
    HorizontalDivider()
}

/**
 * UI screen showing the licences of the apps dependencies.
 * @param onBackPressed Called when the back navigation button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicencesScreen(
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.oss_licences)) },
                // backgroundColor = MaterialTheme.colors.primarySurfaceBackground(MaterialTheme.isTrueDark),
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.nav_back))
                    }
                }
            )
        }
    ) { padding ->
        LibrariesContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
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