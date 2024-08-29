import com.android.build.api.dsl.LibraryExtension
import com.quadrified.convention.ExtensionType
import com.quadrified.convention.configureBuildTypes
import com.quadrified.convention.configureKotlinAndroid
import com.quadrified.convention.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

// For pure "Java/Kotlin" modules
// Check build.gradle.kts of "domain" layers for what to replace
// Registered under convention/build.gradle.kts
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("org.jetbrains.kotlin.jvm")
            }

            // From "Kotlin.kt"
            configureKotlinJvm()
        }
    }
}