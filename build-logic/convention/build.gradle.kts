import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin.compilerOptions.jvmTarget = JvmTarget.JVM_17

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        create("androidApplication") {
            id = "transmemo.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        create("androidLibrary") {
            id = "transmemo.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        create("jvmLibrary") {
            id = "transmemo.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}