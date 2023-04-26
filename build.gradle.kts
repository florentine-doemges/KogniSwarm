import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
}

group = "net.doemges"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

extra["springShellVersion"] = "3.0.2"
extra["testcontainersVersion"] = "1.17.6"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework:spring-web")
    implementation("org.springframework.shell:spring-shell-starter")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("org.seleniumhq.selenium:selenium-java:4.8.3")
    implementation("org.testcontainers:testcontainers:1.17.6")
    implementation("org.testcontainers:selenium:1.17.6")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-scripting-jvm
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:1.7.22")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-script-util
    implementation("org.jetbrains.kotlin:kotlin-script-util:1.7.22")

    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-scripting-jvm-host
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host:1.7.22")

    // https://mvnrepository.com/artifact/com.appmattus.fixture/fixture
    testImplementation("com.appmattus.fixture:fixture:1.2.0")
    // https://mvnrepository.com/artifact/org.jbehave/jbehave-core
    testImplementation("org.jbehave:jbehave-core:5.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    // https://mvnrepository.com/artifact/io.mockk/mockk
    testImplementation("io.mockk:mockk:1.13.5")
    // https://mvnrepository.com/artifact/com.willowtreeapps.assertk/assertk
    testImplementation("com.willowtreeapps.assertk:assertk:0.25")
    // https://mvnrepository.com/artifact/org.awaitility/awaitility
    testImplementation("org.awaitility:awaitility:4.2.0")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    // https://mvnrepository.com/artifact/com.appmattus.fixture/fixture
    testImplementation("com.appmattus.fixture:fixture:1.2.0")
    // https://mvnrepository.com/artifact/com.appmattus.fixture/fixture-javafaker
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
