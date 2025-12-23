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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TitleCard
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.util.author
import com.rohankhayech.choona.lib.R

/**
 * UI component displaying a list of the app's third party libraries, their authors and licenses.
 * @param listState State of the list component.
 * @param contentPadding The padding to apply around the contents
 */
@Composable
fun LibrariesContainer(
    listState: ScalingLazyListState,
    contentPadding: PaddingValues
) {
    val libs by produceLibraries()
    val uriHandler = LocalUriHandler.current

    ScalingLazyColumn(
        state = listState,
        contentPadding = contentPadding
    ) {
        item { ListHeader {
            Text(stringResource(R.string.third_party_licences))
        }}
        items(libs?.libraries ?: emptyList(), key = { it.uniqueId }) {
            TitleCard(
                title = {
                    Text("${it.name} ${it.artifactVersion ?: ""}")
                },
                time = {
                    Text(it.author)
                },
                subtitle = {
                    Text(it.licenses.joinToString(", ") { l -> l.name })
                },
                onClick = {
                    // Open license on browser or connected phone.
                    it.licenses.first().apply {
                        if (!url.isNullOrBlank()) uriHandler.openUri(url!!)
                    }
                }
            )
        }
    }
}