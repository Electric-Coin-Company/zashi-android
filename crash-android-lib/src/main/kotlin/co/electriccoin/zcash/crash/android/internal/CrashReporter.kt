package co.electriccoin.zcash.crash.android.internal

import androidx.annotation.AnyThread

interface CrashReporter {

    /**
     * Report a caught exception, e.g. within a try-catch.
     */
    @AnyThread
    fun reportCaughtException(exception: Throwable)

    /**
     * Enables crash reporting that may have privacy implications.
     */
    fun enable()

    /**
     * Disables reporting and deletes any data that may have privacy implications.
     */
    fun disableAndDelete()
}
