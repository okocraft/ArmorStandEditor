plugins {
    alias(libs.plugins.jcommon)
    alias(libs.plugins.bundler)
    alias(libs.plugins.paperweight.userdev)
    alias(libs.plugins.run.paper)
}

jcommon {
    javaVersion = JavaVersion.VERSION_25

    setupPaperRepository()
    setupJUnit(libs.junit.bom)
    setupMockito(libs.mockito)

    commonDependencies {
        implementation(libs.mcmsgdef)
        compileOnly(libs.platform.paper)

        testImplementation(libs.junit.jupiter)
        testImplementation(libs.platform.paper)
        testImplementation(libs.slf4j.api)
        testRuntimeOnly(libs.slf4j.simple)
    }
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper.get())
}

repositories {
    mavenCentral()
}

bundler {
    replacePluginVersionForBukkit(version)
    copyToRootBuildDirectory("ArmorStandEditor-${project.version}.jar")
}

tasks {
    test {
        systemProperty("org.slf4j.simpleLogger.cacheOutputStream", "true")
        systemProperty("junit.jupiter.extensions.autodetection.enabled", "true")
    }
    runServer {
        minecraftVersion(libs.versions.paper.get().replaceAfter(".build", "").removeSuffix(".build"))
    }
    shadowJar {
        minimize()
    }
}
