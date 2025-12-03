plugins {
    java
    id("org.springframework.boot") version "3.5.6" apply false
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

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.6")
    }
    dependencies {
        dependency("jakarta.persistence:jakarta.persistence-api:3.2.0")
    }
}

dependencies {
    // Spring Boot with JPA 3.2.0
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Jackson for JSON conversion
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // MySQL
    runtimeOnly("com.mysql:mysql-connector-j")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Jinx (DDL 생성)
    annotationProcessor("io.github.yyubin:jinx-processor:0.0.14")
    implementation("io.github.yyubin:jinx-core:0.0.14")

    // Project dependencies
    implementation(project(":gesellschaft-domain"))
    implementation(project(":gesellschaft-application"))
}

val jinxCli by configurations.creating

dependencies {
    "jinxCli"("io.github.yyubin:jinx-cli:0.0.14")
}

tasks.register<JavaExec>("jinxMigrate") {
    group = "jinx"
    classpath = configurations["jinxCli"]
    mainClass.set("org.jinx.cli.JinxCli")
    args("db", "migrate", "-d", "mysql")
    dependsOn("classes")
}

tasks.register<JavaExec>("jinxPromoteBaseline") {
    group = "jinx"
    classpath = configurations["jinxCli"]
    mainClass.set("org.jinx.cli.JinxCli")
    args("db", "promote-baseline", "--force")  // DB 정보 불필요
    dependsOn("classes")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
