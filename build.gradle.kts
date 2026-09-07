plugins {
    java
    application

    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.hyozlet"
version = "0.1.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainClass.set("com.hyozlet.tempo.TempoApplication")
}

javafx {
    version = "25.0.2"
    modules = listOf(
        "javafx.controls",
        "javafx.fxml"
    )
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.53.2.1")

    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
