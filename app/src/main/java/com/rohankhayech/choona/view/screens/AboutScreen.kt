/*
 * Copyright (c) 2023 Rohan Khayech
 */

package com.rohankhayech.choona.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
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
import com.rohankhayech.choona.BuildConfig
import com.rohankhayech.choona.R
import com.rohankhayech.choona.view.components.SectionLabel
import com.rohankhayech.choona.view.theme.AppTheme

/**
 * UI screen displaying version, copyright and license information about the app.
 * @param fullBlack Whether the app is in full black mode.
 * @param onBackPressed Called when the back navigation button is pressed.
 * @author Rohan Khayech
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AboutScreen(
    fullBlack: Boolean,
    onLicencesPressed: () -> Unit,
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${stringResource(R.string.about)} ${stringResource(R.string.app_name)}") },
                backgroundColor = if (fullBlack && !MaterialTheme.colors.isLight) MaterialTheme.colors.background
                    else MaterialTheme.colors.primarySurface,
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
                "${stringResource(R.string.app_name)} v${BuildConfig.VERSION_NAME}\n© ${stringResource(R.string.copyright)} 2023 Rohan Khayech",
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
 * @param fullBlack Whether the app is in full black mode.
 * @param onBackPressed Called when the back navigation button is pressed.
 */
@Composable
fun LicencesScreen(
    fullBlack: Boolean,
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.oss_licences)) },
                backgroundColor = if (fullBlack && !MaterialTheme.colors.isLight) MaterialTheme.colors.background
                    else MaterialTheme.colors.primarySurface,
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
    AppTheme { AboutScreen(false, {}) {} }
}

/** Preview */
@Preview
@Composable
private fun LicensesPreview() {
    AppTheme { LicencesScreen(false) {} }
}