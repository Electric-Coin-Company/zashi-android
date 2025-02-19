package co.electriccoin.zcash.crash.android.internal

import android.content.Context
import co.electriccoin.zcash.crash.android.internal.local.LocalCrashReporter

class ListCrashReportersImpl : ListCrashReporters {
    override fun provideReporters(context: Context): List<CrashReporter> {
        // To prevent a race condition, register the LocalCrashReporter first.
        // FirebaseCrashReporter does some asynchronous registration internally, while
        // LocalCrashReporter uses AndroidUncaughtExceptionHandler which needs to read
        // and write the default UncaughtExceptionHandler.  The only way to ensure
        // interleaving doesn't happen is to register the LocalCrashReporter first.
        return listOfNotNull(
            LocalCrashReporter.getInstance(context),
        )
    }
}
