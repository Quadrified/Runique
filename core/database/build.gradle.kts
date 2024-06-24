plugins {
    alias(libs.plugins.runique.android.library)
}

android {
    namespace = "com.quadrified.core.database"
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.org.mongodb.bson)
}