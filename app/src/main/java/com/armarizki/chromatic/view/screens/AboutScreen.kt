/*
 * ChromaticTuner - Arma Rizki
 *
 * Includes modified source code from Choona Guitar Tuner
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

package com.armarizki.chromatic.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.android.rememberLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.rohankhayech.android.util.ui.theme.m3.isLight
import com.rohankhayech.android.util.ui.theme.m3.isTrueDark
import com.armarizki.chromatic.R
import com.armarizki.chromatic.BuildConfig
import com.armarizki.chromatic.model.preferences.TunerPreferences
import com.armarizki.chromatic.model.preferences.TunerPreferences.Companion.REVIEW_PROMPT_ATTEMPTS
import com.armarizki.chromatic.view.components.SectionLabel
import com.armarizki.chromatic.view.theme.AppTheme
import kotlinx.coroutines.launch

const val ContentAlphaMedium = 0.60f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    prefs: TunerPreferences,
    onLicencesPressed: () -> Unit,
    onBackPressed: () -> Unit,
    onReviewOptOut: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHost = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("${stringResource(R.string.about)} ${stringResource(R.string.app_name)}") },
                navigationIcon = { 
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.nav_back))
                    }
                },
                colors = if (!MaterialTheme.isLight && MaterialTheme.isTrueDark) {
                    TopAppBarDefaults.topAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.background)
                } else {
                    TopAppBarDefaults.topAppBarColors()
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHost)
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                .verticalScroll(rememberScrollState())
        ) {

            SectionLabel(stringResource(R.string.about))

            Text(
                "${stringResource(R.string.app_name)} v${BuildConfig.VERSION_NAME}",
                modifier = Modifier.padding(top = 16.dp).padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                "by Arma Rizki",
                modifier = Modifier.padding(bottom = 16.dp).padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            HorizontalDivider()

            SectionLabel("Copyright Notice")

            Text(
                "Modified copy of Choona - Guitar Tuner v1.5.2",
                modifier = Modifier.padding(bottom = 16.dp).padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                "© ${stringResource(R.string.copyright)} 2025 Rohan Khayech",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                "Choona is licensed under the GNU General Public License v3.0.",
                modifier = Modifier.padding(bottom = 16.dp).padding(horizontal = 16.dp),
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
            LinkListItem(text = stringResource(R.string.licence_terms), url = "https://github.com/ArmaRizki/ChromaticTuner/blob/main/LICENSE")
            LinkListItem(text = stringResource(R.string.source_code), url = "https://github.com/ArmaRizki/ChromaticTuner")

            ListItem(modifier = Modifier.clickable(onClick = onLicencesPressed), headlineContent = {
                Text(stringResource(R.string.third_party_licences))
            })
            HorizontalDivider()

            SectionLabel(stringResource(R.string.privacy))
            LinkListItem(text = stringResource(R.string.privacy_policy), url = "https://github.com/ArmaRizki/ChromaticTuner/blob/main/PRIVACY.md")
        }
    }
}

@Composable
private fun LinkListItem(text: String, url: String) {
    val uriHandler = LocalUriHandler.current
    ListItem(modifier = Modifier.clickable(onClick = remember {{ uriHandler.openUri(url) }}), headlineContent =  {
        Text(text)
    })
    HorizontalDivider()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicencesScreen(
    onBackPressed: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.oss_licences)) },
                colors = if (!MaterialTheme.isLight && MaterialTheme.isTrueDark) {
                    TopAppBarDefaults.topAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.background)
                } else {
                    TopAppBarDefaults.topAppBarColors()
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.nav_back))
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        val libs by rememberLibraries()
        LibrariesContainer(
            libraries = libs,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        )
    }
}
