import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "de.dertyp7214.rboardcomponents"
    buildToolsVersion = "34.0.0"
    compileSdk = 34

    buildFeatures.aidl = true

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_20
        targetCompatibility = JavaVersion.VERSION_20
    }
    kotlinOptions {
        jvmTarget = JvmTarget.JVM_20.toString()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JvmTarget.JVM_20.description
        }
    }
}

dependencies {
    implementation(project(":colorutilsc"))
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.preference.ktx)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
    implementation(libs.gson)
}