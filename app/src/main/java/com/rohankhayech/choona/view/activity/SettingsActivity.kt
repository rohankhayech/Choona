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

package com.rohankhayech.choona.view.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohankhayech.choona.lib.model.preferences.TunerPreferences
import com.rohankhayech.choona.lib.view.activity.BaseSettingsActivity
import com.rohankhayech.choona.view.screens.AboutScreen
import com.rohankhayech.choona.view.screens.LicencesScreen
import com.rohankhayech.choona.view.screens.SettingsScreen
import com.rohankhayech.choona.view.theme.AppTheme

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
            val screen by vm.screen.collectAsStateWithLifecycle()

            AppTheme(fullBlack = prefs.useBlackTheme, dynamicColor = prefs.useDynamicColor) {
                AnimatedContent(
                    targetState = screen,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) togetherWith
                                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start)
                        } else {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) togetherWith
                                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End)
                        }
                    },
                    label = "Screen"
                ) {
                    when (it) {
                        Screen.SETTINGS -> SettingsScreen(
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
                            onAboutPressed = ::openAboutScreen,
                            onBackPressed = ::finish
                        )
                        Screen.ABOUT -> AboutScreen(
                            prefs,
                            onLicencesPressed = ::openLicencesScreen,
                            onBackPressed = ::dismissAboutScreen,
                            onReviewOptOut = vm::optOutOfReviewPrompt
                        )
                        Screen.LICENCES -> LicencesScreen(onBackPressed = ::dismissLicencesScreen)
                    }
                }
            }
        }
    }
}