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

package com.rohankhayech.choona.view.components

import androidx.annotation.IntRange
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import com.rohankhayech.choona.view.components.ItemScrollPosition.Center
import com.rohankhayech.choona.view.components.ItemScrollPosition.End
import com.rohankhayech.choona.view.components.ItemScrollPosition.Start

/**
 * Enum representing the position within the viewport to which an item should be scrolled.
 * - [Start] means the item will be scrolled to the start of the viewport.
 * - [Center] means the item will be scrolled to the center of the viewport.
 * - [End] means the item will be scrolled to the end of the viewport.
 * @author Rohan Khayech
 */
@Immutable
enum class ItemScrollPosition {
    /** Item will be scrolled to the start of the viewport. */
    Start,
    /** Item will be scrolled to the center of the viewport. */
    Center,
    /** Item will be scrolled to the end of the viewport. */
    End
}

/**
 * Brings the item at [index] to the specified position within the viewport,
 * offset by [scrollOffset] pixels.
 *
 * @param index The index to which to scroll. Must be non-negative.
 * @param position The [position][ItemScrollPosition] within the viewport to which the item should be scrolled.
 *   Can be [Start][ItemScrollPosition.Start], [Center][ItemScrollPosition.Center] or [`End`][ItemScrollPosition.End].
 * @param animated Whether the scroll should be animated. If false, the scroll will be immediate. Defaults to `true`.
 * @param scrollOffset The additional offset from the specified position that the item should end up after the scroll, in pixels.
 *                     Note that positive offset refers to forward scroll, so in a top-to-bottom list, positive offset will
 *                     scroll the item further upward.
 * @param itemSize The main axis size of the item to scroll to, in pixels.
 *                 This is used to correctly position items when the item is not visible on screen.
 *                 This can be left `null` if all items are the same size.
 *                 If the list contains items of different sizes, it is recommended to provide the size of the item to avoid incorrect positioning.
 * @author Rohan Khayech
*/
suspend fun LazyListState.scrollItemToPosition(
    @IntRange(from = 0) index: Int,
    position: ItemScrollPosition,
    animated: Boolean = true,
    scrollOffset: Int = 0,
    itemSize: Int? = null
) {
    val itemSize = this.layoutInfo.visibleItemsInfo.find { item ->
        item.index == index
    }?.size ?: itemSize ?: this.layoutInfo.visibleItemsInfo[0].size

    val viewportSize = when (this.layoutInfo.orientation) {
        Orientation.Horizontal -> this.layoutInfo.viewportSize.width
        Orientation.Vertical -> this.layoutInfo.viewportSize.height
    }

    val beforeContentPadding = this.layoutInfo.beforeContentPadding
    val afterContentPadding = this.layoutInfo.afterContentPadding

    val offset = when (position) {
        ItemScrollPosition.Start -> 0
        ItemScrollPosition.Center -> (itemSize / 2) + beforeContentPadding - (viewportSize / 2)
        ItemScrollPosition.End -> itemSize + beforeContentPadding + afterContentPadding - viewportSize
    }

    if (animated) {
        this.animateScrollToItem(index, offset + scrollOffset)
    } else {
        this.scrollToItem(index, offset + scrollOffset)
    }
}

/**
 * Effect that automatically scrolls the specified item in a lazy list to the specified position
 * within the viewport when the [index] changes.
 *
 * @param index The index of the item to scroll to. Must be non-negative.
 * @param position The [position][ItemScrollPosition] within the viewport to which the item should be scrolled.
 *   Can be [Start][ItemScrollPosition.Start], [Center][ItemScrollPosition.Center] or [`End`][ItemScrollPosition.End].
 * @param animated Whether the scroll should be animated. If false, the scroll will be immediate.
 * @param scrollOffset The additional offset from the specified position that the item should end up after the scroll, in pixels.
 *                     Note that positive offset refers to forward scroll, so in a top-to-bottom list, positive offset will
 *                     scroll the item further upward.
 * @param itemSize The main axis size of the item to scroll to, in pixels.
 *                 This is used to correctly position items when the item is not visible on screen.
 *                 This can be left `null` if all items are the same size.
 *                 If the list contains items of different sizes, it is recommended to provide the size of the item to avoid incorrect positioning.
 * @see LazyListState.scrollToItem
 * @see ItemScrollPosition
 * @author Rohan Khayech
 */
@Composable
fun LazyListAutoScroll(
    listState: LazyListState,
    @IntRange(from = 0) index: Int,
    position: ItemScrollPosition,
    animated: Boolean = true,
    scrollOffset: Int = 0,
    itemSize: Int? = null
) {
    LaunchedEffect(index, position, animated, scrollOffset, itemSize) {
        listState.scrollItemToPosition(index, position, animated, scrollOffset, itemSize)
    }
}

