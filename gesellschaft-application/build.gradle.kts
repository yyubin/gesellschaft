plugins {
    id("java")
}

group = "org.yyubin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(project(":gesellschaft-domain"))
}

tasks.test {
    useJUnitPlatform()
}