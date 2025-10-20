plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.yyubin"
version = "0.0.1-SNAPSHOT"
description = "gesellschaft-infrastructure"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("com.mysql:mysql-connector-j")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    annotationProcessor("io.github.yyubin:jinx-processor:0.0.7")
    implementation("io.github.yyubin:jinx-core:0.0.7")
    implementation("io.github.yyubin:jinx-cli:0.0.7")

    implementation(project(":gesellschaft-domain"))
    implementation(project(":gesellschaft-application"))
}

tasks.register<JavaExec>("jinxGenerate") {
    group = "jinx"
    description = "Generate DDL and Liquibase YAML from entity changes via Jinx CLI"

    mainClass.set("io.github.yyubin.jinx.cli.JinxApplication")

    classpath = sourceSets.main.get().runtimeClasspath

    args = listOf(
        "migrate",
        "--out=build/jinx"
    )
}

tasks.withType<Test> {
    useJUnitPlatform()
}
