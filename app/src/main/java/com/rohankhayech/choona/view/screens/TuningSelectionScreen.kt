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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohankhayech.choona.R
import com.rohankhayech.choona.model.tuning.TuningList
import com.rohankhayech.choona.model.tuning.Tunings
import com.rohankhayech.choona.model.tuning.groupAndSort
import com.rohankhayech.choona.view.components.SectionLabel
import com.rohankhayech.choona.view.theme.AppTheme
import com.rohankhayech.choona.view.theme.secondaryTextButtonColors
import com.rohankhayech.music.Instrument
import com.rohankhayech.music.Tuning
import com.rohankhayech.music.Tuning.Category

/**
 * UI screen that allows the user to select a tuning for use,
 * as well as managing favourite and custom tunings.
 *
 * @param tuningList State holder for the tuning list.
 * @param backIcon Icon used for the back navigation button.
 * @param onSave Called when a custom tuning is saved with the specified name.
 * @param onFavouriteSet Called when a tuning is favourited or unfavourited.
 * @param onSelect Called when a tuning is selected.
 * @param onDismiss Called when the screen is dismissed.
 *
 * @author Rohan Khayech
 */
@Composable
fun TuningSelectionScreen(
    tuningList: TuningList,
    backIcon: ImageVector?,
    onSave: (String?, Tuning) -> Unit = {_,_->},
    onFavouriteSet: (Tuning, Boolean) -> Unit = {_,_->},
    onSelect: (Tuning) -> Unit,
    onDismiss: () -> Unit
) {
    // Collect UI state.
    val current by tuningList.current.collectAsStateWithLifecycle()
    val favourites by tuningList.favourites.collectAsStateWithLifecycle()
    val custom by tuningList.custom.collectAsStateWithLifecycle()
    val tunings by tuningList.filteredTunings.collectAsStateWithLifecycle()
    val instrumentFilter by tuningList.instrumentFilter.collectAsStateWithLifecycle()
    val categoryFilter by tuningList.categoryFilter.collectAsStateWithLifecycle()
    val instrumentFilters = tuningList.instrumentFilters.collectAsStateWithLifecycle()
    val categoryFilters = tuningList.categoryFilters.collectAsStateWithLifecycle()

    TuningSelectionScreen(
        current = current,
        tunings = tunings,
        favourites = favourites,
        custom = custom,
        instrumentFilter = instrumentFilter,
        categoryFilter = categoryFilter,
        instrumentFilters = instrumentFilters,
        categoryFilters = categoryFilters,
        backIcon = backIcon,
        onSelectInstrument = {
            tuningList.filterBy(instrument = it)
        },
        onSelectCategory = {
            tuningList.filterBy(category = it)
        },
        onSave = { name, tuning ->
            tuningList.addCustom(name, tuning)
            onSave(name, tuning)
        },
        onFavouriteSet = remember (tuningList, onFavouriteSet) {{ tuning, fav ->
            tuningList.setFavourited(tuning, fav)
            onFavouriteSet(tuning, fav)
        }},
        onSelect = onSelect,
        onDelete = {
            tuningList.removeCustom(it)
        },
        onDismiss = onDismiss
    )
}

/**
 * UI screen that allows the user to select a tuning for use,
 * as well as managing favourite and custom tunings.
 *
 * @param current Currently selected tuning, or null if N/A.
 * @param tunings Current collection of filtered and grouped tunings.
 * @param favourites Set of tunings marked as favourites.
 * @param custom Set of custom tunings saved by the user.
 * @param instrumentFilter Current filter for tuning instrument.
 * @param categoryFilter Current filter for tuning category.
 * @param instrumentFilters Available instrument filters and their enabled states.
 * @param categoryFilters Available category filters and their enabled states.
 * @param backIcon Icon used for the back navigation button.
 * @param onSelectInstrument Called when an instrument filter is selected.
 * @param onSelectCategory Called when an category filter is selected.
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
    tunings: Map<Pair<Instrument, Category?>, List<Tuning>>,
    favourites: Set<Tuning>,
    custom: Set<Tuning>,
    instrumentFilter: Instrument?,
    categoryFilter: Category?,
    instrumentFilters: State<Map<Instrument, Boolean>>,
    categoryFilters: State<Map<Category, Boolean>>,
    backIcon: ImageVector?,
    onSelectInstrument: (Instrument?) -> Unit,
    onSelectCategory: (Category?) -> Unit,
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
        }}.value,
        label = "App Bar Elevation"
    )

    var showSaveDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.select_tuning)) },
                backgroundColor = MaterialTheme.colors.background,
                navigationIcon = backIcon?.let {{
                    IconButton(onClick = onDismiss) {
                        Icon(it, stringResource(R.string.dismiss))
                    }
                }},
                elevation = appBarElevation
            )
        }
    ) {
        TuningList(
            modifier = Modifier.padding(it),
            listState = listState,
            current = current,
            tunings = tunings,
            favourites = favourites,
            custom = custom,
            instrumentFilter = instrumentFilter,
            categoryFilter = categoryFilter,
            instrumentFilters = instrumentFilters,
            categoryFilters = categoryFilters,
            onSelectInstrument = onSelectInstrument,
            onSelectCategory = onSelectCategory,
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
 * @param tunings Current collection of filtered and grouped tunings.
 * @param favourites Set of tunings marked as favourites.
 * @param custom Set of custom tunings saved by the user.
 * @param instrumentFilter Current filter for tuning instrument.
 * @param categoryFilter Current filter for tuning category.
 * @param instrumentFilters Available instrument filters and their enabled states.
 * @param categoryFilters Available category filters and their enabled states.
 * @param onSelectInstrument Called when an instrument filter is selected.
 * @param onSelectCategory Called when an category filter is selected.
 * @param onSave Called when a custom tuning is saved with the specified name.
 * @param onFavouriteSet Called when a tuning is favourited or unfavourited.
 * @param onSelect Called when a tuning is selected.
 * @param onDelete Called when a custom tuning is deleted.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TuningList(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    current: Tuning? = null,
    tunings: Map<Pair<Instrument, Category?>, List<Tuning>>,
    favourites: Set<Tuning>,
    custom: Set<Tuning>,
    instrumentFilter: Instrument?,
    categoryFilter: Category?,
    instrumentFilters: State<Map<Instrument, Boolean>>,
    categoryFilters: State<Map<Category, Boolean>>,
    onSelectInstrument: (Instrument?) -> Unit,
    onSelectCategory: (Category?) -> Unit,
    onSave: (Tuning) -> Unit,
    onFavouriteSet: (Tuning, Boolean) -> Unit,
    onSelect: (Tuning) -> Unit,
    onDelete: (Tuning) -> Unit
) {
    val favsList = remember(favourites) { favourites.toList() }
    val customList = remember(custom) { custom.toList() }

    LazyColumn(modifier = modifier, state = listState) {

        // Current Tuning
        current?.let {
            item("cur") { SectionLabel(stringResource(R.string.tuning_list_current)) }
            item("cur-${current.instrument}-[${current.toFullString()}]") {
                val saved = remember(current, custom) { current.hasEquivalentIn(custom+Tunings.COMMON) }
                CurrentTuningItem(tuning = current, saved = saved, onSave = onSave, onSelect = onSelect)
            }
        }

        // Favourite Tunings
        if (favourites.isNotEmpty()) {
            item("favs") { SectionLabel(stringResource(R.string.tuning_list_favourites)) }
            items(favsList, key = { "fav-${it.instrument}-[${it.toFullString()}]" }) {
                FavouritableTuningItem(tuning = it, favourited = true, onFavouriteSet = onFavouriteSet, onSelect = onSelect)
            }
        }

        // Custom Tunings
        if (custom.isNotEmpty()) {
            item("cus") { SectionLabel(stringResource(R.string.tuning_list_custom)) }
            items(customList, key = { "${it.instrument}-[${it.toFullString()}]" }) {
                val favourited = remember(favourites) { it.hasEquivalentIn(favourites) }
                CustomTuningItem(tuning = it, favourited = favourited, onFavouriteSet = onFavouriteSet, onSelect = onSelect, onDelete = onDelete)
            }
        }

        // All Tunings
        item("all") {
            SectionLabel(stringResource(R.string.all_tunings))
        }
        stickyHeader("filter-bar") {
            var stuck by remember { mutableStateOf(false) }
            Surface(
                color = MaterialTheme.colors.background,
                elevation = if (stuck) 2.dp else 0.dp,
                modifier = Modifier.onGloballyPositioned {
                    stuck = it.positionInParent().y == 0f
                }
            ) {
                FilterBar(instrumentFilter, categoryFilter, instrumentFilters, categoryFilters, onSelectInstrument, onSelectCategory)
            }
        }

        tunings.forEach { group ->
            item(group.toString()) {
                SectionLabel("${group.key.first.getLocalisedName()} - ${group.key.second.getLocalisedName()} ")
            }
            items(group.value, key = { "${it.instrument}-[${it.toFullString()}]" }) {
                val favourited = remember(favourites) { it.hasEquivalentIn(favourites) }
                FavouritableTuningItem(tuning = it, favourited = favourited, onFavouriteSet = onFavouriteSet, onSelect = onSelect)
            }
        }
    }
}

// TODO: Fix recomposition.
/**
 * Chip bar containing filters for tuning instrument and category.
 *
 * @param instrumentFilter Current filter for tuning instrument.
 * @param categoryFilter Current filter for tuning category.
 * @param instrumentFilters Available instrument filters and their enabled states.
 * @param categoryFilters Available category filters and their enabled states.
 * @param onSelectInstrument Called when an instrument filter is selected.
 * @param onSelectCategory Called when an category filter is selected.
 */
@Composable
private fun FilterBar(
    instrumentFilter: Instrument?,
    categoryFilter: Category?,
    instrumentFilters: State<Map<Instrument, Boolean>>,
    categoryFilters: State<Map<Category, Boolean>>,
    onSelectInstrument: (Instrument?) -> Unit,
    onSelectCategory: (Category?) -> Unit
) {
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.width(8.dp))
        instrumentFilters.value.forEach { filter ->
            TuningFilterChip(
                filter = filter.key,
                filterText = { it.getLocalisedName() },
                enabled = filter.value,
                selected = instrumentFilter == filter.key,
                onSelect = onSelectInstrument
            )
        }
        categoryFilters.value.forEach { filter ->
            TuningFilterChip(
                filter = filter.key,
                filterText = { it.getLocalisedName() },
                enabled = filter.value,
                selected = categoryFilter == filter.key,
                onSelect = onSelectCategory
            )
        }
        Spacer(Modifier.width(8.dp))
    }
}

/**
 * Filter chip for tuning filters.
 *
 * @param filter The filter to display.
 * @param filterText Method to retrieve the localised filter name.
 * @param enabled Whether the filter is enabled to be selected.
 * @param selected Whether the filter is currently selected.
 * @param onSelect Called when the filter is selected/unselected.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun <T> TuningFilterChip(
    filter: T,
    filterText: @Composable (T) -> String,
    enabled: Boolean,
    selected: Boolean,
    onSelect: (T?) -> Unit
) {
    FilterChip(
        enabled = enabled,
        selected = selected,
        onClick = { if (enabled) if (selected) onSelect(null) else onSelect(filter) },
        border = if (selected) null else ChipDefaults.outlinedBorder,
        colors = if (!selected) ChipDefaults.outlinedFilterChipColors()
        else ChipDefaults.filterChipColors(
            backgroundColor = MaterialTheme.colors.secondaryVariant.copy(alpha = 0.12f)
                .compositeOver(MaterialTheme.colors.background),
            contentColor = MaterialTheme.colors.secondaryVariant
        ),
        selectedIcon = {
            Row {
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.Done, null, Modifier.size(ChipDefaults.SelectedIconSize))
            }
        },
        modifier = Modifier.animateContentSize()
    ) {
        Text(filterText(filter))
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
    instrumentName: String = tuning.instrument.getLocalisedName(),
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
    instrumentName: String = tuning.instrument.getLocalisedName(),
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
                },
                label = "Tuning Item Background Color"
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
    instrumentName: String = tuning.instrument.getLocalisedName(),
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
    instrumentName: String = tuning.instrument.getLocalisedName(),
    onSelect: (Tuning) -> Unit,
    trailing: (@Composable () -> Unit)? = null
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

/** @return The localised name of this instrument. */
@Composable
fun Instrument.getLocalisedName(): String {
    return stringResource(when (this) {
        Instrument.GUITAR -> R.string.instr_guitar
        Instrument.BASS -> R.string.instr_bass
        else -> R.string.instr_other
    })
}

/** @return The localised name of this category. */
@Composable
fun Category?.getLocalisedName(): String {
    return stringResource(when (this) {
        Category.COMMON -> R.string.tun_cat_common
        Category.POWER -> R.string.tun_cat_power
        Category.OPEN -> R.string.tun_cat_open
        else -> R.string.tun_cat_misc
    })
}

/** UI component displaying a tuning category label with [title] text. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.SectionLabel(title: String) {
    SectionLabel(modifier = Modifier.animateItemPlacement(), title = title)
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
    var name by rememberSaveable { mutableStateOf("") }

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
                    modifier = Modifier.fillMaxWidth(),
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
            TextButton(onClick = onDismiss, colors = secondaryTextButtonColors()) {
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
    val currentTuning = Tunings.BASS_STANDARD.higherTuning()
    val customTuning = Tuning.fromString("E4 E3 E3 E3 E2 E2")
    val favCustomTuning = Tuning.fromString("Custom", Instrument.GUITAR, null, "C#4 B3 F#3 D3 A2 D2")

    AppTheme {
        Surface {
            TuningSelectionScreen(
                current = currentTuning,
                tunings = Tunings.COMMON.groupAndSort(),
                favourites = setOf(Tuning.STANDARD, favCustomTuning),
                custom = setOf(customTuning, favCustomTuning),
                Instrument.BASS, null,
                instrumentFilters = remember { mutableStateOf(Instrument.values().dropLast(1).associateWith { true }) },
                categoryFilters = remember { mutableStateOf(Category.values().drop(1).associateWith { true }) },
                backIcon = Icons.Default.Close,
                onSave = {_,_->},
                onFavouriteSet = {_,_ ->},
                onSelect = {},
                onDelete = {},
                onSelectInstrument = {},
                onSelectCategory = {}
            ) {}
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SaveDialogPreview() {
    val customTuning = Tuning.fromString("Custom", Instrument.GUITAR, null, "C#4 B3 F#3 D3 A2 D2")

    AppTheme {
        SaveTuningDialog(tuning = customTuning, onSave = {_,_->}) {}
    }
}