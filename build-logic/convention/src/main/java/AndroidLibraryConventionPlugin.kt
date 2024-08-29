import com.android.build.api.dsl.LibraryExtension
import com.quadrified.convention.ExtensionType
import com.quadrified.convention.configureBuildTypes
import com.quadrified.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

// For data related modules (for all ".library" or "Library...") eg: core/data
// Check core/data/build.gradle.kts for what to replace
// Replaces things in "build.gradle" blocks => check each ".kt" file mentioned
// Registered under convention/build.gradle.kts
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                // Comes from "Kotlin.kt"
                // this => <LibraryExtension>
                configureKotlinAndroid(this)

                // Comes from "BuildTypes.kt"
                configureBuildTypes(
                    commonExtension = this, extensionType = ExtensionType.LIBRARY
                )

                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }
            }

            dependencies {
                "testImplementation"(kotlin("test"))
            }
        }
    }
}