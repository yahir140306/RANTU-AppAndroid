plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}
// Consolidated dependencies block: use version catalog for core libs and explicit versions for Supabase modules
dependencies {
    // Core + lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose (via BOM from version catalog)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Supabase: fijamos la versión explícitamente para evitar resoluciones sin versión
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.5.1")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.5.1")
    implementation("io.github.jan-tennert.supabase:storage-kt:2.5.1")
    // ViewModel + Compose helpers
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("io.ktor:ktor-client-android:2.3.12")

    // Otras librerías
    implementation("io.coil-kt:coil-compose:2.6.0")
    // Kotlinx serialization (JSON)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    // Accompanist Pager para carrusel de imágenes
    implementation("com.google.accompanist:accompanist-pager:0.32.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.32.0")
    
    // Captura de pantalla para generar imágenes
    implementation("dev.shreyaspatil:capturable:2.1.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

android {
    namespace = "com.example.rantu"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.rantu"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}