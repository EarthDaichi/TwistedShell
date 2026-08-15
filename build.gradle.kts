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
dependencies {
    implementation("org.jline:jline:3.30.6")

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.register<Jar>("fatJar") {
    group = "build"
    description = "Build TwistedShell with all dependencies and media"

    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes[
            "Main-Class"
        ] = application.mainClass.get()
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)

    from({
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) {
                it
            } else {
                zipTree(it)
            }
        }
    })

    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
}