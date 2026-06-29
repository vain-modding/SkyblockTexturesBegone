pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        mavenCentral()
    }

    val loom_version: String by settings

    plugins {
        id("fabric-loom") version loom_version
    }
}

rootProject.name = "SkyblockTexturesBegone"
