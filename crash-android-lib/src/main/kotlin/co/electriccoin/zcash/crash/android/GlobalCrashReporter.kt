package co.electriccoin.zcash.crash.android

import android.content.Context
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import co.electriccoin.zcash.crash.android.internal.CrashReporter
import co.electriccoin.zcash.crash.android.internal.ListCrashReporters
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.spackle.process.ProcessNameCompat
import java.util.Collections

object GlobalCrashReporter {
    internal const val CRASH_PROCESS_NAME_SUFFIX = ":crash" // $NON-NLS

    private val intrinsicLock = Any()

    @Volatile
    private var registeredCrashReporters: List<CrashReporter>? = null

    /**
     * Call to register detection of uncaught exceptions and enable reporting of caught exceptions.
     *
     * @return True if registration occurred and false if registration was skipped.
     */
    @MainThread
    fun register(
        context: Context,
        reporters: ListCrashReporters
    ): Boolean {
        if (isCrashProcess(context)) {
            Twig.debug { "Skipping registration for $CRASH_PROCESS_NAME_SUFFIX process" } // $NON-NLS
            return false
        }

        synchronized(intrinsicLock) {
            if (registeredCrashReporters == null) {
                registeredCrashReporters =
                    Collections.synchronizedList(
                        reporters.provideReporters(context)
                    )
            }
        }

        return true
    }

    /**
     * Report a caught exception, e.g. within a try-catch.
     *
     * Be sure to call [register] before calling this method.
     */
    @AnyThread
    fun reportCaughtException(exception: Throwable) {
        registeredCrashReporters?.forEach { it.reportCaughtException(exception) }
    }

    fun disableAndDelete() {
        registeredCrashReporters?.forEach { it.disableAndDelete() }
    }

    fun enable() {
        registeredCrashReporters?.forEach { it.enable() }
    }
}

private fun isCrashProcess(context: Context) =
    ProcessNameCompat
        .getProcessName(context)
        .endsWith(GlobalCrashReporter.CRASH_PROCESS_NAME_SUFFIX)
