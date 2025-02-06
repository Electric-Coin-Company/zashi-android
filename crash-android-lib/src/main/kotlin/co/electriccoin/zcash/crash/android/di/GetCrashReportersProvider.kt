package co.electriccoin.zcash.crash.android.di

import co.electriccoin.zcash.crash.android.internal.ListCrashReportersImpl

class GetCrashReportersProvider {
    operator fun invoke() = ListCrashReportersImpl()
}
