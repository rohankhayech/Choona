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

package com.rohankhayech.choona.lib.model.tuning;

import androidx.annotation.Nullable;
import androidx.compose.runtime.Immutable;

import java.util.Objects;

@Immutable
public abstract class SuperTuning {
    /** The standard name of this tuning. */
    @Nullable protected final String name;

    /** The category of tuning. */
    @Nullable protected final Category category;

    protected SuperTuning(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    /** @return True if this tuning is named, false otherwise. */
    public boolean hasName() {
        return name != null;
    }

    /** @return True if this tuning has a category, false otherwise. */
    public boolean hasCategory() {
        return category != null;
    }

    /** @return The category of tuning. */
    public Category getCategory() { return category; }

    /**
     * @return The standard name of this tuning, or the string representation if it is not named.
     */
    public String getName() {
        return name != null ? name : toString();
    }

    /**
     * @return The standard name of this tuning (if named) including it's string representation.
     */
    public String getFullName() {
        return name != null ? name + " (" + this + ")" : toString();
    }

    /** @return The tuning's key for use in lists. */
    abstract String getKey();

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof SuperTuning)) return false;

        InstrumentTuning o = (InstrumentTuning)obj;
        return Objects.equals(name, o.name)
            && category == o.category;
    }

    /**
     * Enum describing tuning categories.
     */
    public enum Category {
        /** Common tuning. */
        COMMON,
        /** Power chord tuning. */
        POWER,
        /** Open chord tuning. */
        OPEN,
        /** Miscellaneous tuning. */
        MISC
    }
}
