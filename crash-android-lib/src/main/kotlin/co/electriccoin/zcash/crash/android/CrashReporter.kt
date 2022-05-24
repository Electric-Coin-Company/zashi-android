package co.electriccoin.zcash.crash.android

import android.content.Context
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import co.electriccoin.zcash.crash.ReportableException
import co.electriccoin.zcash.crash.android.internal.AndroidExceptionReporter
import co.electriccoin.zcash.crash.android.internal.AndroidUncaughtExceptionHandler
import co.electriccoin.zcash.crash.android.internal.new
import co.electriccoin.zcash.spackle.Twig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object CrashReporter {

    private val crashReportingScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @Volatile
    private var applicationContext: Context? = null

    /**
     * Call to register detection of uncaught exceptions.
     *
     * This must should only be called once for the entire lifetime of an application's process.
     */
    @MainThread
    fun register(context: Context) {
        AndroidUncaughtExceptionHandler.register(context)

        applicationContext = context.applicationContext
    }

    /**
     * Report a caught exception, e.g. within a try-catch.
     *
     * Be sure to call [register] before calling this method.
     */
    @AnyThread
    fun reportCaughtException(exception: Throwable) {
        // This method relies on a global Context reference, because often Context is not available
        // in various places where we'd like to capture an exception from a try-catch.

        applicationContext?.let {
            crashReportingScope.launch {
                AndroidExceptionReporter.reportException(it, ReportableException.new(it, exception, false))
            }
        } ?: run {
            Twig.warn { "Unable to log exception; Call `register(Context)` prior to reportCaughtException(Throwable)" }
        }
    }
}
