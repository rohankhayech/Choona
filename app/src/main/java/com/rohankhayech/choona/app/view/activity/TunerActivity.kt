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

package com.rohankhayech.choona.app.view.activity

import kotlin.random.Random
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohankhayech.choona.app.BuildConfig
import com.rohankhayech.choona.app.controller.play.ReviewController
import com.rohankhayech.choona.app.controller.play.ReviewControllerImpl
import com.rohankhayech.choona.app.view.screens.MainLayout
import com.rohankhayech.choona.app.view.theme.AppTheme
import com.rohankhayech.choona.lib.R
import com.rohankhayech.choona.lib.model.preferences.TunerPreferences
import com.rohankhayech.choona.lib.model.preferences.TunerPreferences.Companion.REVIEW_PROMPT_ATTEMPTS
import com.rohankhayech.choona.lib.model.tuning.TuningEntry
import com.rohankhayech.choona.lib.view.activity.BaseSettingsActivity
import com.rohankhayech.choona.lib.view.activity.BaseTunerActivity
import kotlinx.coroutines.delay

/**
 * Activity that allows the user to select a tuning and tune their guitar, displaying a comparison of played notes
 * and the correct notes of the strings in the tuning.
 *
 * @author Rohan Khayech
 */
class TunerActivity : BaseTunerActivity() {

    /** Google Play review controller. */
    private lateinit var reviewController: ReviewController

    /**
     * Called when activity is created.
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            enableEdgeToEdge()
        }

        reviewController = ReviewControllerImpl(this)

        // Set UI content.
        setContent {
            val prefs by prefs.collectAsStateWithLifecycle(initialValue = TunerPreferences())

            AppTheme(fullBlack = prefs.useBlackTheme, dynamicColor = prefs.useDynamicColor) {
                // Collect state.
                val granted by ph.granted.collectAsStateWithLifecycle()
                val firstRequest by ph.firstRequest.collectAsStateWithLifecycle()
                val error by vm.tuner.error.collectAsStateWithLifecycle()
                val tuning by vm.tuner.tuning.collectAsStateWithLifecycle()
                val noteOffset = vm.tuner.noteOffset.collectAsStateWithLifecycle()
                val selectedString by vm.tuner.selectedString.collectAsStateWithLifecycle()
                val selectedNote by vm.tuner.selectedNote.collectAsStateWithLifecycle()
                val autoDetect by vm.tuner.autoDetect.collectAsStateWithLifecycle()
                val chromatic by vm.tuner.chromatic.collectAsStateWithLifecycle()
                val tuned by vm.tuner.tuned.collectAsStateWithLifecycle()
                val noteTuned by vm.tuner.noteTuned.collectAsStateWithLifecycle()
                val favTunings = vm.tuningList.favourites.collectAsStateWithLifecycle()
                val editModeEnabled by vm.editModeEnabled.collectAsStateWithLifecycle()

                // Calculate window size/orientation
                val windowSizeClass = calculateWindowSizeClass(this)

                val compact = remember(windowSizeClass) {
                    windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact &&
                        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
                }

                val expanded = remember(windowSizeClass) {
                    windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded &&
                        windowSizeClass.heightSizeClass > WindowHeightSizeClass.Compact
                }

                // Dismiss configure panel if no longer compact.
                LaunchedEffect(compact) {
                    if (!compact) vm.dismissConfigurePanel()
                }

                // Open tuning selector when switching to expanded view.
                LaunchedEffect(expanded) {
                    vm.setExpanded(expanded)
                    if (granted && error == null) {
                        if (expanded) {
                            checkAndStartTuner()
                            vm.openTuningSelector()
                        } else {
                            checkAndStopTuner()
                        }
                    }
                }

                // Launch review prompt after tuning if conditions met.
                @Suppress("KotlinConstantConditions")
                if (BuildConfig.FLAVOR == "play" && granted) {
                    var askedForReview by rememberSaveable { mutableStateOf(false) }
                    LaunchedEffect(tuned, askedForReview, prefs.showReviewPrompt, prefs.reviewPromptLaunches) {
                        if (
                            !askedForReview  // Only ask once per app session.
                            && prefs.showReviewPrompt  // Do not ask if user has disabled.
                            && prefs.reviewPromptLaunches < REVIEW_PROMPT_ATTEMPTS // Only ask a maximum of 3 times.
                            && tuned.all { it } // Only ask once all strings are in tune, as the user is likely finished using the app and satisfied.
                            && Random.nextDouble() < REVIEW_PROMPT_CHANCE // Only ask 30% of the time.
                        ) {
                            delay(1000)
                            launchReviewPrompt()
                            askedForReview = true
                        }
                    }
                }

                // Display UI content.
                MainLayout(
                    backStack = vm.backStack,
                    windowSizeClass = windowSizeClass,
                    granted = granted,
                    compact = compact,
                    expanded = expanded,
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
                    error = error,
                    onSelectString = ::selectString,
                    onSelectTuning = ::setTuning,
                    onSelectChromatic = vm.tuner::setChromatic,
                    onSelectNote = ::selectNote,
                    onTuneUpString = vm.tuner::tuneStringUp,
                    onTuneDownString = vm.tuner::tuneStringDown,
                    onTuneUpTuning = vm.tuner::tuneUp,
                    onTuneDownTuning = vm.tuner::tuneDown,
                    onAutoChanged = vm.tuner::setAutoDetect,
                    onTuned = ::setTuned,
                    onOpenTuningSelector = ::openTuningSelector,
                    onSettingsPressed = ::openSettings,
                    onConfigurePressed = ::openConfigurePanel,
                    onSelectTuningFromList = ::selectTuningFromList,
                    onSelectChromaticFromList = ::selectChromaticFromList,
                    onOpenTuningEditor = vm::openTuningEditor,
                    onSaveTuningFromEditor = vm::onSaveFromEditor,
                    onDeleteTuningFromEditor = vm::onDeleteFromEditor,
                    onPressNote = ::playNote,
                    onBack = ::navBack,
                    onEditModeChanged = vm::setEditMode,
                    editModeEnabled = editModeEnabled,
                    canRequest = firstRequest,
                    onRequestPermission = ph::request,
                    onOpenPermissionSettings = ::openPermissionSettings
                )
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

    /** Launches a prompt for the user to review the app on Google Play. */
    private fun launchReviewPrompt() {
        reviewController.launchReviewPrompt()
    }
}

/** Probability of showing the review prompt once all strings are in tune. */
private const val REVIEW_PROMPT_CHANCE = 0.3
