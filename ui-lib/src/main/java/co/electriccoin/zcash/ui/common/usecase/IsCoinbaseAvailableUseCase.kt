package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.BuildConfig
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider

class IsCoinbaseAvailableUseCase(
    private val getVersionInfo: GetVersionInfoProvider,
) {
    operator fun invoke(): Boolean {
        val versionInfo = getVersionInfo()
        val isDebug = versionInfo.let { it.isDebuggable && !it.isRunningUnderTestService }
        return !versionInfo.isTestnet && (BuildConfig.ZCASH_COINBASE_APP_ID.isNotEmpty() || isDebug)
    }
}
