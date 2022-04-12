package co.electriccoin.zcash.ui.screen.support.model

import kotlinx.datetime.Instant

class CrashInfo(val timestamp: Instant, val isUncaught: Boolean, val className: String, val stacktrace: String) {
    fun toSupportString() = buildString {
        appendLine("Exception")
        appendLine("  Is uncaught: $isUncaught")
        appendLine("  Timestamp: $timestamp")
        appendLine("  Class name: $className")

        // For now, don't include the stacktrace. It'll be too long for the emails we want to generate
    }

    companion object {
        // TODO [#303]: Implement returning some number of recent crashes
        suspend fun all(): List<CrashInfo> = emptyList()
    }
}

fun List<CrashInfo>.toCrashSupportString() = buildString {
    // Using the header "Exceptions" instead of "Crashes" to reduce risk of alarming users
    appendLine("Exceptions:")
    this@toCrashSupportString.forEach {
        appendLine(it.toSupportString())
    }
}
