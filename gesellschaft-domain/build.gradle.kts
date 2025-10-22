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

    // Lombok을 compileOnly + implementation 둘 다 설정
    // - compileOnly: 컴파일 시점에만 필요 (어노테이션 처리)
    // - implementation: JAR에 포함하여 다른 모듈에서 참조 가능
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    implementation("org.projectlombok:lombok:1.18.34")

}

tasks.test {
    useJUnitPlatform()
}