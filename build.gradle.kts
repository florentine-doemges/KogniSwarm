import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.nio.file.FileSystems

plugins {
    id("org.springframework.boot") version "3.0.7"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.allopen") version "1.7.22"
}

group = "net.doemges"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "m2-dv8tion"
        url = URI("https://m2.dv8tion.net/releases")
    }
}

extra["springShellVersion"] = "3.0.2"
extra["testcontainersVersion"] = "1.17.6"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux"){
        exclude(group = "io.netty", module = "netty-resolver-dns-native-macos")
    }
    implementation("org.springframework.data:spring-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.apache.camel.springboot:camel-spring-boot-starter:4.0.0-M3")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.testcontainers:testcontainers:1.18.1")
    implementation("io.weaviate:client:4.1.1")
    implementation("com.aallam.openai:openai-client:3.2.3")
    implementation("com.google.apis:google-api-services-customsearch:v1-rev20210918-1.32.1")
    implementation("io.netty:netty-resolver-dns-native-macos:4.1.92.Final:osx-aarch_64")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    runtimeOnly("io.ktor:ktor-client-okhttp:2.3.0")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.testcontainers:elasticsearch")
    testImplementation("org.testcontainers:junit-jupiter")

}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
        mavenBom("org.springframework.shell:spring-shell-dependencies:${property("springShellVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("exportCode") {
    val outputDir = File("${project.buildDir}/outputs")
    val outputFile = File(outputDir, "project_code.txt")

    val gitIgnoreFile = File(projectDir, ".gitignore")
    val ignorePatterns = if (gitIgnoreFile.exists()) gitIgnoreFile.readLines() else listOf()

    doLast {
        outputDir.mkdirs()
        outputFile.writeText("") // clear previous content

        project.fileTree(projectDir)
            .matching {
                exclude(ignorePatterns)
            }
            .visit {
                if (!isDirectory && name.endsWith(".kt")) {
                    outputFile.appendText(file.readText() + "\n")
                }
            }

        println("Code exported to ${outputFile.absolutePath}")
    }
}