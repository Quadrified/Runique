package com.quadrified.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

// Created to manage logic in multiple gradle convention plugins
// Replaces "buildTypes{...}" block

// internal => used within this module
internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension<*, *, *, *, *, *>, extensionType: ExtensionType
) {
    commonExtension.run {
        // To be able to apply "builtTypes" wherever needed
        buildFeatures {
            buildConfig = true
        }

        // API_KEY => from local.properties
        val apiKey = gradleLocalProperties(rootDir, rootProject.providers).getProperty("API_KEY")

        // From ExtensionType.kt enum class => either for "(Android)APPLICATION" or "(Java/Kotlin)LIBRARY"
        when (extensionType) {
            ExtensionType.APPLICATION -> {
                extensions.configure<ApplicationExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(apiKey)
                        }
                        release {
                            configureReleaseBuildType(commonExtension, apiKey)
                        }
                    }
                }
            }

            ExtensionType.LIBRARY -> {
                extensions.configure<LibraryExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(apiKey)
                        }
                        release {
                            configureReleaseBuildType(commonExtension, apiKey)
                        }
                    }
                }
            }

            ExtensionType.DYNAMIC_FEATURE -> {
                extensions.configure<DynamicFeatureExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(apiKey)
                        }
                        release {
                            configureReleaseBuildType(commonExtension, apiKey)
                            isMinifyEnabled = false
                        }
                    }
                }
            }
        }
    }
}

// Utility function for configuring a "debug" build type
private fun BuildType.configureDebugBuildType(apiKey: String) {
    buildConfigField("String", "API_KEY", "\"$apiKey\"")
    buildConfigField("String", "BASE_URL", "\"https://runique.pl-coding.com:8080\"")
}

// Utility function for configuring a "release" build type
private fun BuildType.configureReleaseBuildType(
    commonExtension: CommonExtension<*, *, *, *, *, *>, apiKey: String
) {
    buildConfigField("String", "API_KEY", "\"$apiKey\"")
    buildConfigField("String", "BASE_URL", "\"https://runique.pl-coding.com:8080\"")

    isMinifyEnabled = true
    proguardFiles(
        commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}