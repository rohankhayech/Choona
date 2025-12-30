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

package com.rohankhayech.choona.app.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.SaveAs
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohankhayech.android.util.ui.preview.ThemePreview
import com.rohankhayech.android.util.ui.theme.StatusBarColor
import com.rohankhayech.android.util.ui.theme.StatusBarIconColor
import com.rohankhayech.android.util.ui.theme.m3.isLight
import com.rohankhayech.android.util.ui.theme.m3.isTrueDark
import com.rohankhayech.choona.app.view.components.SectionLabel
import com.rohankhayech.choona.app.view.theme.AppTheme
import com.rohankhayech.choona.lib.R
import com.rohankhayech.choona.lib.controller.tunings.TuningList
import com.rohankhayech.choona.lib.model.tuning.Instrument
import com.rohankhayech.choona.lib.model.tuning.Tuning
import com.rohankhayech.choona.view.util.getLocalisedName
import com.rohankhayech.choona.lib.model.tuning.Tuning.Category
import com.rohankhayech.choona.lib.model.tuning.TuningEntry
import com.rohankhayech.choona.lib.model.tuning.Tunings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

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
fun TuningSelectionScreen(
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
@OptIn(ExperimentalMaterial3Api::class)
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
    val listState = rememberLazyListState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showSaveDialog by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Collect deleted tuning events and show snackbar.
    val message = stringResource(R.string.deleted_tuning)
    val actionLabel = stringResource(R.string.undo)
    LaunchedEffect(deletedTuning, message, actionLabel, snackbarHostState) {
        deletedTuning.collectLatest {
            // Show deleted tuning snackbar.
            val result = snackbarHostState.showSnackbar(
                message = String.format(message, it.fullName),
                actionLabel = actionLabel,
                duration = SnackbarDuration.Long
            )
            // Undo action
            if (result == SnackbarResult.ActionPerformed) onSave(it.name, it)
        }
    }

    Scaffold(
        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            StatusBarColor(if (MaterialTheme.isLight) StatusBarIconColor.DARK else StatusBarIconColor.LIGHT)
            TopAppBar(
                title = { Text(if (backIcon == null) stringResource(R.string.tunings) else stringResource(R.string.select_tuning)) },
                navigationIcon = { backIcon?.let {
                    IconButton(onClick = onDismiss) {
                        Icon(it, stringResource(R.string.dismiss))
                    }
                }},
                colors = if (!MaterialTheme.isLight && MaterialTheme.isTrueDark) {
                    TopAppBarDefaults.topAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.background)
                } else {
                    TopAppBarDefaults.topAppBarColors()
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { padding ->
        TuningList(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
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
            onDelete = onDelete,
            onPin = onPin,
            onUnpin = onUnpin
        )
    }

    // Save dialog.
    if (showSaveDialog) {
        if (current?.tuning != null) {
            SaveTuningDialog(
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
    listState: LazyListState = rememberLazyListState(),
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

    LazyColumn(modifier = modifier, state = listState) {
        // Current Tuning
        current?.let {
            item("cur") {
                SectionTitle(
                    stringResource(R.string.tuning_list_current),
                    Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
                )
            }
            item("cur-${current.key}") {
                CurrentTuningItem(
                    tuning = current,
                    saved = currentSaved,
                    pinned = currentPinned,
                    pinnedInitial = pinnedInitial,
                    onSave = onSave,
                    onSelect = onSelect,
                    onPinnedSet = { tuning, pinned ->
                        if (pinned) onPin(tuning) else onUnpin()
                    })
            }
        }

        if (pinnedInitial && !currentPinned && !pinnedInFavs && !pinnedIsStandard) {
            item("pinned") { SectionTitle(stringResource(R.string.tuning_list_pinned)) }
            item("pinned-${pinned.key}"
            ) {
                FavouritableTuningItem(tuning = pinned, favourited = false, pinned = true, pinnedInitial = true, onFavouriteSet = onFavouriteSet, onSelect = onSelect, onUnpin = onUnpin)
            }
        }

        // Favourite Tunings
        if (favourites.isNotEmpty()) {
            item("favs") { SectionTitle(stringResource(R.string.tuning_list_favourites), Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) }
            items(favsList, key = { "fav-${it.key}" }) {
                val isPinned = remember(pinned) { it == pinned || it.tuning?.equivalentTo(pinned.tuning) == true }
                FavouritableTuningItem(tuning = it, favourited = true, pinned = isPinned, pinnedInitial = pinnedInitial, onFavouriteSet = onFavouriteSet, onSelect = onSelect, onUnpin = onUnpin)
            }
        }

        // Custom Tunings
        if (custom.isNotEmpty()) {
            item("cus") { SectionTitle(stringResource(R.string.tuning_list_custom), Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) }
            items(customList, key = { it.key }) {
                val favourited = it.isFavourite()
                val isPinned = remember(pinned) { it.tuning.equivalentTo(pinned.tuning) }
                CustomTuningItem(tuning = it, favourited = favourited, pinned = isPinned, pinnedInitial = pinnedInitial, onFavouriteSet = onFavouriteSet, onUnpin = onUnpin, onSelect = onSelect, onDelete = onDelete)
            }
        }

        // All Tunings
        item("all") {
            SectionTitle(stringResource(R.string.all_tunings), Modifier.windowInsetsPadding(WindowInsets.safeDrawing))
        }
        stickyHeader("filter-bar") {
            var stuck by remember { mutableStateOf(false) }
            Surface(
                color = if (stuck && (MaterialTheme.isLight || !MaterialTheme.isTrueDark)) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .onGloballyPositioned {
                        stuck = it.positionInParent().y == 0f
                    }
            ) {
                FilterBar(instrumentFilter, categoryFilter, instrumentFilters, categoryFilters, onSelectInstrument, onSelectCategory)
            }
        }

        tunings.forEach { group ->
            item(group.toString()) {
                SectionTitle("${group.key.first.getLocalisedName()} ‧ ${group.key.second.getLocalisedName()}", Modifier.windowInsetsPadding(WindowInsets.safeDrawing))
            }
            items(group.value, key = { it.key }) {
                val favourited = it.isFavourite()
                val isPinned = remember(pinned) { it.tuning?.equivalentTo(pinned.tuning) == true }
                FavouritableTuningItem(tuning = it, favourited = favourited, pinned = isPinned, pinnedInitial = pinnedInitial, onFavouriteSet = onFavouriteSet, onSelect = onSelect, onUnpin = onUnpin)
            }
        }
        if (instrumentFilter == null && (categoryFilter == null || categoryFilter == Category.MISC)) {
            item(Category.MISC.toString()) {
                SectionTitle(Category.MISC.getLocalisedName(), Modifier.windowInsetsPadding(WindowInsets.safeDrawing))
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
    Column {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.safeDrawing),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(8.dp))
        instrumentFilters.value.forEach { filter ->
            TuningFilterChip(
                filter = filter.key,
                filterText = filter.key.getLocalisedName(),
                enabled = filter.value,
                selected = instrumentFilter == filter.key,
                onSelect = onSelectInstrument
            )
        }

        Spacer(Modifier.width(8.dp))
    }
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.safeDrawing),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(8.dp))
        categoryFilters.value.forEach { filter ->
            TuningFilterChip(
                filter = filter.key,
                filterText = filter.key.getLocalisedName(),
                enabled = filter.value,
                selected = categoryFilter == filter.key,
                onSelect = onSelectCategory
            )
        }
        Spacer(Modifier.width(8.dp))
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
    FilterChip(
        enabled = enabled,
        selected = selected,
        onClick = { if (enabled) if (selected) onSelect(null) else onSelect(filter) },
        leadingIcon = if (selected) {{
            Row {
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.Done, null, Modifier.size(FilterChipDefaults.IconSize))
            }
        }} else null,
        modifier = Modifier.animateContentSize(),
        label = {
            Text(filterText)
        }
    )
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
private fun LazyItemScope.CurrentTuningItem(
    tuning: TuningEntry,
    saved: Boolean,
    pinned: Boolean,
    pinnedInitial: Boolean,
    onSave: (Tuning) -> Unit,
    onSelect: (TuningEntry) -> Unit,
    onPinnedSet: (TuningEntry, Boolean) -> Unit
) {
    TuningItem(
        tuning = tuning,
        onSelect = onSelect,
        trailing = {
            val standard = remember(tuning) { tuning.tuning?.equivalentTo(Tunings.STANDARD) == true }
            Row {
                AnimatedVisibility(!standard && (pinned || (saved && pinnedInitial)), enter = fadeIn(), exit = fadeOut()) {
                    IconToggleButton(
                        enabled = pinnedInitial,
                        checked = pinned,
                        onCheckedChange = {
                            onPinnedSet(tuning, it)
                        }
                    ) {
                        val tint = if (pinned) MaterialTheme.colorScheme.secondary else LocalContentColor.current
                        Icon(
                            if (pinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            tint = if (pinnedInitial) tint else LocalContentColor.current.copy(alpha = 0.38f),
                            contentDescription = if (pinned) stringResource(R.string.unpin) else stringResource(R.string.pin)
                        )
                    }
                }

                if (tuning is TuningEntry.InstrumentTuning && !saved) {
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
        }
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
private fun LazyItemScope.CustomTuningItem(
    tuning: TuningEntry.InstrumentTuning,
    favourited: Boolean,
    pinned: Boolean,
    pinnedInitial: Boolean,
    onFavouriteSet: (TuningEntry, Boolean) -> Unit,
    onUnpin: () -> Unit,
    onSelect: (TuningEntry) -> Unit,
    onDelete: (Tuning) -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        modifier = Modifier.animateItem(),
        state = dismissState,
        enableDismissFromStartToEnd = false,
        onDismiss = {
            onDelete(tuning.tuning)
        },
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surfaceContainer
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
                    tint = when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.onErrorContainer
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    ) {
        FavouritableTuningItem(
            tuning = tuning,
            favourited = favourited,
            pinned = pinned,
            pinnedInitial = pinnedInitial,
            onFavouriteSet = onFavouriteSet,
            onUnpin = onUnpin,
            onSelect = onSelect
        )
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
private fun LazyItemScope.FavouritableTuningItem(
    tuning: TuningEntry,
    favourited: Boolean,
    pinned: Boolean,
    pinnedInitial: Boolean,
    onFavouriteSet: (TuningEntry, Boolean) -> Unit,
    onSelect: (TuningEntry) -> Unit,
    onUnpin: () -> Unit
) {
    TuningItem(tuning = tuning, onSelect = onSelect) {
        val standard = remember(tuning) { tuning.tuning?.equivalentTo(Tunings.STANDARD) == true }
        Row {
            AnimatedVisibility(pinned && !standard, enter = fadeIn(), exit = fadeOut()) {
                IconToggleButton(
                    enabled = pinnedInitial,
                    checked = true,
                    onCheckedChange = { onUnpin() }
                ) {
                    Icon(
                        Icons.Default.PushPin,
                        tint = if (pinnedInitial) MaterialTheme.colorScheme.secondary else LocalContentColor.current.copy(alpha = 0.38f),
                        contentDescription = stringResource(R.string.unpin)
                    )
                }
            }
            IconToggleButton(
                checked = favourited,
                onCheckedChange = { onFavouriteSet(tuning, !favourited) }
            ) {
                Icon(
                    if (favourited) Icons.Default.Star else Icons.Default.StarOutline,
                    tint = if (favourited) MaterialTheme.colorScheme.tertiary else LocalContentColor.current,
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
private fun LazyItemScope.TuningItem(
    tuning: TuningEntry,
    onSelect: (TuningEntry) -> Unit,
    trailing: (@Composable () -> Unit)? = null
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

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.animateItem()
    ) {
        Column(Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))) {
            ListItem(
                headlineContent = { Text(name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                supportingContent = { Text(desc) },
                overlineContent = if (tuning is TuningEntry.InstrumentTuning) {{ Text("${tuning.tuning.instrument.getLocalisedName()} ‧ ${tuning.tuning.numStrings()}" + stringResource(R.string.num_strings_suffix)) }} else null,
                modifier = Modifier.clickable { onSelect(tuning) },
                trailingContent = trailing
            )

            HorizontalDivider()
        }
    }
}

/** @return The localised name of this category. */
@Composable
fun Category?.getLocalisedName(): String {
    return stringResource(when (this) {
        Category.COMMON -> R.string.tun_cat_common
        Category.POWER -> R.string.tun_cat_power
        Category.OPEN -> R.string.tun_cat_open
        Category.EXTENDED -> R.string.tun_cat_extended
        else -> R.string.tun_cat_misc
    })
}

/** UI component displaying a tuning category label with [title] text. */
@Composable
private fun LazyItemScope.SectionTitle(title: String, modifier: Modifier = Modifier) {
    SectionLabel(modifier = modifier.animateItem(), title = title)
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
                    style = MaterialTheme.typography.titleMedium
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
            Button(onClick = { onSave(name.ifBlank { null }, tuning) }) {
                Text(text = stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        onDismissRequest = onDismiss
    )
}

// Previews

@ThemePreview
@Composable
private fun Preview() {
    val currentTuning = TuningEntry.InstrumentTuning(Tunings.BASS_STANDARD.higherTuning())
    val customTuning = TuningEntry.InstrumentTuning(Tuning.fromString("E4 E3 E3 E3 E2 E2"))
    val favCustomTuning = TuningEntry.InstrumentTuning(Tuning.fromString("Custom", Instrument.GUITAR, null, "C#4 B3 F#3 D3 A2 D2"))

    AppTheme {
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
            instrumentFilters = remember { mutableStateOf(
                Instrument
                    .entries.dropLast(1).associateWith { true }) },
            categoryFilters = remember { mutableStateOf(Category.entries.associateWith { true }) },
            backIcon = Icons.Default.Close,
            deletedTuning = MutableSharedFlow(),
            isFavourite = { this == favCustomTuning },
            onSave = {_,_->},
            onFavouriteSet = {_,_ ->},
            onSelect = {},
            onDelete = {},
            onSelectInstrument = {},
            onSelectCategory = {},
            onDismiss = {},
            onPin = {_ -> },
            onUnpin = {},
        )
    }
}

@ThemePreview
@Composable
private fun SaveDialogPreview() {
    val customTuning = Tuning.fromString("Custom", Instrument.GUITAR, null, "C#4 B3 F#3 D3 A2 D2")

    AppTheme {
        SaveTuningDialog(tuning = customTuning, onSave = {_,_->}) {}
    }
}