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


package com.armarizki.chromatic.view.activity

import java.io.IOException
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.armarizki.chromatic.model.preferences.InitialTuningType
import com.armarizki.chromatic.model.preferences.StringLayout
import com.armarizki.chromatic.model.preferences.TunerPreferences
import com.armarizki.chromatic.model.preferences.TuningDisplayType
import com.armarizki.chromatic.model.preferences.tunerPreferenceDataStore
import com.armarizki.chromatic.view.screens.AboutScreen
import com.armarizki.chromatic.view.screens.LicencesScreen
import com.armarizki.chromatic.view.screens.SettingsScreen
import com.armarizki.chromatic.view.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

 
class SettingsActivity : ComponentActivity() {

    companion object {
         
        const val EXTRA_PINNED = "pinned"
    }

     
    private lateinit var vm: SettingsActivityViewModel

     
    private lateinit var dismissAboutScreenOnBack: OnBackPressedCallback

     
    private lateinit var dismissLicencesScreenOnBack: OnBackPressedCallback

     
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            enableEdgeToEdge()
        }

        // Initialise view model.
        vm = ViewModelProvider(
            this,
            SettingsActivityViewModel.Factory(tunerPreferenceDataStore, intent.getStringExtra(EXTRA_PINNED) ?: "")
        )[SettingsActivityViewModel::class.java]

        // Setup custom back navigation.
        dismissAboutScreenOnBack = onBackPressedDispatcher.addCallback(this,
            enabled = vm.screen.value >= Screen.ABOUT
        ) {
            dismissAboutScreen()
        }
        dismissLicencesScreenOnBack = onBackPressedDispatcher.addCallback(this,
            enabled = vm.screen.value >= Screen.LICENCES
        ) {
            dismissLicencesScreen()
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
                            onSetA4Pitch = vm::setA4Pitch,
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

     
    private fun openAboutScreen() {
        dismissAboutScreenOnBack.isEnabled = true
        vm.setScreen(Screen.ABOUT)
    }

    private fun dismissAboutScreen() {
        dismissAboutScreenOnBack.isEnabled = false
        vm.setScreen(Screen.SETTINGS)
    }

     
    private fun openLicencesScreen() {
        dismissLicencesScreenOnBack.isEnabled = true
        vm.setScreen(Screen.LICENCES)
    }

    private fun dismissLicencesScreen() {
        dismissLicencesScreenOnBack.isEnabled = false
        vm.setScreen(Screen.ABOUT)
    }
}

 
private class SettingsActivityViewModel(
    private val dataStore: DataStore<Preferences>,
    val pinnedTuning: String
) : ViewModel() {

     
    private val _screen = MutableStateFlow(Screen.SETTINGS)

     
    val screen = _screen.asStateFlow()

     
    val prefs: Flow<TunerPreferences> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map(TunerPreferences::fromAndroidPreferences)

     
    fun setEnableStringSelectSound(enable: Boolean) {
        setPreference(TunerPreferences.ENABLE_STRING_SELECT_SOUND_KEY, enable)
    }

    fun setA4Pitch(value: Double)
    { setPreference(TunerPreferences.A4_PITCH_KEY, value)
    }

     
    fun setEnableInTuneSound(enable: Boolean) {
        setPreference(TunerPreferences.ENABLE_IN_TUNE_SOUND_KEY, enable)
    }

     
    fun toggleEditModeDefault(enable: Boolean) {
        setPreference(TunerPreferences.EDIT_MODE_DEFAULT_KEY, enable)
    }

     
    fun setDisplayType(displayType: TuningDisplayType) {
        setPreference(TunerPreferences.DISPLAY_TYPE_KEY, displayType.toString())
    }

     
    fun setStringLayout(layout: StringLayout) {
        setPreference(TunerPreferences.STRING_LAYOUT_KEY, layout.toString())
    }

     
    fun setUseBlackTheme(use: Boolean) {
        setPreference(TunerPreferences.USE_BLACK_THEME_KEY, use)
    }

     
    fun setUseDynamicColor(use: Boolean) {
        setPreference(TunerPreferences.USE_DYNAMIC_COLOR_KEY, use)
    }

     
    fun setInitialTuning(initialTuning: InitialTuningType) {
        setPreference(TunerPreferences.INITIAL_TUNING_KEY, initialTuning.toString())
    }

     
    fun optOutOfReviewPrompt() {
        setPreference(TunerPreferences.SHOW_REVIEW_PROMPT_KEY, false)
    }

     
    private fun <T> setPreference(key: Preferences.Key<T>, value: T) {
        viewModelScope.launch {
            dataStore.edit{
                it[key] = value
            }
        }
    }

     
    fun setScreen(screen: Screen) {
        _screen.update { screen }
    }

     
    class Factory(
        private val dataStore: DataStore<Preferences>,
        private val pinnedTuning: String
    ) : ViewModelProvider.Factory {

         
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsActivityViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingsActivityViewModel(dataStore, pinnedTuning) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}

private enum class Screen {
    SETTINGS,
    ABOUT,
    LICENCES
}