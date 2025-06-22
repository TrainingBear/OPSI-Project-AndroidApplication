plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.tbear9.openfarm"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tbear9.openfarm"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags += ""
            }
        }
    }
    packaging {
        packaging {
            jniLibs {
                pickFirsts += "lib/arm64-v8a/libtensorflowlite_jni.so"
                pickFirsts += "lib/armeabi-v7a/libtensorflowlite_jni.so"
            }
        }

        resources {
            pickFirsts += "**"
        }
    }

    configurations.all {
        resolutionStrategy {
            force("org.checkerframework:checker-qual:3.42.0")
            force("com.google.auto.value:auto-value:1.6.3")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = rootProject.extra["viewBindingEnabled"] as Boolean
        mlModelBinding = true
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
//    sourceSets["main"].java.srcDir("build/generated/ml_source_set_base_package")
}

dependencies {
//    implementation(libs.litert.support.api){
//        exclude(group = "org.checkerframework", module = "checker-qual")
//        exclude(group = "com.google.auto.value", module = "auto-value")
//    }
    implementation(libs.tensorflow.lite.gpu){
        exclude(group = "org.checkerframework", module = "checker-qual")
        exclude(group = "com.google.auto.value", module = "auto-value")
    }
    implementation(libs.tensorflow.tensorflow.lite.support){
        exclude(group = "org.checkerframework", module = "checker-qual")
        exclude(group = "com.google.auto.value", module = "auto-value")
        exclude(group = "org.tensorflow", module = "tensorflow-lite")
    }
    implementation(libs.tensorflow.tensorflow.lite.metadata){
        exclude(group = "org.checkerframework", module = "checker-qual")
        exclude(group = "com.google.auto.value", module = "auto-value")
        exclude(group = "org.tensorflow", module = "tensorflow-lite")
    }
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.leanback)
    implementation(libs.glide)
    implementation(libs.activity)
    implementation(libs.mediarouter)
    implementation(libs.room.compiler.processing.testing){
        exclude(group = "org.checkerframework", module = "checker-qual")
        exclude(group = "com.google.auto.value", module = "auto-value")
    }
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}