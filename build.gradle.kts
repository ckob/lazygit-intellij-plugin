plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij") version "1.17.4"
    id("org.jetbrains.changelog") version "2.2.0"
    id("org.jetbrains.qodana") version "2024.1.5"
    id("org.jetbrains.kotlinx.kover") version "0.8.2"
}

group = "dev.ckob.lazygit"
version = "0.2.2"

repositories {
    mavenCentral()
}

intellij {
    version.set("2024.1")
    type.set("IC")
    plugins.set(listOf("org.jetbrains.plugins.terminal", "IdeaVIM:2.10.2"))
}

changelog {
    version.set(project.version.toString())
    headerParserRegex.set("""(\d+\.\d+\.\d+)""".toRegex())
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
    patchPluginXml {
        sinceBuild.set("241")
        untilBuild.set("261.*")
        changeNotes.set(provider {
            changelog.renderItem(changelog.getOrNull(project.version.toString()) ?: changelog.getUnreleased(), org.jetbrains.changelog.Changelog.OutputType.HTML)
        })
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
        dependsOn("patchChangelog")
    }

    buildSearchableOptions {
        enabled = false
    }
}

kover {
    reports {
        total {
            xml {
                onCheck = true
            }
        }
    }
}
