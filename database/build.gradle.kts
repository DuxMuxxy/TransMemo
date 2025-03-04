plugins {
    alias(libs.plugins.transmemo.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinter)
}

android {
    namespace = "com.chrysalide.transmemo.database"

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.generateKotlin", "true")
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":datastore"))

    implementation(libs.bundles.koin)
    implementation(libs.bundles.kotlinx)
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
}