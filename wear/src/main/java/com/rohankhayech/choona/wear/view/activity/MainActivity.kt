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

package com.rohankhayech.choona.wear.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.material3.timeTextCurvedText
import com.rohankhayech.choona.lib.R
import com.rohankhayech.choona.lib.model.preferences.TunerPreferences
import com.rohankhayech.choona.lib.model.tuning.TuningEntry
import com.rohankhayech.choona.lib.view.activity.BaseSettingsActivity
import com.rohankhayech.choona.lib.view.activity.BaseTunerActivity
import com.rohankhayech.choona.wear.view.screens.MainLayout
import com.rohankhayech.choona.wear.view.screens.PermissionScreen
import com.rohankhayech.choona.wear.view.theme.AppTheme

class MainActivity : BaseTunerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            val granted by ph.granted.collectAsStateWithLifecycle()
            AppTheme {
                AppScaffold(
                    timeText = {
                        val appName = stringResource(R.string.app_name)
                        TimeText { time -> timeTextCurvedText("$appName ‧ $time") }
                    }
                ) {
                    if (granted) {
                        val prefs by prefs.collectAsStateWithLifecycle(initialValue = TunerPreferences())
                        // Collect state.
                        val tuning by vm.tuner.tuning.collectAsStateWithLifecycle()
                        val noteOffset = vm.tuner.noteOffset.collectAsStateWithLifecycle()
                        val selectedString by vm.tuner.selectedString.collectAsStateWithLifecycle()
                        val selectedNote by vm.tuner.selectedNote.collectAsStateWithLifecycle()
                        val autoDetect by vm.tuner.autoDetect.collectAsStateWithLifecycle()
                        val chromatic by vm.tuner.chromatic.collectAsStateWithLifecycle()
                        val tuned by vm.tuner.tuned.collectAsStateWithLifecycle()
                        val noteTuned by vm.tuner.noteTuned.collectAsStateWithLifecycle()
                        val tuningSelectorOpen by vm.tuningSelectorOpen.collectAsStateWithLifecycle()
                        val configurePanelOpen by vm.configurePanelOpen.collectAsStateWithLifecycle()
                        val favTunings = vm.tuningList.favourites.collectAsStateWithLifecycle()

                        MainLayout (
                            tuning = if (chromatic) TuningEntry.ChromaticTuning else TuningEntry.InstrumentTuning(tuning),
                            noteOffset = noteOffset,
                            selectedString = selectedString,
                            selectedNote = selectedNote,
                            tuned = tuned,
                            noteTuned = noteTuned,
                            autoDetect = autoDetect,
                            chromatic = chromatic,
                            favTunings = favTunings,
                            getCanonicalName = vm.tuningList::getCanonicalName,
                            prefs = prefs,
                            tuningList = vm.tuningList,
                            tuningSelectorOpen = tuningSelectorOpen,
                            configurePanelOpen = configurePanelOpen,
                            onSelectString = ::selectString,
                            onSelectNote = ::selectNote,
                            onTuneUpString = vm.tuner::tuneStringUp,
                            onTuneDownString = vm.tuner::tuneStringDown,
                            onTuneUpTuning = vm.tuner::tuneUp,
                            onTuneDownTuning = vm.tuner::tuneDown,
                            onAutoChanged = vm.tuner::setAutoDetect,
                            onTuned = ::setTuned,
                            onOpenTuningSelector = ::openTuningSelector,
                            onOpenConfigurePanel = ::openConfigurePanel,
                            onSelectTuning = ::selectTuning,
                            onSelectChromatic = ::selectChromatic,
                            onDismissTuningSelector = ::dismissTuningSelector,
                            onDismissConfigurePanel = ::dismissConfigurePanel,
                            onSettingsPressed = ::openSettings
                        )
                    } else {
                        val firstRequest by ph.firstRequest.collectAsStateWithLifecycle()
                        PermissionScreen(
                            canRequest = firstRequest,
                            onRequestPermission = ph::request,
                            onOpenPermissionSettings = ::openPermissionSettings
                        )
                    }
                }
            }
        }
    }

    /** Opens the tuner settings activity. */
    private fun openSettings() {
        val pinnedName = when (val pinned = vm.tuningList.pinned.value) {
            is TuningEntry.InstrumentTuning -> pinned.tuning.fullName
            is TuningEntry.ChromaticTuning -> getString(R.string.chromatic)
        }

        val intent = Intent(this, SettingsActivity::class.java)
        intent.putExtra(BaseSettingsActivity.EXTRA_PINNED, pinnedName)
        startActivity(intent)
    }
}