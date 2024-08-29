import com.android.build.api.dsl.LibraryExtension
import com.quadrified.convention.ExtensionType
import com.quadrified.convention.configureBuildTypes
import com.quadrified.convention.configureKotlinAndroid
import com.quadrified.convention.configureKotlinJvm
import com.quadrified.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

// For Ktor serialization
// Registered under convention/build.gradle.kts
class JvmKtorConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            dependencies {
                "implementation"(libs.findBundle("ktor").get())
            }
        }
    }
}