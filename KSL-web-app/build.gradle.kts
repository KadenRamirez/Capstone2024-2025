plugins {
    kotlin("jvm") version "1.9.0"
    application
}

repositories {
    mavenCentral()
    // mavenLocal()
    // maven("https://jitpack.io")
}

dependencies {
    implementation("io.javalin:javalin:6.5.0") // Updated Javalin version
    implementation("org.thymeleaf:thymeleaf:3.1.1.RELEASE") // Thymeleaf for templating
    implementation("org.slf4j:slf4j-simple:2.0.9") // Logging for Javalin
    implementation("commons-io:commons-io:2.11.0") // File handling utilities
    implementation("org.jetbrains.kotlin:kotlin-stdlib") // Kotlin standard library
    implementation("org.jetbrains.kotlin:kotlin-scripting-jsr223")
    implementation("org.jetbrains.kotlin:kotlin-script-runtime")
    api(group = "io.github.rossetti", name = "KSLCore", version = "R1.2.0")
}

application {
    mainClass.set("app.MainKt") // Entry point
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

