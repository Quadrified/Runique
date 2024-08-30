plugins {
    // Convention Plugin from "build-logic/convention"
    alias(libs.plugins.runique.android.library)
    alias(libs.plugins.runique.jvm.ktor)
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