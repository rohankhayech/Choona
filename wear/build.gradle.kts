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

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.aboutLibraries.android)
}

android {
    namespace = "com.rohankhayech.choona.wear"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.rohankhayech.choona"
        minSdk = 28
        targetSdk = 35
        versionCode = 15
        versionName = "1.6.0"
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

    useLibrary("wear-sdk")

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
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.wear.input)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.wear.material3)
    implementation(libs.compose.wear.foundation)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)

    // Utility
    implementation(libs.androidutils.layout)

    // Open Source Licenses
    implementation(libs.aboutlibraries.compose.wear.m3)

    // Testing
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)

    // Tooling / Preview
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.wear.tooling.preview)
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