import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import java.io.FileInputStream
import java.util.Properties

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<ApplicationExtension> {
                val localProperties = Properties().apply {
                    val localPropertiesFile = rootProject.file("local.properties")
                    if (localPropertiesFile.exists()) load(FileInputStream(localPropertiesFile))
                }
                configureBuildTypes(this, localProperties)
            }
        }
    }

    private fun Project.configureBuildTypes(commonExtension: ApplicationExtension, localProperties: Properties) {
        commonExtension.apply {
            signingConfigs {
                create("appDistribRelease") {
                    storeFile = rootProject.file("keystore-app-distrib.jks")
                    storePassword = System.getenv("APP_DISTRIB_SIGNING_STORE_PASSWORD").takeUnless { it.isNullOrBlank() }
                        ?: localProperties.getProperty("app.distrib.signing.store.password")
                    keyAlias = System.getenv("APP_DISTRIB_SIGNING_KEY_ALIAS").takeUnless { it.isNullOrBlank() }
                        ?: localProperties.getProperty("app.distrib.signing.key.alias")
                    keyPassword = System.getenv("APP_DISTRIB_SIGNING_KEY_PASSWORD").takeUnless { it.isNullOrBlank() }
                        ?: localProperties.getProperty("app.distrib.signing.key.password")
                }
            }

            buildTypes {
                create("appDistrib") {
                    initWith(getByName("release"))
                    signingConfig = signingConfigs["appDistribRelease"]
                    isMinifyEnabled = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }

                release {
                    isMinifyEnabled = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
            }
        }
    }
}