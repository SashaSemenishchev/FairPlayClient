import dev.architectury.pack200.java.Pack200Adapter

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("gg.essential.loom") version "0.10.0.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    java
    idea
    kotlin("jvm") version "1.5.30"
}

version = "3.3"
group = "me.mrfunny"
val copyDir = "/Users/sasha/Library/Application Support/minecraft/mods"

base {
    archivesName.set(project.name)
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.sk1er.club/repository/maven-public/")
    maven("https://repo.sk1er.club/repository/maven-releases/")
    maven("https://jitpack.io")
    maven("https://clojars.org/repo")
}

loom {
    silentMojangMappingsLicense()
    launchConfigs {
        getByName("client") {
            property("mixin.debug.verbose", "true")
            property("mixin.debug.export", "true")
            property("mixin.dumpTargetOnFailure", "true")
            property("legacy.debugClassLoading", "true")
            property("legacy.debugClassLoadingSave", "true")
            property("legacy.debugClassLoadingFiner", "true")
            arg("--tweakClass", "gg.essential.loader.stage0.EssentialSetupTweaker")
            arg("--mixin", "mixins.sm.json")
        }
    }
    runConfigs {
        getByName("client") {
            isIdeConfigGenerated = true
        }
        remove(getByName("server"))
    }
    forge {
        pack200Provider.set(Pack200Adapter())
        mixinConfig("mixins.sm.json")
    }
    mixin {
        defaultRefmapName.set("mixins.sm.refmap.json")
    }
}

val include: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    include("gg.essential:loader-launchwrapper:1.1.3")
    compileOnly(files("libs/liquidbounce.jar"))
    include("org.java-websocket:Java-WebSocket:1.5.2")
    implementation("gg.essential:essential-1.8.9-forge:3760")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    compileOnly("org.spongepowered:mixin:0.8.5")
}

tasks {
    register<Copy>("copyJar") {
        from(project.buildDir.absolutePath + File.separator + "libs" + File.separator + (project.name + "-" + project.version + ".jar"))
        destinationDir = File(project.properties["modsDir"]?.toString() ?: copyDir)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        println(project.buildDir.absolutePath + File.separator + "libs" + File.separator + (project.name + "-" + project.version + ".jar"))
    }

    register("buildDev") {
        finalizedBy("copyJar")
        dependsOn("build")
        group = "build"
    }

    register<Copy>("installHackEngine") {
        val to = project.properties["modsDir"]?.toString() ?: project.buildDir.toString()
        from(project.projectDir.absolutePath + File.separator + "libs" + File.separator + "liquidbounce.jar")
        destinationDir = File(to)
        if(!destinationDir.exists()) {
            destinationDir.mkdirs()
        }
        duplicatesStrategy = org.gradle.api.file.DuplicatesStrategy.INCLUDE
        group = "build setup"
    }

    register("installClient") {
        group = "build"
        dependsOn("buildDev")
        finalizedBy("installHackEngine")
    }
    wrapper.get().doFirst {
        delete("$buildDir/libs/")
    }
    processResources {
        inputs.property("version", project.version)
        inputs.property("mcversion", "1.8.9")

        filesMatching("mcmod.info") {
            expand(mapOf("version" to project.version, "mcversion" to "1.8.9"))
        }
        dependsOn(compileJava)
    }
    jar {
        manifest {
            attributes(
                mapOf(
                    "ForceLoadAsMod" to true,
                    "ModSide" to "CLIENT",
                    "ModType" to "FML",
                    "TweakClass" to "gg.essential.loader.stage0.EssentialSetupTweaker",
                    "TweakOrder" to "0",
                    "MixinConfigs" to "mixins.sm.json"
                )
            )
        }
        dependsOn(shadowJar)
        enabled = false
    }

    shadowJar {
        archiveClassifier.set("dev")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        configurations = listOf(include)

        relocate("org.apache.commons.collections4", "me.cephetir.apache.commons.collections4")

        exclude(
            "**/LICENSE.md",
            "**/LICENSE.txt",
            "**/LICENSE",
            "**/NOTICE",
            "**/NOTICE.txt",
            "pack.mcmeta",
            "dummyThing",
            "**/module-info.class",
            "META-INF/proguard/**",
            "META-INF/maven/**",
            "META-INF/versions/**",
            "META-INF/com.android.tools/**",
            "fabric.mod.json"
        )
        mergeServiceFiles()
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
}

configurations {
    all {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-runtime")
    }
}