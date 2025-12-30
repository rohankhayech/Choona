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

package com.rohankhayech.choona.wear.view.screens

import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
import androidx.wear.compose.material3.IconButton
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
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.wearableExtender
import androidx.wear.tooling.preview.devices.WearDevices
import com.rohankhayech.android.util.ui.layout.ItemScrollPosition
import com.rohankhayech.android.util.ui.layout.LazyListAutoScroll
import com.rohankhayech.choona.lib.R
import com.rohankhayech.choona.lib.controller.tunings.TuningList
import com.rohankhayech.choona.lib.model.tuning.Instrument
import com.rohankhayech.choona.lib.model.tuning.Tuning
import com.rohankhayech.choona.lib.model.tuning.Tuning.Category
import com.rohankhayech.choona.lib.model.tuning.TuningEntry
import com.rohankhayech.choona.lib.model.tuning.Tunings
import com.rohankhayech.choona.wear.view.components.SectionLabel
import com.rohankhayech.choona.wear.view.theme.AppTheme
import com.rohankhayech.choona.wear.view.theme.extColors
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * UI screen that allows the user to select a tuning for use,
 * as well as managing favourite and custom tunings.
 *
 * @param tuningList State holder for the tuning list.
 * @param backIcon Icon used for the back navigation button.
 * @param pinnedInitial Whether the pinned tuning is used as the initial tuning.
 * @param onSave Called when a custom tuning is saved with the specified name.
 * @param onSelect Called when a tuning is selected.
 * @param onSelectChromatic Called when chromatic tuning is selected.
 * @param onDismiss Called when the screen is dismissed.
 *
 * @author Rohan Khayech
 */
@Composable
fun TuningListScreen(
    tuningList: TuningList,
    backIcon: ImageVector?,
    pinnedInitial: Boolean,
    onSave: (String?, Tuning) -> Unit = {_,_->},
    onSelect: (Tuning) -> Unit,
    onSelectChromatic: () -> Unit,
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
        backIcon = backIcon,
        deletedTuning = tuningList.deletedTuning,
        isFavourite = { tuningList.run { this@TuningSelectionScreen.isFavourite() } },
        onSelectInstrument = { tuningList.filterBy(instrument = it) },
        onSelectCategory = { tuningList.filterBy(category = it) },
        onSave = { name, tuning ->
            tuningList.addCustom(name, tuning)
            onSave(name, tuning)
        },
        onFavouriteSet = tuningList::setFavourited,
        onSelect = {
            if (it is TuningEntry.ChromaticTuning) {
                onSelectChromatic()
            } else {
                onSelect(it.tuning!!)
            }
        },
        onDelete = { tuningList.removeCustom(it) },
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
 * @param backIcon Icon used for the back navigation button.
 * @param deletedTuning Event indicating the specified tuning was deleted.
 * @param isFavourite Function that returns whether a tuning is marked as a favourite.
 * @param onSelectInstrument Called when an instrument filter is selected.
 * @param onSelectCategory Called when an category filter is selected.
 * @param onSave Called when a custom tuning is saved with the specified name.
 * @param onFavouriteSet Called when a tuning is favourited or unfavourited.
 * @param onSelect Called when a tuning is selected.
 * @param onDelete Called when a custom tuning is deleted.
 * @param onDismiss Called when the screen is dismissed.
 * @param onPin Called when a tuning is pinned as default.
 * @param onUnpin Called when the pinned tuning is unpinned as default.
 *
 * @author Rohan Khayech
 */
@Composable
fun TuningSelectionScreen(
    current: TuningEntry? = null,
    currentSaved: Boolean,
    tunings: Map<Pair<Instrument, Category?>, List<TuningEntry.InstrumentTuning>>,
    favourites: Set<TuningEntry>,
    custom: Set<TuningEntry.InstrumentTuning>,
    pinned: TuningEntry,
    pinnedInitial: Boolean,
    instrumentFilter: Instrument?,
    categoryFilter: Category?,
    instrumentFilters: State<Map<Instrument, Boolean>>,
    categoryFilters: State<Map<Category, Boolean>>,
    backIcon: ImageVector?,
    deletedTuning: SharedFlow<Tuning>,
    isFavourite: TuningEntry.() -> Boolean,
    onSelectInstrument: (Instrument?) -> Unit,
    onSelectCategory: (Category?) -> Unit,
    onSave: (String?, Tuning) -> Unit,
    onFavouriteSet: (TuningEntry, Boolean) -> Unit,
    onSelect: (TuningEntry) -> Unit,
    onDelete: (Tuning) -> Unit,
    onDismiss: () -> Unit,
    onPin: (tuning: TuningEntry) -> Unit,
    onUnpin: () -> Unit
) {
    val listState = rememberScalingLazyListState()

    var showSaveDialog by rememberSaveable { mutableStateOf(false) }

    var showDeleteDialogFor by rememberSaveable { mutableStateOf<Tuning?>(null) }

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
            onPin = onPin,
            onUnpin = onUnpin
        )
    }

    // Save dialog.
    if (current?.tuning != null) {
        SaveTuningDialog(
            visible = showSaveDialog,
            tuning = current.tuning!!,
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
 * @param onSelectCategory Called when an category filter is selected.
 * @param onSave Called when a custom tuning is saved with the specified name.
 * @param onFavouriteSet Called when a tuning is favourited or unfavourited.
 * @param onPin Called when a tuning is pinned as default.
 * @param onUnpin Called when the pinned tuning is unpinned as default.
 * @param onSelect Called when a tuning is selected.
 * @param onDelete Called when a custom tuning is deleted.
 */
@Composable
fun TuningList(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
    listState: ScalingLazyListState = rememberScalingLazyListState(),
    current: TuningEntry? = null,
    currentSaved: Boolean,
    tunings: Map<Pair<Instrument, Category?>, List<TuningEntry>>,
    favourites: Set<TuningEntry>,
    custom: Set<TuningEntry.InstrumentTuning>,
    pinned: TuningEntry,
    pinnedInitial: Boolean,
    instrumentFilter: Instrument?,
    categoryFilter: Category?,
    instrumentFilters: State<Map<Instrument, Boolean>>,
    categoryFilters: State<Map<Category, Boolean>>,
    isFavourite: TuningEntry.() -> Boolean,
    onSelectInstrument: (Instrument?) -> Unit,
    onSelectCategory: (Category?) -> Unit,
    onSave: (Tuning) -> Unit,
    onFavouriteSet: (TuningEntry, Boolean) -> Unit,
    onPin: (TuningEntry) -> Unit,
    onUnpin: () -> Unit,
    onSelect: (TuningEntry) -> Unit,
    onDelete: (Tuning) -> Unit
) {
    val favsList = remember(favourites) { favourites.toList() }
    val customList = remember(custom) { custom.toList() }

    val currentPinned = remember(pinned, current) {
        current == pinned ||
            pinned.tuning?.equivalentTo(current?.tuning) == true
    }
    val pinnedInFavs = remember(favsList, pinned) {
        pinned.isFavourite()
    }
    val pinnedIsStandard = remember(pinned) { pinned.tuning?.equivalentTo(Tunings.STANDARD) == true }

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
        current?.let {
            item("cur") {
                CategoryLabel(
                    stringResource(R.string.tuning_list_current)
                )
            }
            item("cur-${current.key}") {
                CurrentTuningItem(
                    tuning = current,
                    saved = currentSaved,
                    pinned = currentPinned,
                    pinnedInitial = pinnedInitial,
                    onSave = onSave,
                    onSelect = onSelect
                ) { tuning, pinned ->
                    if (pinned) onPin(tuning) else onUnpin()
                }
            }
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
                val isPinned = remember(pinned) { it == pinned || it.tuning?.equivalentTo(pinned.tuning) == true }
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
        if (custom.isNotEmpty()) {
            item("cus") { CategoryLabel(stringResource(R.string.tuning_list_custom)) }
            items(customList, key = { it.key }) {
                val favourited = it.isFavourite()
                val isPinned = remember(pinned) { it.tuning.equivalentTo(pinned.tuning) }
                CustomTuningItem(
                    tuning = it,
                    favourited = favourited,
                    pinned = isPinned,
                    pinnedInitial = pinnedInitial,
                    onFavouriteSet = onFavouriteSet,
                    onUnpin = onUnpin,
                    onSelect = onSelect,
                    onDelete = onDelete
                )
            }
        }

        // All Tunings
        item("all") {
            CategoryLabel(stringResource(R.string.all_tunings))
        }
        item("filter-bar") {
            FilterBar(instrumentFilter, categoryFilter, instrumentFilters, categoryFilters, onSelectInstrument, onSelectCategory)
        }

        tunings.forEach { group ->
            item(group.toString()) {
                CategoryLabel("${group.key.first.getLocalisedName()} ‧ ${group.key.second.getLocalisedName()}")
            }
            items(group.value, key = { it.key }) {
                val favourited = it.isFavourite()
                val isPinned = remember(pinned) { it.tuning?.equivalentTo(pinned.tuning) == true }
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
            item(key = "chromatic") {
                FavouritableTuningItem(
                    TuningEntry.ChromaticTuning,
                    remember(favourites) { TuningEntry.ChromaticTuning.isFavourite() },
                    pinned = pinned is TuningEntry.ChromaticTuning,
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
 * @param onSelectCategory Called when an category filter is selected.
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
        if (selected) MaterialTheme.extColors.green.onContainer
        else LocalContentColor.current,
        label = "Filter Chip Content Color"
    )

    // Animate background color by selected state.
    val backgroundColor by animateColorAsState(
        if (selected) MaterialTheme.extColors.green.container
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
 * @param pinned Whether the tuning is currently pinned.
 * @param pinnedInitial Whether the pinned tuning is used as the initial tuning.
 * @param onSave Called when the save button is pressed.
 * @param onSelect Called when this tuning is selected.
 * @param onPinnedSet Called when the pin button is pressed.
 */
@Composable
private fun CurrentTuningItem(
    tuning: TuningEntry,
    saved: Boolean,
    pinned: Boolean,
    pinnedInitial: Boolean,
    onSave: (Tuning) -> Unit,
    onSelect: (TuningEntry) -> Unit,
    onPinnedSet: (TuningEntry, Boolean) -> Unit
) {
    val standard = remember(tuning) { tuning.tuning?.equivalentTo(Tunings.STANDARD) == true }
    TuningItem(
        tuning = tuning,
        onSelect = onSelect,
        trailing = if ((!standard && (pinned || (saved && pinnedInitial))) || (tuning is TuningEntry.InstrumentTuning && !saved)) {{
            if(!standard && (pinned || (saved && pinnedInitial))) {
                item {
                    IconToggleButton(
                        enabled = pinnedInitial,
                        checked = pinned,
                        onCheckedChange = {
                            onPinnedSet(tuning, it)
                        },
                        colors = IconToggleButtonDefaults.colors(
                            checkedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            checkedContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    ) {
                        Icon(
                            if (pinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (pinned) stringResource(R.string.unpin) else stringResource(R.string.pin)
                        )
                    }
                }
            }
            if (tuning is TuningEntry.InstrumentTuning && !saved) {
                item {
                    IconButton(
                        onClick = { onSave(tuning.tuning) }
                    ) {
                        Icon(
                            Icons.Default.SaveAs,
                            contentDescription = stringResource(R.string.save)
                        )
                    }
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
 */
@Composable
private fun CustomTuningItem(
    tuning: TuningEntry.InstrumentTuning,
    favourited: Boolean,
    pinned: Boolean,
    pinnedInitial: Boolean,
    onFavouriteSet: (TuningEntry, Boolean) -> Unit,
    onUnpin: () -> Unit,
    onSelect: (TuningEntry) -> Unit,
    onDelete: (Tuning) -> Unit,
) {
    val standard = remember(tuning) { tuning.tuning.equivalentTo(Tunings.STANDARD) }
    TuningItem(tuning = tuning, onSelect = onSelect) {
        if (pinned && !standard) {
            item {
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
        }
        item {
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
        item {
            FilledIconButton(
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                onClick = { onDelete(tuning.tuning) }
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete)
                )
            }
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
    tuning: TuningEntry,
    favourited: Boolean,
    pinned: Boolean,
    pinnedInitial: Boolean,
    onFavouriteSet: (TuningEntry, Boolean) -> Unit,
    onSelect: (TuningEntry) -> Unit,
    onUnpin: () -> Unit
) {
    val standard = remember(tuning) { tuning.tuning?.equivalentTo(Tunings.STANDARD) == true }
    TuningItem(tuning = tuning, onSelect = onSelect) {
        if (pinned && !standard) {
            item {
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
        }
        item {
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
}

/**
 * List item displaying a custom tuning, with support for a trailing action.
 *
 * @param tuning The tuning to display.
 * @param onSelect Called when this tuning is selected.
 * @param trailing The trailing action to display.
 */
@Composable
private fun TuningItem(
    tuning: TuningEntry,
    onSelect: (TuningEntry) -> Unit,
    trailing: (LazyListScope.() -> Unit)? = null
) {
    val name = when (tuning) {
        is TuningEntry.InstrumentTuning -> tuning.tuning.name
        is TuningEntry.ChromaticTuning -> stringResource(R.string.chromatic)
    }

    val strings = remember(tuning) {
        tuning.tuning?.strings
            ?.reversed()
            ?.joinToString(
                separator = ", ",
            ) { it.toFullString() } ?: ""
    }

    val desc = when (tuning) {
        is TuningEntry.InstrumentTuning -> strings
        is TuningEntry.ChromaticTuning -> stringResource(R.string.chromatic_desc)
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
        time = if (tuning is TuningEntry.InstrumentTuning) {
            { Text("${tuning.tuning.instrument.getLocalisedName()} ‧ ${tuning.tuning.numStrings()}" + stringResource(R.string.num_strings_suffix)) }
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
        visible = expanded && trailing != null,
        title = {
            Text(name)
        },
        verticalArrangement = Arrangement.Center,
    ) {
        item { LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            trailing?.invoke(this)
        }}
    }
}

/** @return The localised name of this instrument. */
@Composable
fun Instrument.getLocalisedName(): String {
    return stringResource(when (this) {
                              Instrument.GUITAR -> R.string.instr_guitar
                              Instrument.BASS -> R.string.instr_bass
                              Instrument.UKULELE -> R.string.instr_ukulele
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
    tuning: Tuning,
    onSave: (String?, Tuning) -> Unit,
    onDismiss: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf(tuning.name) }

    AlertDialog(
        confirmButton = {
            Button(onClick = { onSave(name.ifBlank { null }, tuning) }) {
                Text(text = stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        onDismissRequest = onDismiss,
        visible = visible,
        title = { Text(
            stringResource(R.string.dialog_title_save_tuning)
        )},
    ) {
        item {
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                it.data?.let { data ->
                    // Get all the results
                    val results: Bundle = RemoteInput.getResultsFromIntent(data)
                    // Use the inputTextKey to select the input we are interested in
                    val newInputText: CharSequence? = results.getCharSequence("tuning-name")
                    // Save the text to our variable as a string. Ensure to handle the null case
                    name = newInputText?.toString() ?: ""
                }
            }

            val intent: Intent = RemoteInputIntentHelper.createActionRemoteInputIntent()

            val remoteInputs: List<RemoteInput> = listOf(
                RemoteInput.Builder("tuning-name")
                    .setLabel(stringResource(id = R.string.dialog_title_save_tuning))
                    .wearableExtender {
                        setEmojisAllowed(true)
                        setInputActionType(EditorInfo.IME_ACTION_DONE)
                    }.build()
            )

            RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)

            CompactButton(
                onClick = {
                    // Use the launcher to launch the intent on click of a button
                    launcher.launch(intent)
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
    tuning: Tuning,
    onDelete: (Tuning) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ), onClick = { onDelete(tuning) }) {
                Text(text = stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
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

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Preview(device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Preview(device = WearDevices.SQUARE, showSystemUi = true)
@Composable
private fun Preview() {
    val currentTuning = TuningEntry.InstrumentTuning(Tunings.BASS_STANDARD.higherTuning())
    val customTuning = TuningEntry.InstrumentTuning(Tuning.fromString("E4 E3 E3 E3 E2 E2"))
    val favCustomTuning = TuningEntry.InstrumentTuning(Tuning.fromString("Custom", Instrument.GUITAR, null, "C#4 B3 F#3 D3 A2 D2"))

    AppTheme {
        AppScaffold {
            TuningSelectionScreen(
                current = currentTuning,
                currentSaved = false,
                tunings = TuningList.GROUPED_TUNINGS,
                pinned = TuningEntry.InstrumentTuning(Tunings.WHOLE_STEP_DOWN),
                pinnedInitial = true,
                favourites = setOf(TuningEntry.InstrumentTuning(Tuning.STANDARD), TuningEntry.ChromaticTuning),
                custom = setOf(customTuning, favCustomTuning),
                instrumentFilter = Instrument.BASS,
                categoryFilter = null,
                instrumentFilters = remember { mutableStateOf(Instrument.entries.dropLast(1).associateWith { true }) },
                categoryFilters = remember { mutableStateOf(Category.entries.associateWith { true }) },
                backIcon = Icons.Default.Close,
                deletedTuning = MutableSharedFlow(),
                isFavourite = { this == favCustomTuning },
                onSave = { _, _ -> },
                onFavouriteSet = { _, _ -> },
                onSelect = {},
                onDelete = {},
                onSelectInstrument = {},
                onSelectCategory = {},
                onDismiss = {},
                onPin = { _ -> },
                onUnpin = {},
            )
        }
    }
}
