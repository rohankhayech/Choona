/*
 * Copyright (c) 2023 Rohan Khayech
 */

package com.rohankhayech.choona.view.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** UI component displaying a list section label with [title] text. */
@Composable
fun SectionLabel(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.caption,
        color = MaterialTheme.colors.primaryVariant,
        modifier = modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}