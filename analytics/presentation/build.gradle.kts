plugins {
    alias(libs.plugins.runique.android.feature.ui)
}

android {
    namespace = "com.quadrified.analytics.presentation"
}

dependencies {
    implementation(projects.analytics.domain)
}