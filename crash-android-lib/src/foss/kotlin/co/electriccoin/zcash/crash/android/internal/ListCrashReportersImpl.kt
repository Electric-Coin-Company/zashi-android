package co.electriccoin.zcash.crash.android.internal

import android.content.Context
import co.electriccoin.zcash.crash.android.internal.local.LocalCrashReporter

class ListCrashReportersImpl : ListCrashReporters {
    override fun provideReporters(context: Context): List<CrashReporter> =
        listOfNotNull(
            LocalCrashReporter.getInstance(context),
        )
}
