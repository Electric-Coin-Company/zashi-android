package co.electriccoin.zcash.crash.android.internal

import android.content.Context

interface ListCrashReporters {
    fun provideReporters(context: Context): List<CrashReporter>
}
