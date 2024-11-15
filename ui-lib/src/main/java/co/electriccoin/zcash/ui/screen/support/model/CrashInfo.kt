package co.electriccoin.zcash.ui.screen.support.model

import android.content.Context
import co.electriccoin.zcash.crash.ExceptionPath
import co.electriccoin.zcash.crash.ReportedException
import co.electriccoin.zcash.crash.android.getExceptionDirectory
import co.electriccoin.zcash.crash.new
import co.electriccoin.zcash.spackle.io.listFilesSuspend
import kotlinx.datetime.Instant
import java.io.File

// TODO [#1301]: Localize feedback text content
// TODO [#1301]: https://github.com/Electric-Coin-Company/zashi-android/issues/1301

data class CrashInfo(val exceptionClassName: String, val isUncaught: Boolean, val timestamp: Instant) {
    fun toSupportString() =
        buildString {
            appendLine("Exception")
            appendLine("  Class name: $exceptionClassName")
            appendLine("  Is uncaught: $isUncaught")
            appendLine("  Timestamp: $timestamp")

            // For now, don't include the stacktrace. It'll be too long for the emails we want to generate
        }

    companion object
}

fun List<CrashInfo>.toCrashSupportString() =
    if (isEmpty()) {
        ""
    } else {
        buildString {
            // Using the header "Exceptions" instead of "Crashes" to reduce risk of alarming users
            appendLine("Exceptions:")
            this@toCrashSupportString.forEach {
                appendLine(it.toSupportString())
            }
        }
    }

// If you change this, be sure to update the test case under /docs/testing/manual_testing/Contact Support.md
private const val MAX_EXCEPTIONS_TO_REPORT = 5

suspend fun CrashInfo.Companion.all(context: Context): List<CrashInfo> {
    val exceptionDirectory = ExceptionPath.getExceptionDirectory(context) ?: return emptyList()
    val filesList: List<File>? = exceptionDirectory.listFilesSuspend()?.toList()
    return filesList?.run {
        mapNotNull {
            ReportedException.new(it)
        }.sortedBy { it.time }
            .reversed()
            .take(MAX_EXCEPTIONS_TO_REPORT)
            .map { CrashInfo(it.exceptionClassName, it.isUncaught, it.time) }
    } ?: emptyList()
}
