package co.electriccoin.zcash.crash.android.internal.local

import android.content.Context
import androidx.annotation.AnyThread
import co.electriccoin.zcash.crash.ReportableException
import co.electriccoin.zcash.crash.android.internal.CrashReporter
import co.electriccoin.zcash.spackle.LazyWithArgument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Registers an exception handler to write exceptions to disk.
 */
internal class LocalCrashReporter(
    private val applicationContext: Context
) : CrashReporter {
    private val crashReportingScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @AnyThread
    override fun reportCaughtException(exception: Throwable) {
        crashReportingScope.launch {
            AndroidExceptionReporter.reportException(
                applicationContext,
                ReportableException.new(applicationContext, exception, false)
            )
        }
    }

    override fun enable() {
        // Noop, because there's no privacy implication for locally stored data
    }

    override fun disableAndDelete() {
        // Noop, because there's no privacy implication for locally stored data
    }

    companion object {
        private val lazyWithArgument =
            LazyWithArgument<Context, CrashReporter> {
                AndroidUncaughtExceptionHandler.register(it)
                LocalCrashReporter(it.applicationContext)
            }

        fun getInstance(context: Context): CrashReporter = lazyWithArgument.getInstance(context)
    }
}
