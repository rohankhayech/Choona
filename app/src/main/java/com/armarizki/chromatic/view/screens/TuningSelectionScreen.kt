/*
 * ChromaticTuner - Arma Rizki
 *
 * Includes modified source code from Choona Guitar Tuner
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

package com.armarizki.chromatic.view.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohankhayech.android.util.ui.preview.ThemePreview
import com.rohankhayech.android.util.ui.theme.StatusBarColor
import com.rohankhayech.android.util.ui.theme.StatusBarIconColor
import com.rohankhayech.android.util.ui.theme.m3.isLight
import com.rohankhayech.android.util.ui.theme.m3.isTrueDark
import com.armarizki.chromatic.R
import com.armarizki.chromatic.model.tuning.TuningEntry
import com.armarizki.chromatic.model.tuning.TuningList
import com.armarizki.chromatic.model.tuning.Tunings
import com.armarizki.chromatic.view.theme.AppTheme
import com.armarizki.music.Instrument
import com.armarizki.music.Tuning
import com.armarizki.music.Tuning.Category
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest


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
    val context = LocalContext.current
    LaunchedEffect(deletedTuning, context, snackbarHostState) {
        deletedTuning.collectLatest {
            // Show deleted tuning snackbar.
            val result = snackbarHostState.showSnackbar(
                message = context.getString(R.string.deleted_tuning, it.fullName),
                actionLabel = context.getString(R.string.undo),
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
                SectionLabel(
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
            item("pinned") { SectionLabel(stringResource(R.string.tuning_list_pinned)) }
            item("pinned-${pinned.key}"
            ) {
                FavouritableTuningItem(tuning = pinned, favourited = false, pinned = true, pinnedInitial = true, onFavouriteSet = onFavouriteSet, onSelect = onSelect, onUnpin = onUnpin)
            }
        }

        // Favourite Tunings
        if (favourites.isNotEmpty()) {
            item("favs") { SectionLabel(stringResource(R.string.tuning_list_favourites), Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) }
            items(favsList, key = { "fav-${it.key}" }) {
                val isPinned = remember(pinned) { it == pinned || it.tuning?.equivalentTo(pinned.tuning) == true }
                FavouritableTuningItem(tuning = it, favourited = true, pinned = isPinned, pinnedInitial = pinnedInitial, onFavouriteSet = onFavouriteSet, onSelect = onSelect, onUnpin = onUnpin)
            }
        }

        // Custom Tunings
        if (custom.isNotEmpty()) {
            item("cus") { SectionLabel(stringResource(R.string.tuning_list_custom), Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) }
            items(customList, key = { it.key }) {
                val favourited = it.isFavourite()
                val isPinned = remember(pinned) { it.tuning.equivalentTo(pinned.tuning) }
                CustomTuningItem(tuning = it, favourited = favourited, pinned = isPinned, pinnedInitial = pinnedInitial, onFavouriteSet = onFavouriteSet, onUnpin = onUnpin, onSelect = onSelect, onDelete = onDelete)
            }
        }

        // All Tunings
        item("all") {
            SectionLabel(stringResource(R.string.all_tunings), Modifier.windowInsetsPadding(WindowInsets.safeDrawing))
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
                SectionLabel("${group.key.first.getLocalisedName()} ‧ ${group.key.second.getLocalisedName()}", Modifier.windowInsetsPadding(WindowInsets.safeDrawing))
            }
            items(group.value, key = { it.key }) {
                val favourited = it.isFavourite()
                val isPinned = remember(pinned) { it.tuning?.equivalentTo(pinned.tuning) == true }
                FavouritableTuningItem(tuning = it, favourited = favourited, pinned = isPinned, pinnedInitial = pinnedInitial, onFavouriteSet = onFavouriteSet, onSelect = onSelect, onUnpin = onUnpin)
            }
        }
        if (instrumentFilter == null && (categoryFilter == null || categoryFilter == Category.MISC)) {
            item(Category.MISC.toString()) {
                SectionLabel(Category.MISC.getLocalisedName(), Modifier.windowInsetsPadding(WindowInsets.safeDrawing))
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
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete(tuning.tuning)
                true
            } else false
        }
    )

    SwipeToDismissBox(
        modifier = Modifier.animateItem(),
        state = dismissState,
        enableDismissFromStartToEnd = false,
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

 
@Composable
fun Instrument.getLocalisedName(): String {
    return stringResource(
        when (this) {
            Instrument.GUITAR -> R.string.instr_guitar
            Instrument.BASS -> R.string.instr_bass
        }
    )
}

 
@Composable
fun Category?.getLocalisedName(): String {
    return stringResource(when (this) {
        Category.COMMON -> R.string.tun_cat_common
        Category.POWER -> R.string.tun_cat_power
        Category.OPEN -> R.string.tun_cat_open
        else -> R.string.tun_cat_misc
    })
}

 
@Composable
private fun LazyItemScope.SectionLabel(
    title: String,
    modifier: Modifier = Modifier
) {
    SectionLabelBase(
        title = title,
        modifier = modifier.animateItem()
    )
}

@Composable
private fun SectionLabelBase(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

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
