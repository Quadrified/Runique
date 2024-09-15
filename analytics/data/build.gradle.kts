plugins {
    alias(libs.plugins.runique.android.library)
    alias(libs.plugins.runique.android.room)
}

android {
    namespace = "com.quadrified.analytics.data"
}

dependencies {
    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.analytics.domain)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.bundles.koin)
}