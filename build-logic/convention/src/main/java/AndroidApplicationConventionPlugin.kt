import com.android.build.api.dsl.ApplicationExtension
import com.quadrified.convention.ExtensionType
import com.quadrified.convention.configureBuildTypes
import com.quadrified.convention.configureKotlinAndroid
import com.quadrified.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

// For "Android Library" modules (for all ".application" or "Application...") without Compose
// Only for "app" module build.gradle configs
// Check app/build.gradle.kts for what to replace
// Replaces things in "android{...},defaultConfig {...},compileOptions{...}, kotlinOptions{...} " block => check each ".kt" file mentioned
// Registered under convention/build.gradle.kts
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                // apply => pluginId
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            // ApplicationExtension => since used in Android Library modules
            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    // libs => comes from "ProjectExt" [4.7.1]
                    // projectApplicationId => comes from "libs.version.toml"
                    applicationId = libs.findVersion("projectApplicationId").get().toString()
                    targetSdk = libs.findVersion("projectTargetSdkVersion").get().toString().toInt()
                    versionCode = libs.findVersion("projectVersionCode").get().toString().toInt()
                    versionName = libs.findVersion("projectVersionName").get().toString()
                }

                // Comes from "Kotlin.kt"
                // this => <ApplicationExtension>
                configureKotlinAndroid(this)

                // Comes from BuildTypes.kt
                // ExtensionType.APPLICATION => because this is applied in an Android Application lib (Not pure Java/Kotlin lib)
                configureBuildTypes(
                    commonExtension = this, extensionType = ExtensionType.APPLICATION
                )
            }

        }
    }
}