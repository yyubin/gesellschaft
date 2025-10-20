plugins {
    id("java")
}

group = "org.yyubin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
    }

    tasks.test {
        useJUnitPlatform()
    }
}
