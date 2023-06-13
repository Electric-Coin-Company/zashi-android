import java.util.concurrent.TimeUnit
if (isRosetta()) {
    logger.warn("This Gradle invocation is running under Rosetta. Use an ARM (aarch64) JDK to " +
        "improve performance. One can be downloaded from https://adoptium.net/temurin/releases")
}

@Suppress("MagicNumber")
private val maxTimeoutMillis = 5000L

/**
 * This method is safe to call from any operating system or CPU architecture.
 *
 * @return True if the application is running under Rosetta.
 */
fun isRosetta(): Boolean {
    if (System.getProperty("os.name").lowercase(java.util.Locale.ROOT).startsWith("mac")) {
        // Counterintuitive, but running under Rosetta is reported as Intel64 to the JVM
        if (!System.getProperty("os.arch").lowercase(java.util.Locale.ROOT).contains("aarch64")) {
            val outputValue = Runtime.getRuntime()
                .exec(arrayOf("sysctl", "-in", "sysctl.proc_translated"))
                .scanOutputLine()
                .toIntOrNull()

            if (1 == outputValue) {
                return true
            }
        }
    }

    return false
}

fun Process.scanOutputLine(): String {
    var outputString = ""

    inputStream.use { inputStream ->
        java.util.Scanner(inputStream).useDelimiter("\\A").use { scanner ->
            while (scanner.hasNext()) {
                outputString = scanner.next()
            }
        }
    }

    waitFor(maxTimeoutMillis, TimeUnit.MILLISECONDS)

    return outputString.trim()
}
