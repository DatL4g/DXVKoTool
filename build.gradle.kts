import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.compose") version "1.2.0-beta02"
    kotlin("plugin.serialization") version "1.7.10"
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
    id("com.github.ben-manes.versions") version "0.42.0"
    id("com.squareup.sqldelight") version "1.5.3"
}

val appVersion = "0.3.0"

group = "dev.datlag"
version = appVersion

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

val ktorfitVersion = "1.0.0-beta14"
val ktorVersion = "2.1.2"
val composeVersion = "1.2.0-beta02"

dependencies {
    implementation("org.jetbrains.compose.desktop:desktop-jvm-linux-x64:$composeVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.jetbrains.compose.material3:material3:$composeVersion")
    implementation("org.jetbrains.compose.material:material-icons-extended:$composeVersion")
    implementation("io.github.vincenzopalazzo:material-ui-swing:1.1.4")

    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("de.jensklingenberg.ktorfit:ktorfit-lib:$ktorfitVersion")
    ksp("de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")

    implementation("org.tukaani:xz:1.9")
    implementation("org.rauschig:jarchivelib:1.2.0")
    implementation("org.apache.tika:tika-core:2.4.1")

    implementation("com.squareup.sqldelight:sqlite-driver:1.5.3")
    implementation("com.squareup.sqldelight:coroutines-extensions-jvm:1.5.3")

    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"

}

compose.desktop {
    application {
        mainClass = "dev.datlag.dxvkotool.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.AppImage, TargetFormat.Rpm, TargetFormat.Deb)
            packageName = "DXVKoTool"
            packageVersion = appVersion
            outputBaseDir.set(project.buildDir.resolve("release"))
            description = "The DXVKoTool extracts DXVK caches of games automatically and can update them with newer caches"
            copyright = "© 2020 Jeff Retz (DatLag). All rights reserved."
            licenseFile.set(project.file("LICENSE"))

            linux {
                iconFile.set(project.file("./src/main/resources/AppIcon128.png"))
            }

            modules("java.instrument", "java.management", "java.naming", "java.prefs", "java.sql", "jdk.unsupported")
        }
    }
}

sqldelight {
    database("DXVKoToolDB") {
        packageName = "dev.datlag"
    }
}
