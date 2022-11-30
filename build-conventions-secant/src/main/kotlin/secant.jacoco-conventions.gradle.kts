plugins {
    id("jacoco")
}

tasks {
    register("jacocoReport", JacocoReport::class) {
        group = "Coverage"
        description = "Generate XML/HTML code coverage reports for coverage.ec"

        reports {
            xml.required.set(false)
            html.required.set(true)
        }

        sourceDirectories.setFrom("${project.projectDir}/src/main/kotlin")
        val fileFilter =
            listOf("**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*", "**/*Test*.*", "android/**/*.*")

        classDirectories.setFrom(
            files("${buildDir}/intermediates/javac/debug").map {
                fileTree(it) {
                    exclude(fileFilter)
                }
            },
            files("${buildDir}/tmp/kotlin-classes/debug").map {
                fileTree(it) {
                    exclude(fileFilter)
                }
            })

        executionData.setFrom(
            files("${buildDir}/test-results").map {
                fileTree(it) {
                    include("**/*.ec", "**/*.exec")
                }
            }
        )
    }
}
