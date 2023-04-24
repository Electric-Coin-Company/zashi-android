pluginManager.withPlugin("org.jetbrains.kotlinx.kover") {
    extensions.findByType<kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension>()?.apply {
        if (!project.property("IS_KOTLIN_TEST_COVERAGE_ENABLED").toString().toBoolean()) {
            disable()
        }
    }

    extensions.findByType<kotlinx.kover.gradle.plugin.dsl.KoverReportExtension>()?.apply {
        defaults {
            html {
                onCheck = true
                setReportDir(layout.buildDirectory.dir("kover/html"))
            }
            xml {
                onCheck = true
                setReportFile(layout.buildDirectory.file("kover/xml/report.xml"))
            }
        }
    }
}
