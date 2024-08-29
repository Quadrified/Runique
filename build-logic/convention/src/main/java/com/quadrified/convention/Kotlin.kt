package com.quadrified.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Created to manage logic in multiple gradle convention plugins
// Replaces android => defaultConfig {compileSdk, minSdk} , compileOptions{...}, kotlinOptions{...}
// Common for both "Android Application" and "Java/Kotlin Library" modules

// internal => used within this module
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    commonExtension.apply {
        compileSdk = libs.findVersion("projectCompileSdkVersion").get().toString().toInt()
        defaultConfig.minSdk = libs.findVersion("projectMinSdkVersion").get().toString().toInt()

        compileOptions {
            isCoreLibraryDesugaringEnabled = true
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }

    configureKotlin()

    dependencies {
        // coreLibraryDesugaring => mechanism that makes Java APIs backwards compatible for lower android versions
        // For ANDROID_API_LEVEL<= 21
        "coreLibraryDesugaring"(libs.findLibrary("desugar.jdk.libs").get())
    }
}

private fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            // Same version as sourceCompatibility, targetCompatibility
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }
}