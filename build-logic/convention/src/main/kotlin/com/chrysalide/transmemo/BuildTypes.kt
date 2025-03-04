package com.chrysalide.transmemo

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import java.io.FileInputStream
import java.util.Properties

fun Project.configureBuildTypes(commonExtension: CommonExtension<*, *, *, *, *>) {
    val localProperties = Properties().apply {
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) load(FileInputStream(localPropertiesFile))
    }
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
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }

        when (commonExtension) {
            is ApplicationExtension -> {
                commonExtension.apply {
                    buildTypes {
                        get("appDistrib").signingConfig = signingConfigs["appDistribRelease"]
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
            is LibraryExtension -> {
                commonExtension.apply {
                    buildTypes {
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
    }
}
