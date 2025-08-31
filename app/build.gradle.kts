plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.tbear9.openfarm"
    compileSdk = 36

    lint {
        baseline = file("lint-baseline.xml")
    }

    defaultConfig {
        applicationId = "com.tbear9.openfarm"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        mlModelBinding = true
        viewBinding = true
    }
//    externalNativeBuild {
//        cmake {
//            path = file("src/main/cpp/CMakeLists.txt")
//            version = "3.22.1"
//        }
//    }
    buildToolsVersion = "35.0.0"
//    viewBinding {
//        isEnabled = true
//    }
    dependenciesInfo {
        includeInApk = true
        includeInBundle = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }

}

dependencies {
//    implementation(libs.google.litert)
//    implementation(libs.image.labeling.custom)
//    implementation(libs.tensorflow.lite.task.vision)
//    implementation(libs.tensorflow.lite.gpu){
//        exclude(group = "org.checkerframework", module = "checker-qual")
//        exclude(group = "com.google.auto.value", module = "auto-value")
//    }
//    implementation(libs.tensorflow.tensorflow.lite.metadata){
//        exclude(group = "org.checkerframework", module = "checker-qual")
//        exclude(group = "com.google.auto.value", module = "auto-value")
//        exclude(group = "org.tensorflow", module = "tensorflow-lite")
//    }
//    implementation(libs.image.labeling.custom)
//    implementation(libs.tensorflow.lite)
    implementation("com.github.TrainingBear.OPSI-PlantAPI:api:1.1.3")
    /* https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp */
    implementation(libs.okhttp)
    implementation(libs.linkfirebase)
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

    implementation(libs.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.video)

    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)
    implementation(libs.androidx.core.ktx)
    implementation(libs.core.ktx)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.material3.android)
    implementation(libs.firebase.crashlytics.buildtools)
//    implementation(libs.androidx.material3.jvmstubs)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

}