import com.m8test.util.VersionUtils
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption


plugins {
    alias(m8test.plugins.m8test.gradle.android.application)
}

android {
    namespace = "com.m8test.enhancement"
    compileSdk = m8test.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = m8test.versions.minSdk.get().toInt()
        targetSdk = m8test.versions.targetSdk.get().toInt()
        versionName = "0.1.0"
        versionCode = VersionUtils.getCode(versionName!!)
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
        sourceCompatibility = JavaVersion.toVersion(m8test.versions.sourceCompatibility.get())
        targetCompatibility = JavaVersion.toVersion(m8test.versions.targetCompatibility.get())
    }

    kotlinOptions {
        jvmTarget = m8test.versions.jvmTarget.get()
    }
}

dependencies {
    compileOnly(m8test.m8test.sdk)
    dokkaPlugin(m8test.dokka.hide.internal.api)
    dokkaPlugin(m8test.dokka.code.provider.generator)
}


dokka {
    moduleName.set("sdk")
    dokkaPublications.html {
        outputDirectory.set(layout.buildDirectory.dir("dokka/html"))
    }
    dokkaSourceSets.main {
        val projects = listOf(":server")
        projects.forEach { p ->
            val subproject = project(p)
            subproject.dependencies {
                compileOnly(m8test.dokka.annotation)
                dokkaPlugin(m8test.dokka.hide.internal.api)
                dokkaPlugin(m8test.dokka.code.provider.generator)
//                    dokkaPlugin(libs.dokka.custom.link)
            }
            val kotlinDir = subproject.file("src/main/kotlin")
            if (kotlinDir.exists()) sourceRoots.from(kotlinDir)
            val javaDir = subproject.file("src/main/java")
            if (javaDir.exists()) sourceRoots.from(javaDir)
        }
    }
    pluginsConfiguration.html {
        footerMessage.set("powered by m8test")
    }
}