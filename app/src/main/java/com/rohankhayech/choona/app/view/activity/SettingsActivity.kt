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

package com.rohankhayech.choona.app.view.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.rohankhayech.choona.app.view.screens.AboutScreen
import com.rohankhayech.choona.app.view.screens.LicencesScreen
import com.rohankhayech.choona.app.view.screens.SettingsScreen
import com.rohankhayech.choona.app.view.theme.AppTheme
import com.rohankhayech.choona.lib.model.preferences.TunerPreferences
import com.rohankhayech.choona.lib.view.activity.BaseSettingsActivity
import kotlinx.serialization.Serializable

/**
 * Activity that allows the user to select their preferences for the guitar tuner.
 *
 * @author Rohan Khayech
 */
class SettingsActivity : BaseSettingsActivity() {
    /** Called when the activity is created. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            enableEdgeToEdge()
        }

        // Set UI content.
        setContent {
            val prefs by vm.prefs.collectAsStateWithLifecycle(TunerPreferences())
            val backStack: NavBackStack<NavKey> = rememberNavBackStack(Screen.Settings)

            AppTheme(fullBlack = prefs.useBlackTheme, dynamicColor = prefs.useDynamicColor) {
                NavDisplay(
                    backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
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
                                onSelectStringLayout = vm::setStringLayout,
                                onEnableStringSelectSound = vm::setEnableStringSelectSound,
                                onEnableInTuneSound = vm::setEnableInTuneSound,
                                onToggleEditModeDefault = vm::toggleEditModeDefault,
                                onSetUseBlackTheme = vm::setUseBlackTheme,
                                onSetUseDynamicColor = vm::setUseDynamicColor,
                                onSelectInitialTuning = vm::setInitialTuning,
                                onAboutPressed = { backStack.add(Screen.About) },
                                onBackPressed = ::finish
                            )
                        }
                        entry<Screen.About> {
                            AboutScreen(
                                prefs,
                                onLicencesPressed = { backStack.add(Screen.Licences) },
                                onBackPressed = { backStack.removeLastOrNull() },
                                onReviewOptOut = vm::optOutOfReviewPrompt
                            )
                        }
                        entry<Screen.Licences> {
                            LicencesScreen(onBackPressed = { backStack.removeLastOrNull() })
                        }
                    }
                )
            }
        }
    }

    @Serializable
    private sealed class Screen: NavKey {
        @Serializable
        data object Settings: Screen()
        @Serializable
        data object About: Screen()
        @Serializable
        data object Licences: Screen()
    }
}