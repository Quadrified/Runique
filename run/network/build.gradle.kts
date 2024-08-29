plugins {
    // Convention Plugin from "build-logic/convention"
    alias(libs.plugins.runique.android.library)
    alias(libs.plugins.runique.jvm.ktor)
}

android {
    namespace = "com.quadrified.run.network"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)
}