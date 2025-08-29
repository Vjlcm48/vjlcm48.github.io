plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.services) apply false

    id("com.google.firebase.crashlytics") version "3.0.6" apply false
}
