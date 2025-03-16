plugins {
    kotlin("jvm") version "1.9.0"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.javalin:javalin:5.6.0") // Javalin Web Framework
    implementation("org.thymeleaf:thymeleaf:3.1.1.RELEASE") // Thymeleaf for templating
}

application {
    mainClass.set("MainKt") // Entry point
}
