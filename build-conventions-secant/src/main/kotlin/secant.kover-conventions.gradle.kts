import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension

pluginManager.withPlugin("org.jetbrains.kotlinx.kover") {
    extensions.configure<KoverProjectExtension>("kover") {
        if (!project.property("IS_KOTLIN_TEST_COVERAGE_ENABLED").toString().toBoolean()) {
            disable()
        }
        reports {
            total {
                html {
                    onCheck = true
                    htmlDir = layout.buildDirectory.dir("kover/html")
                }
                xml {
                    onCheck = true
                    xmlFile = layout.buildDirectory.file("kover/xml/report.xml")
                }
            }
        }
    }
}
