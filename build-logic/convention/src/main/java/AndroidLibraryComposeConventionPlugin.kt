import com.android.build.api.dsl.LibraryExtension
import com.quadrified.convention.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

// For data related modules (for all ".library" or "Library...") eg: core/data
// Check core/data/build.gradle.kts for what to replace
// Replaces things in "build.gradle" blocks => check each ".kt" file mentioned
// Registered under convention/build.gradle.kts
class AndroidLibraryComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                // pluginID: from convention/build.gradle.kts "gradlePlugin{...}"
                apply("runique.android.library")
            }

            val extension = extensions.getByType<LibraryExtension>()
            configureAndroidCompose(extension)
        }
    }
}