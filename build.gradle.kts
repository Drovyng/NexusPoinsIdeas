plugins {
    java
}

group = "com.drovyng"
version = "1.4"
description = "NexusPoins Ideas Plugin by Drovyng"

base {
    archivesName.set("NexusPoins-Ideas")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.playpro.com")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")

    compileOnly("net.coreprotect:coreprotect:22.4")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    // withSourcesJar()
}

tasks {
    assemble {
        dependsOn(clean)
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
        // Token replacing
        val props = mapOf(
            "version" to version,
            "desc" to project.description,
            "apiVersion" to "1.21"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    jar {
        from("LICENSE") {
            rename { "LICENSE_${rootProject.name}" }
        }
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
    }
}
