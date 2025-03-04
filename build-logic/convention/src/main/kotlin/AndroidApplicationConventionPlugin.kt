
import com.android.build.api.dsl.ApplicationExtension
import com.chrysalide.transmemo.configureBuildTypes
import com.chrysalide.transmemo.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 35
                configureBuildTypes(this)
            }
        }
    }
}
