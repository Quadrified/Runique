plugins {
    alias(libs.plugins.runique.jvm.library) // Convention Plugin from "build-logic/convention"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    implementation(projects.core.domain)
    implementation(projects.core.connectivity.domain)
}