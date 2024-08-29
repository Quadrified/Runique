import com.android.build.api.dsl.ApplicationExtension
import com.quadrified.convention.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

// For "Android Library" modules (for all ".application" or "Application...") with Compose
// Only for "app" module build.gradle configs
// Check app/build.gradle.kts for what to replace
// Replaces things in "composeOptions{...}" block => check each ".kt" file mentioned
// Registered under convention/build.gradle.kts
class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            // Applying all configuration from convention/AndroidApplicationConventionPlugin.kt in current plugin
            // pluginID: from convention/build.gradle.kts "gradlePlugin{...}"
            pluginManager.apply("runique.android.application")

            val extension = extensions.getByType<ApplicationExtension>()

            // Comes from AndroidCompose.kt
            configureAndroidCompose(extension)
        }
    }
}