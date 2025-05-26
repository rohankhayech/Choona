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
import com.rohankhayech.choona.model.preferences.InitialTuningType
import com.rohankhayech.choona.model.preferences.StringLayout
import com.rohankhayech.choona.model.preferences.TunerPreferences
import com.rohankhayech.choona.model.preferences.TuningDisplayType
import com.rohankhayech.choona.model.preferences.tunerPreferenceDataStore
import com.rohankhayech.choona.view.screens.AboutScreen
import com.rohankhayech.choona.view.screens.LicencesScreen
import com.rohankhayech.choona.view.screens.SettingsScreen
import com.rohankhayech.choona.view.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Activity that allows the user to select their preferences for the guitar tuner.
 *
 * @author Rohan Khayech
 */
class SettingsActivity : ComponentActivity() {

    companion object {
        /** Activity intent extra for the name of the pinned tuning. */
        const val EXTRA_PINNED = "pinned"
    }

    /** View model used to interact with the users preferences. */
    private lateinit var vm: SettingsActivityViewModel

    /** Callback used to dismiss about screen when the back button is pressed. */
    private lateinit var dismissAboutScreenOnBack: OnBackPressedCallback

    /** Callback used to dismiss licences screen when the back button is pressed. */
    private lateinit var dismissLicencesScreenOnBack: OnBackPressedCallback

    /** Called when the activity is created. */
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
                            onAboutPressed = ::openAboutScreen,
                            onBackPressed = ::finish
                        )
                        Screen.ABOUT -> AboutScreen(
                            onLicencesPressed = ::openLicencesScreen,
                            onBackPressed = ::dismissAboutScreen
                        )
                        Screen.LICENCES -> LicencesScreen(onBackPressed = ::dismissLicencesScreen)
                    }
                }
            }
        }
    }

    /** Opens the about screen and enables custom back behaviour. */
    private fun openAboutScreen() {
        dismissAboutScreenOnBack.isEnabled = true
        vm.setScreen(Screen.ABOUT)
    }

    private fun dismissAboutScreen() {
        dismissAboutScreenOnBack.isEnabled = false
        vm.setScreen(Screen.SETTINGS)
    }

    /** Opens the licences screen and enables custom back behaviour. */
    private fun openLicencesScreen() {
        dismissLicencesScreenOnBack.isEnabled = true
        vm.setScreen(Screen.LICENCES)
    }

    private fun dismissLicencesScreen() {
        dismissLicencesScreenOnBack.isEnabled = false
        vm.setScreen(Screen.ABOUT)
    }
}

/**
 * View model for the tuner settings activity.
 *
 * @param dataStore Data store object used to access and edit the user's preferences.
 * @param pinnedTuning The tuning selected to be used when the app is first opened.
 *
 * @author Rohan Khayech
 */
private class SettingsActivityViewModel(
    private val dataStore: DataStore<Preferences>,
    val pinnedTuning: String
) : ViewModel() {

    /** Mutable backing property for [screen]. */
    private val _screen = MutableStateFlow(Screen.SETTINGS)

    /** The currently visible screen. */
    val screen = _screen.asStateFlow()

    /** Flow containing the users preferences. */
    val prefs: Flow<TunerPreferences> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map(TunerPreferences::fromAndroidPreferences)

    /** Sets whether the string select sound is [enabled][enable]. */
    fun setEnableStringSelectSound(enable: Boolean) {
        setPreference(TunerPreferences.ENABLE_STRING_SELECT_SOUND_KEY, enable)
    }

    /** Sets whether the in tune sound is [enabled][enable]. */
    fun setEnableInTuneSound(enable: Boolean) {
        setPreference(TunerPreferences.ENABLE_IN_TUNE_SOUND_KEY, enable)
    }

    /** Sets whether tuning editing is [enabled][enable]. */
    fun toggleEditModeDefault(enable: Boolean) {
        setPreference(TunerPreferences.EDIT_MODE_DEFAULT_KEY, enable)
    }

    /** Sets the [type][displayType] of tuning offset value to display. */
    fun setDisplayType(displayType: TuningDisplayType) {
        setPreference(TunerPreferences.DISPLAY_TYPE_KEY, displayType.toString())
    }

    /** Sets the [layout] to display string controls. */
    fun setStringLayout(layout: StringLayout) {
        setPreference(TunerPreferences.STRING_LAYOUT_KEY, layout.toString())
    }

    /** Sets whether to [use] full black theme when in dark mode. */
    fun setUseBlackTheme(use: Boolean) {
        setPreference(TunerPreferences.USE_BLACK_THEME_KEY, use)
    }

    /** Sets whether to [use] dynamic color theme when in dark mode. */
    fun setUseDynamicColor(use: Boolean) {
        setPreference(TunerPreferences.USE_DYNAMIC_COLOR_KEY, use)
    }

    /** Sets the [initialTuning] to be used when the app is first opened. */
    fun setInitialTuning(initialTuning: InitialTuningType) {
        setPreference(TunerPreferences.INITIAL_TUNING_KEY, initialTuning.toString())
    }

    /** Sets the preference with the specified [key] to the specified [value]. */
    private fun <T> setPreference(key: Preferences.Key<T>, value: T) {
        viewModelScope.launch {
            dataStore.edit{
                it[key] = value
            }
        }
    }

    /** Sets the visible [screen]. */
    fun setScreen(screen: Screen) {
        _screen.update { screen }
    }

    /**
     * Factory class used to instantiate the view model with a reference to the data store.
     *
     * @param dataStore Data store object used to access and edit the user's preferences.
     * @param pinnedTuning The tuning selected to be used when the app is first opened.
     */
    class Factory(
        private val dataStore: DataStore<Preferences>,
        private val pinnedTuning: String
    ) : ViewModelProvider.Factory {

        /** Instantiates the view model with a reference to the data store. */
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