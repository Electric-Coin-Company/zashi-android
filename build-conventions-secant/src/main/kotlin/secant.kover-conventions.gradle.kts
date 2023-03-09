pluginManager.withPlugin("org.jetbrains.kotlinx.kover") {
    extensions.findByType<kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension>()?.apply {
        disabledForProject = !project.property("IS_KOTLIN_TEST_COVERAGE_ENABLED").toString().toBoolean()
    }

    extensions.findByType<kotlinx.kover.gradle.plugin.dsl.KoverReportExtension>()?.apply {
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
