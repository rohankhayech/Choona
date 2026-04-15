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

package com.rohankhayech.choona.wear.view.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.SaveAs
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.AlertDialog
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.CompactButton
import androidx.wear.compose.material3.FilledIconButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.IconToggleButton
import androidx.wear.compose.material3.IconToggleButtonDefaults
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.LocalContentColor
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TextButton
import androidx.wear.compose.material3.TitleCard
import com.rohankhayech.android.util.ui.layout.ItemScrollPosition
import com.rohankhayech.android.util.ui.layout.LazyListAutoScroll
import com.rohankhayech.android.util.ui.preview.wear.WearSizePreview
import com.rohankhayech.android.util.ui.wear.input.wearTextInput
import com.rohankhayech.choona.lib.R
import com.rohankhayech.choona.lib.controller.tunings.TuningList
import com.rohankhayech.choona.lib.model.tuning.ChromaticTuning
import com.rohankhayech.choona.lib.model.tuning.Instrument
import com.rohankhayech.choona.lib.model.tuning.InstrumentTuning
import com.rohankhayech.choona.lib.model.tuning.Tuning
import com.rohankhayech.choona.lib.model.tuning.Tuning.Category
import com.rohankhayech.choona.lib.model.tuning.Tunings
import com.rohankhayech.choona.lib.model.tuning.equivalentTo
import com.rohankhayech.choona.lib.view.util.getLocalisedName
import com.rohankhayech.choona.wear.view.components.SectionLabel
import com.rohankhayech.choona.wear.view.theme.AppTheme

/**
 * UI screen that allows the user to select a tuning for use,
 * as well as managing favourite and custom tunings.
 *
 * @param tuningList State holder for the tuning list.
 * @param pinnedInitial Whether the pinned tuning is used as the initial tuning.
 * @param onSave Called when a custom tuning is saved with the specified name.
 * @param onSelect Called when a tuning is selected.
 * @param onSelectChromatic Called when chromatic tuning is selected.
 * @param onOpenTuningEditor Called when the edit tuning screen is opened.
 * @param onDismiss Called when the screen is dismissed.
 *
 * @author Rohan Khayech
 */
@Composable
fun TuningListScreen(
    tuningList: TuningList,
    pinnedInitial: Boolean,
    onSave: (String?, InstrumentTuning) -> Unit = { _, _ -> },
    onSelect: (InstrumentTuning) -> Unit,
    onSelectChromatic: () -> Unit,
    onOpenTuningEditor: (InstrumentTuning, Boolean) -> Unit = { _, _ -> },
    onDismiss: () -> Unit
) {
    // Collect UI state.
    val current by tuningList.current.collectAsStateWithLifecycle()
    val currentSaved by tuningList.currentSaved.collectAsStateWithLifecycle()
    val favourites by tuningList.favourites.collectAsStateWithLifecycle()
    val custom by tuningList.custom.collectAsStateWithLifecycle()
    val tunings by tuningList.filteredTunings.collectAsStateWithLifecycle()
    val instrumentFilter by tuningList.instrumentFilter.collectAsStateWithLifecycle()
    val categoryFilter by tuningList.categoryFilter.collectAsStateWithLifecycle()
    val instrumentFilters = tuningList.instrumentFilters.collectAsStateWithLifecycle()
    val categoryFilters = tuningList.categoryFilters.collectAsStateWithLifecycle()
    val pinned by tuningList.pinned.collectAsStateWithLifecycle()

    TuningSelectionScreen(
        current = current,
        currentSaved = currentSaved,
        tunings = tunings,
        favourites = favourites,
        custom = custom,
        pinned = pinned,
        pinnedInitial = pinnedInitial,
        instrumentFilter = instrumentFilter,
        categoryFilter = categoryFilter,
        instrumentFilters = instrumentFilters,
        categoryFilters = categoryFilters,
        isFavourite = { tuningList.run { this@TuningSelectionScreen.isFavourite() } },
        onSelectInstrument = { tuningList.filterBy(instrument = it) },
        onSelectCategory = { tuningList.filterBy(category = it) },
        onSave = { name, tuning ->
            tuningList.addCustom(name, tuning)
            onSave(name, tuning)
        },
        onFavouriteSet = tuningList::setFavourited,
        onSelect = {
            when (it) {
                is ChromaticTuning -> onSelectChromatic()
                is InstrumentTuning -> onSelect(it)
            }
        },
        onDelete = tuningList::removeCustom,
        onOpenTuningEditor = onOpenTuningEditor,
        onDismiss = onDismiss,
        onPin = { tuningList.setPinned(it) },
        onUnpin = { tuningList.unpinTuning() }
    )
}

/**
 * UI screen that allows the user to select a tuning for use,
 * as well as managing favourite and custom tunings.
 *
 * @param current Currently selected tuning, or null if N/A.
 * @param currentSaved Whether the current tuning is saved as a custom or built-in tuning.
 * @param tunings Current collection of filtered and grouped tunings.
 * @param favourites Set of tunings marked as favourites.
 * @param custom Set of custom tunings saved by the user.
 * @param pinned The tuning pinned to be used when the app is first opened.
 * @param pinnedInitial Whether the pinned tuning is used as the initial tuning.
 * @param instrumentFilter Current filter for tuning instrument.
 * @param categoryFilter Current filter for tuning category.
 * @param instrumentFilters Available instrument filters and their enabled states.
 * @param categoryFilters Available category filters and their enabled states.
 * @param isFavourite Function that returns whether a tuning is marked as a favourite.
 * @param onSelectInstrument Called when an instrument filter is selected.
 * @param onSelectCategory Called when a category filter is selected.
 * @param onSave Called when a custom tuning is saved with the specified name.
 * @param onFavouriteSet Called when a tuning is favourited or unfavourited.
 * @param onSelect Called when a tuning is selected.
 * @param onDelete Called when a custom tuning is deleted.
 * @param onOpenTuningEditor Called when the edit tuning screen is opened.
 * @param onDismiss Called when the screen is dismissed.
 * @param onPin Called when a tuning is pinned as default.
 * @param onUnpin Called when the pinned tuning is unpinned as default.
 *
 * @author Rohan Khayech
 */
@Composable
fun TuningSelectionScreen(
    current: Tuning,
    currentSaved: Boolean,
    tunings: Map<Pair<Instrument, Category?>, List<InstrumentTuning>>,
    favourites: Set<Tuning>,
    custom: Set<InstrumentTuning>,
    pinned: Tuning,
    pinnedInitial: Boolean,
    instrumentFilter: Instrument?,
    categoryFilter: Category?,
    instrumentFilters: State<Map<Instrument, Boolean>>,
    categoryFilters: State<Map<Category, Boolean>>,
    isFavourite: Tuning.() -> Boolean,
    onSelectInstrument: (Instrument?) -> Unit,
    onSelectCategory: (Category?) -> Unit,
    onSave: (String?, InstrumentTuning) -> Unit,
    onFavouriteSet: (Tuning, Boolean) -> Unit,
    onSelect: (Tuning) -> Unit,
    onDelete: (InstrumentTuning) -> Unit,
    onOpenTuningEditor: (InstrumentTuning, Boolean) -> Unit,
    onDismiss: () -> Unit,
    onPin: (tuning: Tuning) -> Unit,
    onUnpin: () -> Unit
) {
    val listState = rememberScalingLazyListState()

    var showSaveDialog by remember { mutableStateOf(false) }

    var showDeleteDialogFor by remember { mutableStateOf<InstrumentTuning?>(null) }

    ScreenScaffold(
        scrollState = listState,
    ) { padding ->
        TuningList(
            padding = padding,
            listState = listState,
            current = current,
            currentSaved = currentSaved,
            tunings = tunings,
            favourites = favourites,
            custom = custom,
            pinned = pinned,
            pinnedInitial = pinnedInitial,
            instrumentFilter = instrumentFilter,
            categoryFilter = categoryFilter,
            instrumentFilters = instrumentFilters,
            categoryFilters = categoryFilters,
            isFavourite = isFavourite,
            onSelectInstrument = onSelectInstrument,
            onSelectCategory = onSelectCategory,
            onSave = { showSaveDialog = true },
            onFavouriteSet = onFavouriteSet,
            onSelect = onSelect,
            onDelete = { showDeleteDialogFor = it },
            onOpenTuningEditor = onOpenTuningEditor,
            onPin = onPin,
            onUnpin = onUnpin
        )
    }

    // Save dialog.
    if (current is InstrumentTuning) {
        SaveTuningDialog(
            visible = showSaveDialog,
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

    // Delete dialog.
    showDeleteDialogFor?.let {
        DeleteTuningDialog(
            visible = true,
            tuning = it,
            onDelete = { tuning ->
                onDelete(tuning)
                showDeleteDialogFor = null
            },
            onDismiss = {
                showDeleteDialogFor = null
            }
        )
    }
}

/**
 * UI component displaying the current tuning and a list of favourite, custom and common tunings.
 *
 * @param modifier The modifier to apply to this layout.
 * @param listState State controller for the lazy list.
 * @param current Currently selected tuning, or null if N/A.
 * @param currentSaved Whether the current tuning is saved as a custom or built-in tuning.
 * @param tunings Current collection of filtered and grouped tunings.
 * @param favourites Set of tunings marked as favourites.
 * @param custom Set of custom tunings saved by the user.
 * @param pinned The tuning pinned to be used when the app is first opened.
 * @param pinnedInitial Whether the pinned tuning is used as the initial tuning.
 * @param instrumentFilter Current filter for tuning instrument.
 * @param categoryFilter Current filter for tuning category.
 * @param instrumentFilters Available instrument filters and their enabled states.
 * @param categoryFilters Available category filters and their enabled states.
 * @param onSelectInstrument Called when an instrument filter is selected.
 * @param onSelectCategory Called when a category filter is selected.
 * @param onSave Called when a custom tuning is saved with the specified name.
 * @param onFavouriteSet Called when a tuning is favourited or unfavourited.
 * @param onPin Called when a tuning is pinned as default.
 * @param onUnpin Called when the pinned tuning is unpinned as default.
 * @param onSelect Called when a tuning is selected.
 * @param onDelete Called when a custom tuning is deleted.
 * @param onOpenTuningEditor Called when the edit tuning screen is opened.
 */
@Composable
fun TuningList(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
    listState: ScalingLazyListState = rememberScalingLazyListState(),
    current: Tuning,
    currentSaved: Boolean,
    tunings: Map<Pair<Instrument, Category?>, List<InstrumentTuning>>,
    favourites: Set<Tuning>,
    custom: Set<InstrumentTuning>,
    pinned: Tuning,
    pinnedInitial: Boolean,
    instrumentFilter: Instrument?,
    categoryFilter: Category?,
    instrumentFilters: State<Map<Instrument, Boolean>>,
    categoryFilters: State<Map<Category, Boolean>>,
    isFavourite: Tuning.() -> Boolean,
    onSelectInstrument: (Instrument?) -> Unit,
    onSelectCategory: (Category?) -> Unit,
    onSave: (InstrumentTuning) -> Unit,
    onFavouriteSet: (Tuning, Boolean) -> Unit,
    onPin: (Tuning) -> Unit,
    onUnpin: () -> Unit,
    onSelect: (Tuning) -> Unit,
    onDelete: (InstrumentTuning) -> Unit,
    onOpenTuningEditor: (InstrumentTuning, Boolean) -> Unit,
) {
    val favsList = remember(favourites) { favourites.toList() }
    val customList = remember(custom) { custom.toList() }

    val currentPinned = remember(pinned, current) {
        pinned equivalentTo current
    }
    val pinnedInFavs = remember(favsList, pinned) {
        pinned.isFavourite()
    }
    val pinnedIsStandard = remember(pinned) { pinned equivalentTo Tunings.STANDARD }

    ScalingLazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            top = padding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding()
        ),
        state = listState,
        autoCentering = AutoCenteringParams(
            itemIndex = 2
        )
    ) {
        item {
            ListHeader {
                Text(stringResource(R.string.select_tuning))
            }
        }

        // Current Tuning
        item("cur") {
            CategoryLabel(
                stringResource(R.string.tuning_list_current)
            )
        }
        item("cur-${current.key}") {
            CurrentTuningItem(
                tuning = current,
                saved = currentSaved,
                favourited = current.isFavourite(),
                pinned = currentPinned,
                pinnedInitial = pinnedInitial,
                onSave = onSave,
                onSelect = onSelect,
                onPinnedSet = { tuning, pinned ->
                    if (pinned) onPin(tuning) else onUnpin()
                },
                onFavouriteSet = onFavouriteSet
            )
        }

        if (pinnedInitial && !currentPinned && !pinnedInFavs && !pinnedIsStandard) {
            item("pinned") { CategoryLabel(stringResource(R.string.tuning_list_pinned)) }
            item("pinned-${pinned.key}"
            ) {
                FavouritableTuningItem(
                    tuning = pinned,
                    favourited = false,
                    pinned = true,
                    pinnedInitial = true,
                    onFavouriteSet = onFavouriteSet,
                    onSelect = onSelect,
                    onUnpin = onUnpin
                )
            }
        }

        // Favourite Tunings
        if (favourites.isNotEmpty()) {
            item("favs") { CategoryLabel(stringResource(R.string.tuning_list_favourites)) }
            items(favsList, key = { "fav-${it.key}" }) {
                val isPinned = remember(pinned) { it equivalentTo pinned }
                FavouritableTuningItem(
                    tuning = it,
                    favourited = true,
                    pinned = isPinned,
                    pinnedInitial = pinnedInitial,
                    onFavouriteSet = onFavouriteSet,
                    onSelect = onSelect,
                    onUnpin = onUnpin
                )
            }
        }

        // Custom Tunings
        item("cus") { CategoryLabel(stringResource(R.string.tuning_list_custom)) }
        item("add-custom") {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = {
                    onOpenTuningEditor(current as? InstrumentTuning ?: Tunings.STANDARD, true)
                }
            ) {
                Icon(Icons.Default.Add, null)
                Text(stringResource(R.string.new_tuning), modifier = Modifier.padding(start = 8.dp))
            }
        }
        items(customList, key = { it.key }) {
            val favourited = it.isFavourite()
            val isPinned = remember(pinned) { pinned equivalentTo it }
            CustomTuningItem(
                tuning = it,
                favourited = favourited,
                pinned = isPinned,
                pinnedInitial = pinnedInitial,
                onFavouriteSet = onFavouriteSet,
                onUnpin = onUnpin,
                onSelect = onSelect,
                onDelete = onDelete,
                onEdit = { onOpenTuningEditor(it, false) }
            )
        }


        // All Tunings
        item("all") {
            CategoryLabel(stringResource(R.string.all_tunings))
        }
        item("filter-bar") {
            FilterBar(instrumentFilter, categoryFilter, instrumentFilters, categoryFilters, onSelectInstrument, onSelectCategory)
        }

        tunings.forEach { group ->
            item(group.key.toString()) {
                CategoryLabel("${group.key.first.getLocalisedName()} ‧ ${group.key.second.getLocalisedName()}")
            }
            items(group.value, key = { it.key }) {
                val favourited = it.isFavourite()
                val isPinned = remember(pinned) { it equivalentTo pinned }
                FavouritableTuningItem(
                    tuning = it,
                    favourited = favourited,
                    pinned = isPinned,
                    pinnedInitial = pinnedInitial,
                    onFavouriteSet = onFavouriteSet,
                    onSelect = onSelect,
                    onUnpin = onUnpin
                )
            }
        }
        if (instrumentFilter == null && (categoryFilter == null || categoryFilter == Category.MISC)) {
            item(Category.MISC.toString()) {
                CategoryLabel(Category.MISC.getLocalisedName())
            }
            item(key = ChromaticTuning.key) {
                FavouritableTuningItem(
                    ChromaticTuning,
                    remember(favourites) { ChromaticTuning.isFavourite() },
                    pinned = pinned is ChromaticTuning,
                    pinnedInitial = pinnedInitial,
                    onFavouriteSet = onFavouriteSet,
                    onSelect = onSelect,
                    onUnpin = onUnpin
                )
            }
        }
    }
}

/**
 * Chip bar containing filters for tuning instrument and category.
 *
 * @param instrumentFilter Current filter for tuning instrument.
 * @param categoryFilter Current filter for tuning category.
 * @param instrumentFilters Available instrument filters and their enabled states.
 * @param categoryFilters Available category filters and their enabled states.
 * @param onSelectInstrument Called when an instrument filter is selected.
 * @param onSelectCategory Called when a category filter is selected.
 */
// Note: Recomposition could be improved.
@Composable
private fun FilterBar(
    instrumentFilter: Instrument?,
    categoryFilter: Category?,
    instrumentFilters: State<Map<Instrument, Boolean>>,
    categoryFilters: State<Map<Category, Boolean>>,
    onSelectInstrument: (Instrument?) -> Unit,
    onSelectCategory: (Category?) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val instrListState = rememberLazyListState()
        // Center the selected button.
        if (instrumentFilter != null) {
            val index = instrumentFilters.value.keys.indexOf(instrumentFilter)
            if (index >= 0) {
                LazyListAutoScroll(
                    instrListState,
                    index,
                    ItemScrollPosition.Center
                )
            }
        }
        LazyRow(
            state = instrListState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(instrumentFilters.value.entries.toList()) { filter ->
                TuningFilterChip(
                    filter = filter.key,
                    filterText = filter.key.getLocalisedName(),
                    enabled = filter.value,
                    selected = instrumentFilter == filter.key,
                    onSelect = onSelectInstrument
                )
            }
        }

        val catListState = rememberLazyListState()
        // Center the selected button.
        if (categoryFilter != null) {
            val index = categoryFilters.value.keys.indexOf(categoryFilter)
            if (index >= 0) {
                LazyListAutoScroll(
                    catListState,
                    index,
                    ItemScrollPosition.Center
                )
            }
        }
        LazyRow(
            state = catListState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(categoryFilters.value.entries.toList()) { filter ->
                TuningFilterChip(
                    filter = filter.key,
                    filterText = filter.key.getLocalisedName(),
                    enabled = filter.value,
                    selected = categoryFilter == filter.key,
                    onSelect = onSelectCategory
                )
            }
        }
    }
}

/**
 * Filter chip for tuning filters.
 *
 * @param filter The filter to display.
 * @param filterText The localised filter name.
 * @param enabled Whether the filter is enabled to be selected.
 * @param selected Whether the filter is currently selected.
 * @param onSelect Called when the filter is selected/unselected.
 */
@Composable
private fun <T> TuningFilterChip(
    filter: T,
    filterText: String,
    enabled: Boolean,
    selected: Boolean,
    onSelect: (T?) -> Unit
) {
    // Animate content color by selected and tuned state.
    val contentColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.onPrimaryContainer
        else LocalContentColor.current,
        label = "Filter Chip Content Color"
    )

    // Animate background color by selected state.
    val backgroundColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceContainerLow,
        label = "Filter Chip Background Color"
    )

    Button(
        modifier = Modifier.height(32.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        onClick = remember(onSelect, filter, selected) {{ if (selected) onSelect(null) else onSelect(filter) }}
    ) {
        Text(
            filterText,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * List item displaying the current tuning and an option to save it.
 *
 * @param tuning Currently selected tuning.
 * @param saved Whether the tuning is currently saved.
 * @param favourited Whether the tuning is currently favourited.
 * @param pinned Whether the tuning is currently pinned.
 * @param pinnedInitial Whether the pinned tuning is used as the initial tuning.
 * @param onSave Called when the save button is pressed.
 * @param onSelect Called when this tuning is selected.
 * @param onPinnedSet Called when the pin button is pressed.
 * @param onFavouriteSet Called when the favourite button is pressed.
 */
@Composable
private fun CurrentTuningItem(
    tuning: Tuning,
    saved: Boolean,
    favourited: Boolean,
    pinned: Boolean,
    pinnedInitial: Boolean,
    onSave: (InstrumentTuning) -> Unit,
    onSelect: (Tuning) -> Unit,
    onPinnedSet: (Tuning, Boolean) -> Unit,
    onFavouriteSet: (Tuning, Boolean) -> Unit
) {
    val standard = remember(tuning) { tuning equivalentTo Tunings.STANDARD }
    TuningItem(
        tuning = tuning,
        favourited = favourited,
        pinned = pinned,
        pinnedInitial = pinnedInitial,
        onSelect = onSelect,
        actions = if ((!standard && (pinned || (saved && pinnedInitial))) || (tuning is InstrumentTuning && !saved) || saved) {
            {
            if(!standard && (pinned || (saved && pinnedInitial))) {
                IconToggleButton(
                    enabled = pinnedInitial,
                    checked = pinned,
                    onCheckedChange = {
                        onPinnedSet(tuning, it)
                    },
                    colors = IconToggleButtonDefaults.colors(
                        checkedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        checkedContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        if (pinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                        contentDescription = if (pinned) stringResource(R.string.unpin) else stringResource(R.string.pin)
                    )
                }
            }
            if (saved) {
                IconToggleButton(
                    checked = favourited,
                    onCheckedChange = { onFavouriteSet(tuning, !favourited) },
                    colors = IconToggleButtonDefaults.colors(
                        checkedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        checkedContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                ) {
                    Icon(
                        if (favourited) Icons.Default.Star else Icons.Default.StarOutline,
                        contentDescription = if (favourited) stringResource(R.string.unfavourite) else stringResource(R.string.favourite)
                    )
                }
            }
                if (tuning is InstrumentTuning && !saved) {
                FilledIconButton(
                    onClick = { onSave(tuning) }
                ) {
                    Icon(
                        Icons.Default.SaveAs,
                        contentDescription = stringResource(R.string.save)
                    )
                }
            }
        }} else null
    )
}

/**
 * List item displaying a custom tuning, with options to favourite or remove it.
 *
 * @param tuning The tuning to display.
 * @param favourited Whether the tuning is currently marked as a favourite.
 * @param pinned Whether the tuning is currently pinned.
 * @param pinnedInitial Whether the pinned tuning is used as the initial tuning.
 * @param onFavouriteSet Called when the favourite button is pressed.
 * @param onUnpin Called when this tuning is unpinned as default.
 * @param onSelect Called when this tuning is selected.
 * @param onDelete Called when this tuning is swiped to be removed.
 * @param onEdit Called when the edit button is pressed.
 */
@Composable
private fun CustomTuningItem(
    tuning: InstrumentTuning,
    favourited: Boolean,
    pinned: Boolean,
    pinnedInitial: Boolean,
    onFavouriteSet: (Tuning, Boolean) -> Unit,
    onUnpin: () -> Unit,
    onSelect: (Tuning) -> Unit,
    onDelete: (InstrumentTuning) -> Unit,
    onEdit: () -> Unit
) {
    val standard = remember(tuning) { tuning equivalentTo Tunings.STANDARD }
    TuningItem(
        tuning = tuning,
        favourited = favourited,
        pinned = pinned,
        pinnedInitial = pinnedInitial,
        onSelect = onSelect
    ) {
        if (pinned && !standard) {
            IconToggleButton(
                enabled = pinnedInitial,
                checked = true,
                colors = IconToggleButtonDefaults.colors(
                    checkedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    checkedContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                onCheckedChange = { onUnpin() }
            ) {
                Icon(
                    Icons.Default.PushPin,
                    contentDescription = stringResource(R.string.unpin)
                )
            }
        }

        IconToggleButton(
            checked = favourited,
            onCheckedChange = { onFavouriteSet(tuning, !favourited) },
            colors = IconToggleButtonDefaults.colors(
                checkedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                checkedContentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        ) {
            Icon(
                if (favourited) Icons.Default.Star else Icons.Default.StarOutline,
                contentDescription = if (favourited) stringResource(R.string.unfavourite) else stringResource(R.string.favourite)
            )
        }

        FilledIconButton(
            onClick = onEdit
        ) {
            Icon(
                Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit_tuning)
            )
        }

        FilledIconButton(
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            onClick = { onDelete(tuning) }
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete)
            )
        }
    }
}

/**
 * List item displaying a tuning, with an option to favourite it.
 *
 * @param tuning The tuning to display.
 * @param favourited Whether the tuning is currently marked as a favourite.
 * @param pinned Whether the tuning is currently pinned.
 * @param pinnedInitial Whether the pinned tuning is used as the initial tuning.
 * @param onFavouriteSet Called when the favourite button is pressed.
 * @param onSelect Called when this tuning is selected.
 * @param onUnpin Called when this tuning is unpinned as default.
 */
@Composable
private fun FavouritableTuningItem(
    tuning: Tuning,
    favourited: Boolean,
    pinned: Boolean,
    pinnedInitial: Boolean,
    onFavouriteSet: (Tuning, Boolean) -> Unit,
    onSelect: (Tuning) -> Unit,
    onUnpin: () -> Unit
) {
    val standard = remember(tuning) { tuning equivalentTo Tunings.STANDARD }
    TuningItem(
        tuning = tuning,
        favourited = favourited,
        pinned = pinned,
        pinnedInitial = pinnedInitial,
        onSelect = onSelect
    ) {
        if (pinned && !standard) {
            IconToggleButton(
                enabled = pinnedInitial,
                checked = true,
                colors = IconToggleButtonDefaults.colors(
                    checkedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    checkedContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                onCheckedChange = { onUnpin() }
            ) {
                Icon(
                    Icons.Default.PushPin,
                    contentDescription = stringResource(R.string.unpin)
                )
            }
        }
        IconToggleButton(
            checked = favourited,
            onCheckedChange = { onFavouriteSet(tuning, !favourited) },
            colors = IconToggleButtonDefaults.colors(
                checkedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                checkedContentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        ) {
            Icon(
                if (favourited) Icons.Default.Star else Icons.Default.StarOutline,
                contentDescription = if (favourited) stringResource(R.string.unfavourite) else stringResource(R.string.favourite)
            )
        }
    }
}

/**
 * List item displaying a custom tuning, with support for long press actions.
 *
 * @param tuning The tuning to display.
 * @param favourited Whether the tuning is currently favourited.
 * @param pinned Whether the tuning is currently pinned.
 * @param pinnedInitial Whether the pinned tuning is used as the initial tuning.
 * @param onSelect Called when this tuning is selected.
 * @param actions The actions to display on long press.
 */
@Composable
private fun TuningItem(
    tuning: Tuning,
    favourited: Boolean = false,
    pinned: Boolean = false,
    pinnedInitial: Boolean = false,
    onSelect: (Tuning) -> Unit,
    actions: @Composable (RowScope.() -> Unit)? = null
) {
    val name = when (tuning) {
        is InstrumentTuning -> tuning.name
        is ChromaticTuning -> stringResource(R.string.chromatic)
        else -> throw IllegalStateException("Invalid tuning type.")
    }

    val strings = remember(tuning) {
        (tuning as? InstrumentTuning)?.strings
            ?.reversed()
            ?.joinToString(
                separator = ", ",
            ) { it.toFullString() } ?: ""
    }

    val desc = when (tuning) {
        is InstrumentTuning -> strings
        is ChromaticTuning -> stringResource(R.string.chromatic_desc)
        else -> throw IllegalStateException("Invalid tuning type.")
    }

    var expanded by remember { mutableStateOf(false) }

    TitleCard(
        modifier = Modifier.padding(horizontal = 16.dp),
        title = {
            Text(name, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        subtitle = {
            Text(desc)

        },
        time = if (tuning is InstrumentTuning || (pinned && pinnedInitial) || favourited) {
            {
                Row(Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    if (tuning is InstrumentTuning) {
                        Text("${tuning.instrument.getLocalisedName()} ‧ ${tuning.numStrings()}" + stringResource(R.string.num_strings_suffix))
                    } else {
                        Text(stringResource(R.string.tun_cat_misc))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (pinned && pinnedInitial && !(tuning equivalentTo Tunings.STANDARD)) {
                            Icon(
                                Icons.Default.PushPin,
                                contentDescription = stringResource(R.string.tuning_list_pinned),
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (favourited) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = stringResource(R.string.tuning_list_favourites),
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else null,
        onClick = { onSelect(tuning) },
        onLongClick = { expanded = true }
    )

    AlertDialog(
        modifier = Modifier.fillMaxSize(),
        dismissButton = {
            TextButton(onClick = { expanded = false }) {
                Text(text = stringResource(R.string.nav_back))
            }
        },
        confirmButton = {},
        onDismissRequest = { expanded = false },
        visible = expanded && actions != null,
        title = {
            Text(name)
        },
        verticalArrangement = Arrangement.Center,
    ) {
        item {
            FlowRow(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                itemVerticalAlignment = Alignment.CenterVertically,
                maxItemsInEachRow = 2
            ) {
                actions?.invoke(this)
            }
        }
    }
}

/** UI component displaying a tuning category label with [title] text. */
@Composable
private fun CategoryLabel(title: String) {
    SectionLabel(title = title, modifier = Modifier.padding(horizontal = 16.dp))
}

/**
 * Dialog allowing the user to enter a name and save the specified tuning.
 *
 * @param tuning The tuning to save.
 * @param onSave Called when save button is pressed. Provides the saved tuning and the entered name.
 * @param onDismiss Called when the save dialog is dismissed.
 */
@Composable
fun SaveTuningDialog(
    visible: Boolean,
    tuning: InstrumentTuning,
    onSave: (String?, InstrumentTuning) -> Unit,
    onDismiss: () -> Unit
) {
    var name by rememberSaveable(visible, tuning.name) { mutableStateOf(tuning.name) }

    AlertDialog(
        confirmButton = {
            Button(onClick = { onSave(name.ifBlank { null }, tuning) }) {
                Text(text = stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
        onDismissRequest = onDismiss,
        visible = visible,
        title = { Text(
            stringResource(R.string.dialog_title_save_tuning)
        )},
    ) {
        item {
            CompactButton(
                onClick = wearTextInput(
                    stringResource(id = R.string.dialog_title_save_tuning),
                    "tuning-name"
                ) {
                    name = it
                },
            ) {
                Text(text = name)
            }
        }
    }
}

/**
 * Dialog allowing the user to delete the specified saved tuning.
 *
 * @param tuning The tuning to delete.
 * @param onDelete Called when save button is pressed. Provides the deleted tuning.
 * @param onDismiss Called when the delete dialog is dismissed.
 */
@Composable
fun DeleteTuningDialog(
    visible: Boolean,
    tuning: InstrumentTuning,
    onDelete: (InstrumentTuning) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ), onClick = { onDelete(tuning) }) {
                Text(text = stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
        onDismissRequest = onDismiss,
        visible = visible,
        title = { Text(
            "${stringResource(R.string.delete)} ${tuning.name}?"
        )},
        icon = {
            Icon(
                Icons.Default.Delete,
                tint = MaterialTheme.colorScheme.error,
                contentDescription = null
            )
        }
    )
}

// Previews

@WearSizePreview
@Composable
private fun Preview() {
    val currentTuning = Tunings.BASS_STANDARD.higherTuning()
    val customTuning = InstrumentTuning.fromString("E4 E3 E3 E3 E2 E2")
    val favCustomTuning = InstrumentTuning.fromString("Custom", Instrument.GUITAR, null, "C#4 B3 F#3 D3 A2 D2")

    AppTheme {
        AppScaffold {
            TuningSelectionScreen(
                current = currentTuning,
                currentSaved = false,
                tunings = TuningList.GROUPED_TUNINGS,
                pinned = Tunings.WHOLE_STEP_DOWN,
                pinnedInitial = true,
                favourites = setOf(Tunings.STANDARD, ChromaticTuning),
                custom = setOf(customTuning as InstrumentTuning, favCustomTuning as InstrumentTuning),
                instrumentFilter = Instrument.BASS,
                categoryFilter = null,
                instrumentFilters = remember { mutableStateOf(Instrument.entries.dropLast(1).associateWith { true }) },
                categoryFilters = remember { mutableStateOf(Category.entries.associateWith { true }) },
                isFavourite = { this == favCustomTuning },
                onSave = { _, _ -> },
                onFavouriteSet = { _, _ -> },
                onSelect = {},
                onDelete = {},
                onOpenTuningEditor = {_,_->},
                onSelectInstrument = {},
                onSelectCategory = {},
                onDismiss = {},
                onPin = { _ -> },
                onUnpin = {},
            )
        }
    }
}
