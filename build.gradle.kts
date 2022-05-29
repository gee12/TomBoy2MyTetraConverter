import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.0"
    id("com.github.gmazzo.buildconfig") version "3.0.3"
}

group = "com.gee12"
version = "1.2.0"

buildConfig {
    buildConfigField("String", "APP_NAME", "\"${project.name}\"")
    buildConfigField("String", "APP_VERSION", provider { "\"${project.version}\"" })
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(compose.desktop.currentOs)
    implementation("org.jdom:jdom2:2.0.6.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "Tomboy2Mytetra"
            packageVersion = "1.2.0"
            version = "1.2.0"
            description = "Tomboy to Mytetra data converter"
            copyright = "©2022 gee12"
            vendor = "gee12"
            windows {
                iconFile.set(project.file("icon.ico"))
                dirChooser = true
                menuGroup = "Tomboy2Mytetra"
                shortcut = true
            }
            linux {
                iconFile.set(project.file("icon.ico"))
                packageName = "tomboy2mytetra"
                shortcut = true
            }
        }
    }
}