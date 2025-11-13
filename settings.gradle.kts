// En C:/Users/Juan Vahir/Documents/Project/RANTU/settings.gradle.kts

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// Bloque a corregir y unificar
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // 👇 AÑADE ESTA LÍNEA PARA QUE ENCUENTRE LAS LIBRERÍAS DE SUPABASE 👇
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "RANTU"
include(":app")
