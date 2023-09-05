import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

plugins {
    kotlin("multiplatform")
    id("secant.kotlin-multiplatform-build-conventions")
    id("secant.dependency-conventions")
}

// Injects build information
// Note timestamp is not currently injected because it effectively disables the cache since it
// changes with every build
val generateBuildConfigTask = tasks.create("buildConfig") {
    val generatedDir = layout.buildDirectory.dir("generated").get().asFile

    val gitInfo = co.electriccoin.zcash.Git.newInfo(parent!!.projectDir)
    //val buildTimestamp = newIso8601Timestamp()

    inputs.property("gitSha", gitInfo.sha)
    inputs.property("gitCommitCount", gitInfo.commitCount)
    //inputs.property("buildTimestamp", buildTimestamp)

    outputs.dir(generatedDir)

    doLast {
        val outputFile = File("$generatedDir/co/electriccoin/zcash/build/BuildConfig.kt")
        outputFile.parentFile.mkdirs()

        // To add timestamp, add this to the output below
        // import kotlinx.datetime.Instant
        // import kotlinx.datetime.toInstant
        // val buildTimestamp: Instant = "$buildTimestamp".toInstant()
        outputFile.writeText(
            """
// Generated file
package co.electriccoin.zcash.build

const val gitSha: String = "${gitInfo.sha}"
const val gitCommitCount: Int = ${gitInfo.commitCount}
""".trimIndent()
        )
    }
}

kotlin {
    jvm()
    sourceSets {
        getByName("commonMain") {
            dependencies {
                kotlin.srcDir(generateBuildConfigTask)
                //api(libs.kotlinx.datetime)
            }
        }
        getByName("commonTest") {
            dependencies {
                implementation(kotlin("test"))
            }
        }
       getByName("jvmMain") {
            dependencies {
            }
        }
        getByName("jvmTest") {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

/**
 * @return Current ISO 8601 timestamp.
 */
fun newIso8601Timestamp(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return formatter.format(Date())
}
