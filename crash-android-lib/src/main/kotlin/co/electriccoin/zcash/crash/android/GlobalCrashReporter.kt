package co.electriccoin.zcash.crash.android

import android.content.Context
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import co.electriccoin.zcash.crash.android.internal.CrashReporter
import co.electriccoin.zcash.crash.android.internal.FirebaseCrashReporter
import co.electriccoin.zcash.crash.android.internal.LocalCrashReporter
import java.util.Collections

object GlobalCrashReporter {

    private val intrinsicLock = Any()

    @Volatile
    private var registeredCrashReporters: List<CrashReporter>? = null

    /**
     * Call to register detection of uncaught exceptions and enable reporting of caught exceptions.
     */
    @MainThread
    fun register(context: Context) {
        synchronized(intrinsicLock) {
            if (registeredCrashReporters == null) {
                registeredCrashReporters = Collections.synchronizedList(
                    listOfNotNull(
                        // Ordering is important here; we want our exception handler to come second so that it can
                        // run before Firebase's exception handler.
                        FirebaseCrashReporter.getInstance(context),
                        LocalCrashReporter.getInstance(context)
                    )
                )
            }
        }
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
}
