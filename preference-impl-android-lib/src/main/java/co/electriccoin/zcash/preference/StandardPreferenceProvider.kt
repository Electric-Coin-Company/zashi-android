package co.electriccoin.zcash.preference

import android.content.Context
import co.electriccoin.zcash.preference.api.PreferenceProvider

class StandardPreferenceProvider(private val context: Context) : PreferenceHolder() {
    override suspend fun create(): PreferenceProvider =
        AndroidPreferenceProvider.newStandard(context, "co.electriccoin.zcash")
}
