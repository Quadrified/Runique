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

        // For "Java/Kotlin Library" module without compose
        // For data layer
        register("androidLibrary") {
            id = "runique.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }

        // For "Java/Kotlin Library" module without compose
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
    }
}