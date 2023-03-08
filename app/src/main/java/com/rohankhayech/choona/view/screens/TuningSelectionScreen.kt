/*
 * Copyright (c) 2023 Rohan Khayech
 */

package com.rohankhayech.choona.view.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohankhayech.choona.R
import com.rohankhayech.choona.model.tuning.TuningList
import com.rohankhayech.choona.model.tuning.Tunings
import com.rohankhayech.choona.view.theme.AppTheme
import com.rohankhayech.music.Tuning

/**
 * UI screen that allows the user to select a tuning for use,
 * as well as managing favourite and custom tunings.
 *
 * @param tuningList State holder for the tuning list.
 * @param onSelect Called when a tuning is selected.
 * @param onDismiss Called when the screen is dismissed.
 *
 * @author Rohan Khayech
 */
@Composable
fun TuningSelectionScreen(
    tuningList: TuningList,
    onSelect: (Tuning) -> Unit,
    onDismiss: () -> Unit
) {
    // Collect UI state.
    val current by tuningList.current.collectAsStateWithLifecycle()
    val favourites by tuningList.favourites.collectAsStateWithLifecycle()
    val custom by tuningList.custom.collectAsStateWithLifecycle()

    TuningSelectionScreen(
        current = current,
        common = Tunings.COMMON,
        favourites = favourites,
        custom = custom,
        onSave = tuningList::addCustom,
        onFavouriteSet = tuningList::setFavourited,
        onSelect = onSelect,
        onDelete = tuningList::removeCustom,
        onDismiss = onDismiss
    )
}

/**
 * UI screen that allows the user to select a tuning for use,
 * as well as managing favourite and custom tunings.
 *
 * @param current Currently selected tuning, or null if N/A.
 * @param common Set of commonly used tunings.
 * @param favourites Set of tunings marked as favourites.
 * @param custom Set of custom tunings saved by the user.
 * @param onSave Called when a custom tuning is saved with the specified name.
 * @param onFavouriteSet Called when a tuning is favourited or unfavourited.
 * @param onSelect Called when a tuning is selected.
 * @param onDelete Called when a custom tuning is deleted.
 * @param onDismiss Called when the screen is dismissed.
 *
 * @author Rohan Khayech
 */
@Composable
fun TuningSelectionScreen(
    current: Tuning? = null,
    common: Set<Tuning>,
    favourites: Set<Tuning>,
    custom: Set<Tuning>,
    onSave: (String?, Tuning) -> Unit,
    onFavouriteSet: (Tuning, Boolean) -> Unit,
    onSelect: (Tuning) -> Unit,
    onDelete: (Tuning) -> Unit,
    onDismiss: () -> Unit,
) {
    val listState = rememberLazyListState()

    val appBarElevation by animateDpAsState(
        remember { derivedStateOf {
            if (listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0) {
                0.dp
            } else AppBarDefaults.TopAppBarElevation
        }}.value
    )

    var showSaveDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.select_tuning)) },
                backgroundColor = MaterialTheme.colors.surface,
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, stringResource(R.string.dismiss))
                    }
                },
                elevation = appBarElevation
            )
        }
    ) {
        TuningList(
            modifier = Modifier.padding(it),
            listState = listState,
            current = current,
            common = common,
            favourites = favourites,
            custom = custom,
            onSave = { showSaveDialog = true },
            onFavouriteSet = onFavouriteSet,
            onSelect = onSelect,
            onDelete = onDelete
        )
    }

    // Save dialog.
    if (showSaveDialog) {
        if (current != null) {
            SaveTuningDialog(
                tuning = current,
                onSave = { name, tuning ->
                    onSave(name, tuning)
                    showSaveDialog = false
                },
                onDismiss = {
                    showSaveDialog = false
                }
            )
        }
    }
}

/**
 * UI component displaying the current tuning and a list of favourite, custom and common tunings.
 *
 * @param modifier The modifier to apply to this layout.
 * @param listState State controller for the lazy list.
 * @param current Currently selected tuning, or null if N/A.
 * @param common Set of commonly used tunings.
 * @param favourites Set of tunings marked as favourites.
 * @param custom Set of custom tunings saved by the user.
 * @param onSave Called when a custom tuning is saved with the specified name.
 * @param onFavouriteSet Called when a tuning is favourited or unfavourited.
 * @param onSelect Called when a tuning is selected.
 * @param onDelete Called when a custom tuning is deleted.
 */
@Composable
fun TuningList(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    current: Tuning? = null,
    common: Set<Tuning>,
    favourites: Set<Tuning>,
    custom: Set<Tuning>,
    onSave: (Tuning) -> Unit,
    onFavouriteSet: (Tuning, Boolean) -> Unit,
    onSelect: (Tuning) -> Unit,
    onDelete: (Tuning) -> Unit
) {
    val commonList = remember(common) { common.toList() }
    val favsList = remember(favourites) { favourites.toList() }
    val customList = remember(custom) { custom.toList() }

    LazyColumn(modifier = modifier, state = listState) {

        // Current Tuning
        current?.let {
            item("cur") { SectionLabel(stringResource(R.string.tuning_list_current)) }
            item("cur-${current.name}") {
                val saved = remember(current, custom, common) { current.hasEquivalentIn(custom+common) }
                CurrentTuningItem(tuning = current, saved = saved, onSave = onSave, onSelect = onSelect)
            }
        }

        // Favourite Tunings
        if (favourites.isNotEmpty()) {
            item("favs") { SectionLabel(stringResource(R.string.tuning_list_favourites)) }
            items(favsList, key = { "fav-${it.name}" }) {
                FavouritableTuningItem(tuning = it, favourited = true, onFavouriteSet = onFavouriteSet, onSelect = onSelect)
            }
        }

        // Custom Tunings
        if (custom.isNotEmpty()) {
            item("cus") { SectionLabel(stringResource(R.string.tuning_list_custom)) }
            items(customList, key = { it.name }) {
                val favourited = remember(favourites) { it.hasEquivalentIn(favourites) }
                CustomTuningItem(tuning = it, favourited = favourited, onFavouriteSet = onFavouriteSet, onSelect = onSelect, onDelete = onDelete)
            }
        }

        // Common Tunings
        item("com") { SectionLabel(stringResource(R.string.tuning_list_common)) }
        items(commonList, key = { it.name }) {
            val favourited = remember(favourites) { it.hasEquivalentIn(favourites) }
            FavouritableTuningItem(tuning = it, favourited = favourited, onFavouriteSet = onFavouriteSet, onSelect = onSelect)
        }
    }
}

/**
 * List item displaying the current tuning and an option to save it.
 *
 * @param tuning Currently selected tuning.
 * @param instrumentName Name of the instrument the tuning is for.
 * @param saved Whether the tuning is currently saved.
 * @param onSave Called when the save button is pressed.
 * @param onSelect Called when this tuning is selected.
 */
@Composable
private fun LazyItemScope.CurrentTuningItem(
    tuning: Tuning,
    instrumentName: String = stringResource(R.string.instr_guitar),
    saved: Boolean,
    onSave: (Tuning) -> Unit,
    onSelect: (Tuning) -> Unit,
) {
    TuningItem(
        tuning = tuning,
        instrumentName = instrumentName,
        onSelect = onSelect,
        trailing = if (!saved) { {
            IconButton(
                onClick = { onSave(tuning) }
            ) {
                Icon(
                    Icons.Default.SaveAs,
                    contentDescription = stringResource(R.string.save)
                )
            }
        }} else null
    )
}

/**
 * List item displaying a custom tuning, with options to favourite or remove it.
 *
 * @param tuning The tuning to display.
 * @param instrumentName Name of the instrument the tuning is for.
 * @param favourited Whether the tuning is currently marked as a favourite.
 * @param onFavouriteSet Called when the favourite button is pressed.
 * @param onSelect Called when this tuning is selected.
 * @param onDelete Called when this tuning is swiped to be removed.
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.CustomTuningItem(
    tuning: Tuning,
    instrumentName: String = stringResource(R.string.instr_guitar),
    favourited: Boolean,
    onFavouriteSet: (Tuning, Boolean) -> Unit,
    onSelect: (Tuning) -> Unit,
    onDelete: (Tuning) -> Unit,
) {
     val dismissState = rememberDismissState(
        confirmStateChange = { dismissValue ->
            if (dismissValue == DismissValue.DismissedToStart) {
                onDelete(tuning)
                true
            } else false
        }
    )

    SwipeToDismiss(
        modifier = Modifier.animateItemPlacement(),
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.DismissedToStart -> MaterialTheme.colors.error
                        .copy(alpha = 0.36f)
                        .compositeOver(MaterialTheme.colors.surface)
                    else -> MaterialTheme.colors.onSurface
                        .copy(alpha = 0.05f)
                        .compositeOver(MaterialTheme.colors.surface)
                }
            )

            Row (
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    Icons.Default.DeleteForever,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colors.onSurface
                )
            }
        }
    ) {
        Surface(
            color = MaterialTheme.colors.background
        ) {
            Column {
                FavouritableTuningItem(
                    tuning = tuning,
                    instrumentName = instrumentName,
                    favourited = favourited,
                    onFavouriteSet = onFavouriteSet,
                    onSelect = onSelect
                )
            }
        }
    }
}

/**
 * List item displaying a tuning, with an option to favourite it.
 *
 * @param tuning The tuning to display.
 * @param instrumentName Name of the instrument the tuning is for.
 * @param favourited Whether the tuning is currently marked as a favourite.
 * @param onFavouriteSet Called when the favourite button is pressed.
 * @param onSelect Called when this tuning is selected.
 */
@Composable
private fun LazyItemScope.FavouritableTuningItem(
    tuning: Tuning,
    instrumentName: String = stringResource(R.string.instr_guitar),
    favourited: Boolean,
    onFavouriteSet: (Tuning, Boolean) -> Unit,
    onSelect: (Tuning) -> Unit,
) {
    TuningItem(tuning = tuning, instrumentName = instrumentName, onSelect = onSelect) {
        IconToggleButton(
            checked = favourited,
            onCheckedChange = { onFavouriteSet(tuning, !favourited) }
        ) {
            Icon(
                if (favourited) Icons.Default.Star else Icons.Default.StarOutline,
                tint = if (favourited) MaterialTheme.colors.secondary else LocalContentColor.current,
                contentDescription = if (favourited) stringResource(R.string.unfavourite) else stringResource(R.string.favourite)
            )
        }
    }
}

/**
 * List item displaying a custom tuning, with support for a trailing action.
 *
 * @param tuning The tuning to display.
 * @param instrumentName Name of the instrument the tuning is for.
 * @param onSelect Called when this tuning is selected.
 * @param trailing The trailing action to display.
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.TuningItem(
    tuning: Tuning,
    instrumentName: String = stringResource(R.string.instr_guitar),
    onSelect: (Tuning) -> Unit,
    trailing: (@Composable () -> Unit)? = null,

) {
    val strings = remember(tuning) {
        tuning.strings
            .reversed()
            .joinToString(
                separator = ", ",
            ) { it.toFullString() }
    }

    Column(Modifier.animateItemPlacement()) {
        ListItem(
            text = { Text(tuning.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            secondaryText = { Text(strings) },
            overlineText = { Text("$instrumentName ‧ ${tuning.numStrings()}"+stringResource(R.string.num_strings_suffix)) },
            modifier = Modifier.clickable { onSelect(tuning) },
            trailing = trailing
        )

        Divider()
    }
}

/** UI component displaying a tuning category label with [title] text. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.SectionLabel(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.caption,
        color = MaterialTheme.colors.primaryVariant,
        modifier = Modifier
            .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            .animateItemPlacement()
    )
}

/**
 * Dialog allowing the user to enter a name and save the specified tuning.
 *
 * @param tuning The tuning to save.
 * @param onSave Called when save button is pressed. Provides the saved tuning and the entered name.
 * @param onDismiss Called when the save dialog is dismissed.
 */
@Composable
private fun SaveTuningDialog(
    tuning: Tuning,
    onSave: (String?, Tuning) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        text = {
            Column {
                Text(
                    stringResource(R.string.dialog_title_save_tuning),
                    style = MaterialTheme.typography.subtitle1,
                    color = LocalContentColor.current.copy(alpha = ContentAlpha.high)
                )
                Spacer(Modifier.height(16.dp))
                TextField(
                    value = name,
                    placeholder = { Text(tuning.toString()) },
                    singleLine = true,
                    onValueChange = { name = it }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(name.ifBlank { null }, tuning) }) {
                Text(text = stringResource(R.string.save).uppercase())
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel).uppercase())
            }
        },
        onDismissRequest = onDismiss,
    )
}

// Previews

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    val currentTuning = Tuning.fromString("G3 D3 A3 E4")
    val customTuning = Tuning.fromString("E4 E3 E3 E3 E2 E2")
    val favCustomTuning = Tuning.fromString("Custom", "C#4 B3 F#3 D3 A2 D2")

    AppTheme {
        Surface {
            TuningSelectionScreen(
                current = currentTuning,
                common = Tunings.COMMON,
                favourites = setOf(Tuning.STANDARD, favCustomTuning),
                custom = setOf(customTuning, favCustomTuning),
                onSave = {_,_->},
                onFavouriteSet = {_,_ ->},
                onSelect = {},
                onDelete = {},
                onDismiss = {}
            )
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SaveDialogPreview() {
    val customTuning = Tuning.fromString("Custom", "C#4 B3 F#3 D3 A2 D2")

    AppTheme {
        SaveTuningDialog(tuning = customTuning, onSave = {_,_->}) {}
    }
}