import com.android.build.api.dsl.AndroidResources

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlin-kapt")
}

android {
    namespace = "com.trbear9.openfarm"
    compileSdk = 36

    lint {
        baseline = file("lint-baseline.xml")
    }

    defaultConfig {
        ndk{
            abiFilters += listOf("arm64-v8a")
        }
        applicationId = "com.trbear9.openfarm"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a")
            isUniversalApk = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("/home/kujatic/Desktop/kufan.jks")
            storePassword = "kukuhrefan"
            keyAlias = "Jasper"
            keyPassword = "TrainingBear"
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
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            // optionally:
            // isShrinkResources = false
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        mlModelBinding = true
        viewBinding = true
        compose = true
    }
    buildToolsVersion = "35.0.0"
    dependenciesInfo {
        includeInApk = true
        includeInBundle = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    packaging {
            resources {
                pickFirsts += "messages/JavaOptionBundle.properties"
                pickFirsts += "kotlin/reflect/reflect.kotlin_builtins"
                pickFirsts += "DebugProbesKt.bin"
                pickFirsts += "META-INF/analysis-api/analysis-api-impl-base.xml"
                pickFirsts += "META-INF/analysis-api/analysis-api-fir.xml"
                pickFirsts += "META-INF/analysis-api/**"
                pickFirsts += "META-INF/**/**"
                pickFirsts += "META-INF/**"
                pickFirsts += "mozilla/public-suffix-list.txt"
                pickFirsts += listOf(
                    "lib/armeabi-v7a/liblitert_jni.so",
                    "lib/arm64-v8a/liblitert_jni.so"
                )
            }
    }
    fun AndroidResources.() {
        noCompress += "tflite"
    }
}

dependencies {
    implementation ("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")

    // https://mvnrepository.com/artifact/com.google.ai.edge.litert/litert-support
    implementation("com.google.ai.edge.litert:litert-support:1.4.0")
    // https://mvnrepository.com/artifact/com.google.ai.edge.litert/litert
    implementation("com.google.ai.edge.litert:litert:2.0.2")
//    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.4")
    implementation("com.open-meteo:open-meteo-api-kotlin:0.7.1-beta.1")
    implementation("org.apache.commons:commons-csv:1.10.0")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation ("androidx.paging:paging-runtime:3.3.6")
    implementation ("androidx.paging:paging-compose:3.3.6")
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m2)
    implementation(libs.vico.compose.m3)
    implementation(libs.vico.multiplatform)
    implementation(libs.vico.views)
    implementation(libs.vico.multiplatform.m2)
    implementation(libs.vico.multiplatform.m3)
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.google.accompanist:accompanist-flowlayout:0.30.1")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("androidx.compose.material:material-icons-extended:1.5.0")
    implementation("androidx.navigation:navigation-compose:2.9.4")
//    implementation("com.github.TrainingBear.OPSI-PlantAPI:api:3.1.3"){
//        exclude(group = "org.jetbrains.kotlin")
//        exclude(group = "org.jetbrains")
//        exclude("messages/JavaOptionBundle.properties")
//    }
    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    implementation("org.apache.commons:commons-lang3:3.18.0")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.0")
    // https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    /* https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp */
    implementation("com.google.guava:guava:31.0.1-android")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.tbuonomo:dotsindicator:5.0")

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
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-compiler-embeddable")
    }

    implementation(libs.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.video)

    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)
    implementation(libs.core.ktx)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.material3.android)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    testImplementation(kotlin("test"))

}