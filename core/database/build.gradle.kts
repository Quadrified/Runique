plugins {
    // Convention Plugin from "build-logic/convention"
    alias(libs.plugins.runique.android.library)
    alias(libs.plugins.runique.android.room)
}

android {
    namespace = "com.quadrified.core.database"
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.org.mongodb.bson)
}