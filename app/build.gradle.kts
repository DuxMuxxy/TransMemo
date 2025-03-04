
import org.jmailen.gradle.kotlinter.tasks.FormatTask
import org.jmailen.gradle.kotlinter.tasks.LintTask

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.transmemo.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.vectorize)
}

android {
    namespace = "com.chrysalide.transmemo"

    defaultConfig {
        applicationId = "com.chrysalide.transmemo"
        versionCode = 50
        versionName = "5.0"

        androidResources.generateLocaleConfig = true
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