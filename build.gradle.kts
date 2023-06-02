import com.github.gradle.node.npm.task.NpmTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    id("org.springframework.boot") version "3.0.7"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.github.node-gradle.node") version "5.0.0"
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
    implementation("io.netty:netty-resolver-dns-native-macos:4.1.92.Final:osx-aarch_64")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("org.seleniumhq.selenium:selenium-java:4.9.1")
    implementation("org.testcontainers:testcontainers:1.18.1")
    implementation("org.testcontainers:selenium:1.18.1")
    implementation("org.apache.tika:tika-core:2.8.0")
    implementation("com.google.apis:google-api-services-customsearch:v1-rev86-1.25.0")


    runtimeOnly("io.ktor:ktor-client-okhttp:2.3.0")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("com.willowtreeapps.assertk:assertk:0.25")
    testImplementation("org.awaitility:awaitility:4.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("com.appmattus.fixture:fixture:1.2.0")
    testImplementation("com.appmattus.fixture:fixture-javafaker:1.2.0")

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

node {
    download.set(false)
    nodeProjectDir.set(file("vue-element-admin"))
}

tasks {
    register<NpmTask>("npmRunBuildProd") {
        group = "build"
        description = "Run 'npm run build:prod'"
        args.set(listOf("run", "build:prod"))
        node.nodeProjectDir.set(file("vue-element-admin"))
    }
}

tasks {
    register<NpmTask>("npmVersion") {
        group = "npm"
        description = "Print npm version"
        args.set(listOf("-v"))
    }
}

tasks.register<Copy>("copyVueDistToStatic") {
    group = "build"
    description = "Copies Vue.js build output to the static resources directory"
    from("vue-element-admin/dist")
    into("src/main/resources/static")
    dependsOn("npmRunBuildProd")
}

