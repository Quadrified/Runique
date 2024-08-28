plugins {
    alias(libs.plugins.runique.android.library)
}

android {
    namespace = "com.quadrified.core.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.database)

    implementation(libs.timber)
    implementation(libs.androidx.monitor)
    implementation(libs.androidx.junit.ktx)
}