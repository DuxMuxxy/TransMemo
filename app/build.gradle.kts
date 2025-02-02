import org.jmailen.gradle.kotlinter.tasks.FormatTask
import org.jmailen.gradle.kotlinter.tasks.LintTask

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.vectorize)
}

android {
    namespace = "com.chrysalide.transmemo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.chrysalide.transmemo"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.generateKotlin", "true")
        }

        androidResources {
            generateLocaleConfig = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

// Setup protobuf configuration, generating lite Java and Kotlin classes
protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.bundles.androidx.compose)
    implementation(libs.bundles.androidx.dataStore)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.kotlinx)
    implementation(libs.bundles.room)
    implementation(libs.protobuf.kotlin.lite)
    ksp(libs.room.compiler)
    implementation(libs.vectorize)

    testImplementation(libs.junit)
    testImplementation(libs.koinTest)
}

tasks.named("generateImages").configure {
    dependsOn("kspDebugKotlin")
}

tasks.withType<LintTask> {
    this.source = this.source.minus(fileTree("build/images/src/main/kotlin/dev/sergiobelda")).asFileTree
}

tasks.withType<FormatTask> {
    this.source = this.source.minus(fileTree("build/images/src/main/kotlin/dev/sergiobelda")).asFileTree
}