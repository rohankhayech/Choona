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

import java.time.LocalDateTime
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.aboutLibraries.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization.plugin)
}

android {
    namespace = "com.rohankhayech.choona.app"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.rohankhayech.choona"
        minSdk = 24
        targetSdk = 36
        versionCode = 15
        versionName = "1.6.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BUILD_YEAR", "\"${buildYear()}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }
    }
    flavorDimensions += listOf("dist")

    productFlavors {
        create("play") {
            dimension = "dist"
            isDefault = true
        }
        create("open") {
            dimension = "dist"
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

dependencies {
    // Project
    implementation(project(":lib"))

    // Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.kotlinx.serialization)

    // Google Play
    "playImplementation"(libs.review.ktx)

    // Compose
    val composeBOM = platform(libs.compose.bom)
    implementation(composeBOM)
    implementation(libs.compose.material3)
    implementation(libs.compose.animation)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.material3.window.size)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Audio
    implementation(libs.tarsos.dsp.core)

    // Utility
    implementation(libs.androidutils.theme)
    implementation(libs.androidutils.preview)
    implementation(libs.androidutils.layout)

    // Open Source Licenses
    implementation(libs.aboutlibraries.compose.m3)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.json)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(composeBOM)
    androidTestImplementation(libs.compose.ui.test.junit4)

    // Tooling / Preview
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}

aboutLibraries {
    collect {
        // Disables fetching of "remote" funding information. Uses the API of supported source hosts
        fetchRemoteFunding = false
    }

    export {
        // Allows to exclude some fields from the generated meta data field.
        // If the class name is specified, the field is only excluded for that class; without a class name, the exclusion is global.
        excludeFields.addAll("generated", "funding", "scm", "website")
    }

    library {
        // Configure the duplication rule, to match "duplicates" with
        duplicationRule = com.mikepenz.aboutlibraries.plugin.DuplicateRule.SIMPLE
    }
}

fun buildYear(): String {
    return LocalDateTime.now().year.toString()
}