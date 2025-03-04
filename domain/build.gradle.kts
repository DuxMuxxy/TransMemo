plugins {
    alias(libs.plugins.transmemo.jvm.library)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.bundles.kotlinx)
}
