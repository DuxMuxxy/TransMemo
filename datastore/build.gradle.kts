plugins {
    alias(libs.plugins.transmemo.android.library)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.kotlinter)
}

android {
    namespace = "com.chrysalide.transmemo.datastore"
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
    implementation(project(":domain"))

    implementation(libs.bundles.koin)
    implementation(libs.bundles.kotlinx)
    implementation(libs.bundles.androidx.dataStore)
    implementation(libs.protobuf.kotlin.lite)
}