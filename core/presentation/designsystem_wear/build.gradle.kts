plugins {
    alias(libs.plugins.runique.android.library.compose) // Convention Plugin from "build-logic/convention"
}

android {
    namespace = "com.quadrified.core.presentation.designsystem_wear"

    defaultConfig {
        minSdk = 30
    }
}

dependencies {
    api(projects.core.presentation.designsystem)

    implementation(libs.androidx.wear.compose.material)
}