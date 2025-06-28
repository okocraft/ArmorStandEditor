plugins {
    alias(libs.plugins.jcommon)
    alias(libs.plugins.bundler)
    alias(libs.plugins.run.paper)
}

jcommon {
    javaVersion = JavaVersion.VERSION_21

    setupPaperRepository()

    commonDependencies {
        implementation(libs.annotations)
        implementation(libs.configapi.yaml)
        implementation(libs.translationloader)

        compileOnly(libs.platform.paper)
    }
}

bundler {
    replacePluginVersionForBukkit(version)
}

tasks {
    runServer {
        minecraftVersion(libs.versions.paper.get().removeSuffix("-R0.1-SNAPSHOT"))
    }
    shadowJar {
        minimize {
            exclude("net.okocraft.armorstandeditor.ArmorStandEditorPlugin")
        }
    }
}
