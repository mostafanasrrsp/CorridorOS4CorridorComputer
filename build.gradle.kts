plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    application
}

group = "com.corridor.os"
version = "0.1.0-ALPHA"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

application {
    mainClass.set("com.corridor.os.tools.cli.CorridorCLIKt")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
}

tasks.register("buildKernel") {
    dependsOn("compileKotlin")
    group = "corridor"
    description = "Build the Corridor OS kernel"
}

tasks.register("buildTools") {
    dependsOn("jar")
    group = "corridor"
    description = "Build development tools and emulator"
}

tasks.register("buildAll") {
    dependsOn("buildKernel", "buildTools")
    group = "corridor"
    description = "Build kernel and tools"
}

tasks.test {
    useJUnitPlatform()
}

