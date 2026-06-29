plugins {
    id("fabric-loom")
    `maven-publish`
}

group = property("maven_group")!!
version = property("mod_version")!!

base {
    archivesName.set(property("archives_base_name") as String)
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
}

tasks {
    processResources {
        val props = mapOf(
            "version" to project.version,
            "minecraft_version" to project.property("minecraft_version"),
            "loader_version" to project.property("loader_version"),
        )
        inputs.properties(props)
        filesMatching("fabric.mod.json") {
            expand(props)
        }
    }

    compileJava {
        options.release.set(21)
        options.encoding = "UTF-8"
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    withSourcesJar()
}
