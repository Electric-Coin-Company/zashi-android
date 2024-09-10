package co.electriccoin.zcash.ui.common.provider

import android.app.Application
import co.electriccoin.zcash.ui.common.model.VersionInfo

class GetVersionInfoProvider(private val application: Application) {
    operator fun invoke() = VersionInfo.new(application)
}
