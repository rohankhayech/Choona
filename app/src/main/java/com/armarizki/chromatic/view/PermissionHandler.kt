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

package com.armarizki.chromatic.view

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.runtime.Stable
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

 
@Stable
class PermissionHandler(
    private val activity: ComponentActivity,
    private val permission: String,
) {

     
    private val launcher = activity.registerForActivityResult(RequestPermission()) {
        _granted.update { it }
        _firstRequest.update { false }
    }

     
    private val _firstRequest = MutableStateFlow(true)

     
    val firstRequest = _firstRequest.asStateFlow()

     
    private val _granted = MutableStateFlow(checkPerm())

     
    val granted = _granted.asStateFlow()

     
    fun request() {
        if (!check() && firstRequest.value) {
            launcher.launch(permission)
        }
    }

     
    fun check(): Boolean {
        return _granted.updateAndGet { checkPerm() }
    }

     
    private fun checkPerm(): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }
}