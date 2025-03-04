
import org.jmailen.gradle.kotlinter.tasks.FormatTask
import org.jmailen.gradle.kotlinter.tasks.LintTask

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.vectorize)
    alias(libs.plugins.transmemo.android.application)
}

android {
    namespace = "com.chrysalide.transmemo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.chrysalide.transmemo"
        minSdk = 26
        targetSdk = 35
        versionCode = 50
        versionName = "5.0"

        androidResources.generateLocaleConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
        freeCompilerArgs += "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    }
    buildFeatures.buildConfig = true
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":database"))
    implementation(project(":datastore"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.bundles.androidx.compose)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.kotlinx)
    implementation(libs.vectorize)
    implementation(libs.biometric)

    testImplementation(libs.bundles.test)
    testImplementation(libs.koinTest)
}

tasks.withType<LintTask> {
    this.source = this.source.minus(fileTree("build/images/src/main/kotlin/dev/sergiobelda")).asFileTree
}

tasks.withType<FormatTask> {
    this.source = this.source.minus(fileTree("build/images/src/main/kotlin/dev/sergiobelda")).asFileTree
}

tasks.withType<Test> {
    jvmArgs("-XX:+EnableDynamicAgentLoading")
}