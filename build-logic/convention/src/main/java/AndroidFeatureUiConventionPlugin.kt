import com.android.build.api.dsl.LibraryExtension
import com.quadrified.convention.addUiLayerDependencies
import com.quadrified.convention.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

// For feature related modules with presentation eg: run/auth with compose
// Check core/data/build.gradle.kts for what to replace
// Replaces things in "build.gradle" blocks => check each ".kt" file mentioned
// Registered under convention/build.gradle.kts
class AndroidFeatureUiConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                // pluginID: from convention/build.gradle.kts "gradlePlugin{...}"
                apply("runique.android.library.compose")
            }

            // Comes from "ComposeDependencies.kt"
            dependencies {
                addUiLayerDependencies(target)
            }
        }
    }
}