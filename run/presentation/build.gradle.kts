plugins {
    alias(libs.plugins.runique.android.feature.ui) // Convention Plugin from "build-logic/convention"
    alias(libs.plugins.mapsplatform.secrets.plugin)
}

android {
    namespace = "com.quadrified.run.presentation"
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.google.maps.android.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.timber)

    implementation(projects.core.domain)
    implementation(projects.run.domain)
    implementation(projects.core.connectivity.domain)
}