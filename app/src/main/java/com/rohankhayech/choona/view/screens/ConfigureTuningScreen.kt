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

package com.rohankhayech.choona.view.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rohankhayech.choona.R
import com.rohankhayech.choona.model.tuning.Tunings
import com.rohankhayech.choona.view.components.StringControls
import com.rohankhayech.choona.view.components.TuningSelector
import com.rohankhayech.choona.view.theme.AppTheme
import com.rohankhayech.music.Tuning

/**
 * UI screen used to tune individual strings and the tuning
 * itself up and down, as well as select from favourite tunings.
 *
 * @param tuning Guitar tuning used for comparison.
 * @param favTunings Set of tunings marked as favourite by the user.
 * @param customTunings Set of custom tunings added by the user.
 * @param onSelectTuning Called when a tuning is selected.
 * @param onTuneUpString Called when a string is tuned up.
 * @param onTuneDownString Called when a string is tuned down.
 * @param onTuneUpTuning Called when the tuning is tuned up.
 * @param onTuneDownTuning Called when the tuning is tuned down.
 * @param onOpenTuningSelector Called when the user opens the tuning selector screen.
 * @param onDismiss Called when the screen is dismissed.
 *
 * @author Rohan Khayech
 */
@Composable
fun ConfigureTuningScreen(
    tuning: Tuning,
    favTunings: State<Set<Tuning>>,
    customTunings: State<Set<Tuning>>,
    onSelectTuning: (Tuning) -> Unit,
    onTuneUpString: (Int) -> Unit,
    onTuneDownString: (Int) -> Unit,
    onTuneUpTuning: () -> Unit,
    onTuneDownTuning: () -> Unit,
    onOpenTuningSelector: () -> Unit,
    onDismiss: () -> Unit,
) {
    val scrollState = rememberScrollState()

    val appBarElevation by animateDpAsState(
        remember { derivedStateOf {
            if (scrollState.value == 0) {
                0.dp
            } else AppBarDefaults.TopAppBarElevation
        }}.value,
        label = "App Bar Elevation"
    )

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.configure_tuning))
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, stringResource(R.string.dismiss))
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = appBarElevation
            )
        },
        bottomBar = {
            Column(Modifier.fillMaxWidth()) {
                Divider(thickness = Dp.Hairline)
                BottomAppBar(
                    modifier = Modifier.height(IntrinsicSize.Min),
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.onBackground,
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                        TuningSelector(
                            tuning = tuning,
                            favTunings = favTunings,
                            customTunings = customTunings,
                            onSelect = onSelectTuning,
                            onTuneDown = onTuneDownTuning,
                            onTuneUp = onTuneUpTuning,
                            onOpenTuningSelector = onOpenTuningSelector,
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(8.dp))
            StringControls(
                inline = true,
                tuning = tuning,
                selectedString = null,
                tuned = null,
                onSelect = {},
                onTuneDown = onTuneDownString,
                onTuneUp = onTuneUpString
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Preview(device = "spec:width=411dp,height=320dp")
@Preview(device = "spec:width=320dp,height=411dp")
@Preview(device = "spec:width=411dp,height=320dp", uiMode = UI_MODE_NIGHT_YES)
@Preview(device = "spec:width=320dp,height=411dp", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    AppTheme {
        ConfigureTuningScreen(
            tuning = Tunings.HALF_STEP_DOWN,
            favTunings = remember { mutableStateOf(emptySet()) },
            customTunings = remember { mutableStateOf(emptySet()) },
            onSelectTuning = {},
            onTuneUpString = {},
            onTuneDownString = {},
            onTuneUpTuning = {},
            onTuneDownTuning = {},
            onOpenTuningSelector = {}
        ) {}
    }
}