/*
 * Choona - Guitar Tuner
 * Copyright (C) 2026 Rohan Khayech
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

package com.rohankhayech.choona.wear.view.util

import android.app.RemoteInput
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.wearableExtender

/**
 * Remembers a launcher for the Wear OS system text input interface,
 * and registers a callback to handle the result.
 *
 * @param title The title of the text input interface.
 * @param key The key used to retrieve the text input.
 * @param emojisAllowed Whether emojis are allowed in the text input.
 * @param imeActionType Action type to be set on RemoteInput session. Should be one of the
 *   following values: [EditorInfo.IME_ACTION_SEND], [EditorInfo.IME_ACTION_SEARCH],
 *   [EditorInfo.IME_ACTION_DONE], [EditorInfo.IME_ACTION_GO]. If not, send action will be set.
 * @param onInput Called with the text input when the user submits it.
 * @return A function that triggers the text input interface.
 *
 * @author Rohan Khayech
 */
@Composable
fun wearTextInput(
    title: String,
    key: String,
    emojisAllowed: Boolean = true,
    imeActionType: Int = EditorInfo.IME_ACTION_DONE,
    onInput: (String) -> Unit,
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        it.data?.let { data ->
            val results: Bundle? = RemoteInput.getResultsFromIntent(data)
            val text = results?.getCharSequence(key)
            text?.let { text -> onInput(text.toString()) }
        }
    }

    val intent = remember(key, title, emojisAllowed, imeActionType) {
        RemoteInputIntentHelper.createActionRemoteInputIntent().also {
            RemoteInputIntentHelper.putRemoteInputsExtra(it, listOf(
                RemoteInput.Builder(key)
                    .setLabel(title)
                    .wearableExtender {
                        setEmojisAllowed(emojisAllowed)
                        setInputActionType(imeActionType)
                    }.build()
            ))
        }
    }

    return {
        launcher.launch(intent)
    }
}