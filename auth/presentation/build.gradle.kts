plugins {
    alias(libs.plugins.runique.android.feature.ui) // Convention Plugin from "build-logic/convention"
}

android {
    namespace = "com.quadrified.auth.presentation"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.auth.domain)
}