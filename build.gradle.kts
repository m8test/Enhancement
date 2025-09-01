// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(m8test.plugins.android.application) apply false
    alias(m8test.plugins.android.library) apply false
    alias(m8test.plugins.kotlin.android) apply false
    alias(m8test.plugins.kotlin.compose) apply false
}

buildscript {
    dependencies {
        // Add the Maven coordinates and latest version of the plugin
        classpath(m8test.m8test.gradle)
    }
}