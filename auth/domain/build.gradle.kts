plugins {
    alias(libs.plugins.runique.jvm.library) // Convention Plugin from "build-logic/convention"
}

dependencies {
    implementation(projects.core.domain)
}