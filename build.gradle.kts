plugins {
    kotlin("multiplatform") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.9.20"
}

group = "com.corridor.os"
version = "0.1.0-ALPHA"

repositories {
    mavenCentral()
    maven("https://repo.kotlin.link")
}

kotlin {
    // Native target for the OS kernel
    linuxX64("kernel") {
        binaries {
            executable {
                entryPoint = "com.corridor.os.kernel.main"
                freeCompilerArgs += listOf(
                    "-opt-in=kotlin.ExperimentalUnsignedTypes",
                    "-opt-in=kotlin.ExperimentalStdlibApi",
                    "-opt-in=kotlin.native.concurrent.ObsoleteWorkersApi"
                )
            }
        }
    }
    
    // JVM target for development tools and emulator
    jvm("tools") {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }
        
        val kernelMain by getting {
            dependencies {
                // Kernel-specific dependencies
            }
        }
        
        val toolsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")
                implementation("org.slf4j:slf4j-api:2.0.9")
                implementation("ch.qos.logback:logback-classic:1.4.11")
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        
        val toolsTest by getting {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.10.0")
                implementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
            }
        }
    }
}

tasks.register("buildKernel") {
    dependsOn("kernelBinaries")
    group = "corridor"
    description = "Build the Corridor OS kernel"
}

tasks.register("buildTools") {
    dependsOn("toolsJar")
    group = "corridor"
    description = "Build development tools and emulator"
}

tasks.register("buildAll") {
    dependsOn("buildKernel", "buildTools")
    group = "corridor"
    description = "Build kernel and tools"
}

