if (project.property("IS_COVERAGE_ENABLED").toString().toBoolean()) {
    apply(plugin = "java-library")
    apply(plugin = "jacoco")

    configure<JacocoPluginExtension> {
        toolVersion = project.property("JACOCO_VERSION").toString()
    }

    afterEvaluate {
        tasks.withType<JacocoReport>().configureEach {
            classDirectories.setFrom(
                fileTree("${buildDir}/classes/kotlin/jvm/") {
                    exclude("**/*Test*.*", "**/*Fixture*.*")
                }
            )

            sourceDirectories.setFrom(
                // This could be better if it dynamically got the source directories, e.g. more along the lines of
                // kotlin.sourceSets["commonMain"].kotlin.sourceDirectories,
                // kotlin.sourceSets["jvmMain"].kotlin.sourceDirectories
                listOf("src/commonMain/kotlin", "src/jvmMain/kotlin")
            )
            executionData.setFrom("${buildDir}/jacoco/jvmTest.exec")
        }
    }
}
