plugins {
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.mateusz113.financemanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mateusz113.financemanager"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.mateusz113.financemanager.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
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
    testOptions {
        packaging {
            jniLibs {
                useLegacyPackaging = true
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    //Compose bom
    implementation(platform("androidx.compose:compose-bom:2024.03.00"))
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.facebook.android:facebook-android-sdk:16.3.0")

    //Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.44")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Compose dependencies
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.4")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Compose Nav Destinations
    implementation("io.github.raamcosta.compose-destinations:core:1.9.55")
    ksp("io.github.raamcosta.compose-destinations:ksp:1.9.55")

    // Date picker library
    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.9.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.3")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.3")

    //Coil
    implementation("io.coil-kt:coil-compose:2.5.0")

    //YCharts
    implementation("co.yml:ycharts:2.1.0")

    // Local unit tests
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.1")
    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("io.mockk:mockk:1.13.10")

    // Instrumentation tests
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.37")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("io.mockk:mockk-android:1.13.10")
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.1")
    androidTestImplementation("com.google.truth:truth:1.1.3")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
}