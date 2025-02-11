package co.electriccoin.zcash.crash.android.di

import co.electriccoin.zcash.crash.android.internal.ListCrashReportersImpl

class CrashReportersProvider {
    operator fun invoke() = ListCrashReportersImpl()
}
