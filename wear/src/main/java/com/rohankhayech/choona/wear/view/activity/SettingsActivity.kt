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

package com.rohankhayech.choona.wear.view.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.material3.timeTextCurvedText
import androidx.wear.compose.navigation3.rememberSwipeDismissableSceneStrategy
import com.rohankhayech.choona.lib.R
import com.rohankhayech.choona.lib.model.preferences.TunerPreferences
import com.rohankhayech.choona.lib.view.activity.BaseSettingsActivity
import com.rohankhayech.choona.wear.view.screens.AboutScreen
import com.rohankhayech.choona.wear.view.screens.LicencesScreen
import com.rohankhayech.choona.wear.view.screens.SettingsScreen
import com.rohankhayech.choona.wear.view.theme.AppTheme

/**
 * Activity that allows the user to select their preferences for the guitar tuner.
 *
 * @author Rohan Khayech
 */
class SettingsActivity : BaseSettingsActivity() {
    /** Called when the activity is created. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set UI content.
        setContent {
            val prefs by vm.prefs.collectAsStateWithLifecycle(TunerPreferences())

            AppTheme(dynamicColor = prefs.useDynamicColor) {
                AppScaffold(
                    timeText = {
                        val appName = stringResource(R.string.app_name)
                        TimeText { time -> timeTextCurvedText("$appName ‧ $time") }
                    }
                ) {
                    NavDisplay(
                        backStack = vm.backStack,
                        onBack = vm::navBack,
                        sceneStrategy = rememberSwipeDismissableSceneStrategy(),
                        transitionSpec = {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) togetherWith
                                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start)
                        },
                        popTransitionSpec = {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) togetherWith
                                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End)
                        },
                        entryProvider = entryProvider {
                            entry<Screen.Settings> {
                                SettingsScreen(
                                    prefs = prefs,
                                    pinnedTuning = vm.pinnedTuning,
                                    onSelectDisplayType = vm::setDisplayType,
                                    onEnableStringSelectSound = vm::setEnableStringSelectSound,
                                    onEnableInTuneSound = vm::setEnableInTuneSound,
                                    onSetUseDynamicColor = vm::setUseDynamicColor,
                                    onSelectInitialTuning = vm::setInitialTuning,
                                    onAboutPressed = { vm.navTo(Screen.About) },
                                )
                            }
                            entry<Screen.About> {
                                AboutScreen(
                                    onLicencesPressed = { vm.navTo(Screen.Licences) },
                                )
                            }
                            entry<Screen.Licences> {
                                LicencesScreen()
                            }
                        }
                    )
                }
            }
        }
    }
}