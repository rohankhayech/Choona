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

package com.rohankhayech.choona.view

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Stable
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

/**
 * The permission handler provides methods to check and request permissions and stores the permission state.
 * Allows permission to be requested once per activity but checked multiple times.
 *
 * @param activity The Android activity context.
 * @param permission The permission to request.
 *
 * @see android.Manifest.permission
 *
 * @author Rohan Khayech
 */
@Stable
class PermissionHandler(
    private val activity: ComponentActivity,
    private val permission: String,
) {

    /** Permission request launcher. */
    private val launcher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        _granted.update { it }
        _firstRequest.update { false }
    }

    /** Mutable backing property for [firstRequest]. */
    private val _firstRequest = MutableStateFlow(true)

    /** Whether this is the first time requesting the permission. */
    val firstRequest = _firstRequest.asStateFlow()

    /** Mutable backing property for [granted]. */
    private val _granted = MutableStateFlow(checkPerm())

    /** Whether the permission is currently granted. */
    val granted = _granted.asStateFlow()

    /**
     * Requests the permission if it is not already granted and it is the first time attempting to request the permission.
     * Repeated calls will have no effect.
     *
     * @see firstRequest
     */
    fun request() {
        if (!check() && firstRequest.value) {
            launcher.launch(permission)
        }
    }

    /**
     * Checks if the permission has been granted and updates the permission state.
     * @return True if the permission has been granted.
     */
    fun check(): Boolean {
        return _granted.updateAndGet { checkPerm() }
    }

    /**
     * @return True if the permission has been granted.
     */
    private fun checkPerm(): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }
}