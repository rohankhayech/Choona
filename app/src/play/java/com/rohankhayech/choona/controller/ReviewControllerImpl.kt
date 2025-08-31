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

package com.rohankhayech.choona.controller

import androidx.activity.ComponentActivity
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.rohankhayech.choona.model.preferences.TunerPreferences
import com.rohankhayech.choona.model.preferences.tunerPreferenceDataStore
import kotlinx.coroutines.launch

/**
 * Controller that handles launching Google Play review prompts.
 * @author Rohan Khayech
 */
class ReviewControllerImpl(private val context: ComponentActivity): ReviewController {
    /** Google Play review manager. */
    private val manager: ReviewManager = ReviewManagerFactory.create(context)

    /** Launches a prompt for the user to review the app on Google Play. */
    override fun launchReviewPrompt() {
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                manager.launchReviewFlow(context, reviewInfo)
                    .addOnCompleteListener {
                        // Increment launches counter.
                        context.lifecycleScope.launch {
                            context.tunerPreferenceDataStore.edit { prefs ->
                                prefs[TunerPreferences.REVIEW_PROMPT_LAUNCHES_KEY] = (
                                    (prefs[TunerPreferences.REVIEW_PROMPT_LAUNCHES_KEY]?.toIntOrNull() ?: 0)
                                        + 1
                                    ).toString()
                            }
                        }
                    }
            }
        }
    }
}