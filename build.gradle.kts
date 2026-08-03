plugins {
    id("java")
    id("application")
}

group = "com.twisted"
version = "0.1.0"

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.twisted.shell.Main")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(26))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.processResources {

    inputs.property("version", project.version)
    inputs.property("group", project.group)

    filesMatching("META-INF/build.properties") {
        expand(
            mapOf(
                "name" to "TwistedShell",
                "version" to project.version,
                "group" to project.group,
                "java" to JavaVersion.current().majorVersion
            )
        )
    }
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "com.twisted.shell.Main"
        )
    }
}