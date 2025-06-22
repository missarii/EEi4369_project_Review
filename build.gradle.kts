// File: build.gradle.kts (project root)

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Android Gradle Plugin
        classpath("com.android.tools.build:gradle:8.9.0")
        // Google Services plugin for Firebase
        classpath("com.google.gms:google-services:4.4.2")
    }
}

// REMOVE any allprojects { repositories { … } } or plugins { … } blocks here.
// Settings-level dependencyResolutionManagement now controls all repositories.
