import com.android.build.api.dsl.DynamicFeatureExtension
import com.quadrified.convention.ExtensionType
import com.quadrified.convention.addUiLayerDependencies
import com.quadrified.convention.configureAndroidCompose
import com.quadrified.convention.configureBuildTypes
import com.quadrified.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

// For "Android Library" modules (for all ".application" or "Application...") without Compose
// Only for "app" module build.gradle configs
// Check app/build.gradle.kts for what to replace
// Replaces things in "android{...},defaultConfig {...},compileOptions{...}, kotlinOptions{...} " block => check each ".kt" file mentioned
// Registered under convention/build.gradle.kts
class AndroidDynamicFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                // apply => pluginId
                apply("com.android.dynamic-feature")
                apply("org.jetbrains.kotlin.android")
            }

            // ApplicationExtension => since used in Android Library modules
            extensions.configure<DynamicFeatureExtension> {
                // Comes from "Kotlin.kt"
                // this => <ApplicationExtension>
                configureKotlinAndroid(this)
                configureAndroidCompose(this)

                // Comes from BuildTypes.kt
                // ExtensionType.APPLICATION => because this is applied in an Android Application lib (Not pure Java/Kotlin lib)
                configureBuildTypes(
                    commonExtension = this,
                    extensionType = ExtensionType.DYNAMIC_FEATURE
                )
            }

            dependencies {
                addUiLayerDependencies(target)
                "testImplementation"(kotlin("test"))
            }
        }
    }
}