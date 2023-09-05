plugins {
    id("jacoco")
}

tasks {
    register("jacocoReport", JacocoReport::class) {
        group = "Coverage"
        description = "Generate XML/HTML code coverage reports for coverage.ec"
        val buildDirectory = layout.buildDirectory.get().asFile

        reports {
            xml.required.set(false)
            html.required.set(true)
        }

        sourceDirectories.setFrom("${project.projectDir}/src/main/kotlin")
        val fileFilter =
            listOf("**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*", "**/*Test*.*", "android/**/*.*")

        classDirectories.setFrom(
            files("${buildDirectory}/intermediates/javac/debug").map {
                fileTree(it) {
                    exclude(fileFilter)
                }
            },
            files("${buildDirectory}/tmp/kotlin-classes/debug").map {
                fileTree(it) {
                    exclude(fileFilter)
                }
            })

        executionData.setFrom(
            files("${buildDirectory}/test-results").map {
                fileTree(it) {
                    include("**/*.ec", "**/*.exec")
                }
            }
        )
    }
}
