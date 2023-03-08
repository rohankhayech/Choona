/*
 * Copyright (c) 2023 Rohan Khayech
 */

package com.rohankhayech.choona.view.activity

import java.io.IOException
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.getValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.rohankhayech.choona.model.preferences.StringLayout
import com.rohankhayech.choona.model.preferences.TunerPreferences
import com.rohankhayech.choona.model.preferences.TuningDisplayType
import com.rohankhayech.choona.model.preferences.tunerPreferenceDataStore
import com.rohankhayech.choona.view.screens.AboutScreen
import com.rohankhayech.choona.view.screens.SettingsScreen
import com.rohankhayech.choona.view.theme.AppTheme
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Activity that allows the user to select their preferences for the guitar tuner.
 *
 * @author Rohan Khayech
 */
class SettingsActivity : AppCompatActivity() {

    /** View model used to interact with the users preferences. */
    private lateinit var vm: SettingsActivityViewModel

    /** Callback used to dismiss about screen when the back button is pressed. */
    private lateinit var dismissAboutScreenOnBack: OnBackPressedCallback

    /** Called when the activity is created. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialise view model.
        vm = ViewModelProvider(
            this,
            SettingsActivityViewModel.Factory(tunerPreferenceDataStore)
        )[SettingsActivityViewModel::class.java]

        // Setup custom back navigation.
        dismissAboutScreenOnBack = onBackPressedDispatcher.addCallback(this,
            enabled = vm.showAboutScreen.value
        ) {
            dismissAboutScreen()
        }

        // Set UI content.
        setContent {
            val prefs by vm.prefs.collectAsStateWithLifecycle(TunerPreferences())
            val showAboutScreen by vm.showAboutScreen.collectAsStateWithLifecycle()
            
            AppTheme(fullBlack = prefs.useBlackTheme) {
                AnimatedVisibility(
                    visible = !showAboutScreen,
                    enter = slideInHorizontally { -it/4 },
                    exit = slideOutHorizontally { -it/4 }
                ) {
                    SettingsScreen(
                        prefs = prefs,
                        onSelectDisplayType = vm::setDisplayType,
                        onSelectStringLayout = vm::setStringLayout,
                        onEnableStringSelectSound = vm::setEnableStringSelectSound,
                        onEnableInTuneSound = vm::setEnableInTuneSound,
                        onSetUseBlackTheme = vm::setUseBlackTheme,
                        onAboutPressed = ::openAboutScreen,
                        onBackPressed = ::finish
                    )
                }

                AnimatedVisibility(
                    visible = showAboutScreen,
                    enter = slideInHorizontally { it/4 },
                    exit = slideOutHorizontally { it/4 }
                ) {
                    AboutScreen(onBackPressed = ::dismissAboutScreen)
                }
            }
        }
    }

    /** Opens the about screen and enables custom back behaviour. */
    private fun openAboutScreen() {
        dismissAboutScreenOnBack.isEnabled = true
        vm.openAboutScreen()
    }

    private fun dismissAboutScreen() {
        dismissAboutScreenOnBack.isEnabled = false
        vm.dismissAboutScreen()
    }
}

/**
 * View model for the tuner settings activity.
 *
 * @param dataStore Data store object used to access and edit the user's preferences.
 *
 * @author Rohan Khayech
 */
class SettingsActivityViewModel(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    /** Mutable backing property for [showAboutScreen]. */
    private val _showAboutScreen = MutableStateFlow(false)

    /** Whether to show the about screen. */
    val showAboutScreen = _showAboutScreen.asStateFlow()

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

    /** Sets the preference with the specified [key] to the specified [value]. */
    private fun <T> setPreference(key: Preferences.Key<T>, value: T) {
        viewModelScope.launch {
            dataStore.edit{
                it[key] = value
            }
        }
    }

    /** Opens the about screen. */
    fun openAboutScreen() {
        _showAboutScreen.update { true }
    }

    /** Dismisses the about screen. */
    fun dismissAboutScreen() {
        _showAboutScreen.update { false }
    }

    /**
     * Factory class used to instantiate the view model with a reference to the data store.
     *
     * @param dataStore Data store object used to access and edit the user's preferences.
     */
    class Factory(
        private val dataStore: DataStore<Preferences>
    ) : ViewModelProvider.Factory {

        /** Instantiates the view model with a reference to the data store. */
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsActivityViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingsActivityViewModel(dataStore) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}