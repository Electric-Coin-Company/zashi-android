package co.electriccoin.zcash.crash.android.internal

import androidx.annotation.AnyThread

interface CrashReporter {

    /**
     * Report a caught exception, e.g. within a try-catch.
     */
    @AnyThread
    fun reportCaughtException(exception: Throwable)
}
