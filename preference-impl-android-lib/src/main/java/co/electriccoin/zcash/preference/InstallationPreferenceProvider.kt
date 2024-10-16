package co.electriccoin.zcash.preference

import android.content.Context
import co.electriccoin.zcash.preference.api.PreferenceProvider

class InstallationPreferenceProvider(private val context: Context) : PreferenceHolder() {
    override suspend fun create(): PreferenceProvider {
        return AndroidPreferenceProvider.newStandard(context, "co.electriccoin.zcash.installation")
    }
}
