plugins {
    `kotlin-dsl`
}

group = "com.quadrified.runique.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

// Add all "id" in "libs.versions.toml"
gradlePlugin {
    plugins {

        // For "Android (Application) Library" module without compose
        register("androidApplication") {
            id = "runique.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        // For "Android (Application) Library" module with compose
        register("androidApplicationCompose") {
            id = "runique.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }

        // For "Wear OS Library" module with compose
        register("androidApplicationWearCompose") {
            id = "runique.android.application.wear.compose"
            implementationClass = "AndroidApplicationWearComposeConventionPlugin"
        }

        // For data layer
        register("androidLibrary") {
            id = "runique.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }

        // For data layer
        register("androidLibraryCompose") {
            id = "runique.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }

        // For Presentation/UI layers
        register("androidFeatureUi") {
            id = "runique.android.feature.ui"
            implementationClass = "AndroidFeatureUiConventionPlugin"
        }

        // For Room
        register("androidRoom") {
            id = "runique.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }

        // For Dynamic Feature
        register("androidDynamicFeature") {
            id = "runique.android.dynamic.feature"
            implementationClass = "AndroidDynamicFeatureConventionPlugin"
        }

        // For pure "Java/Kotlin Library" modules
        register("jvmLibrary") {
            id = "runique.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        // For Ktor
        register("jvmKtor") {
            id = "runique.jvm.ktor"
            implementationClass = "JvmKtorConventionPlugin"
        }
    }
}