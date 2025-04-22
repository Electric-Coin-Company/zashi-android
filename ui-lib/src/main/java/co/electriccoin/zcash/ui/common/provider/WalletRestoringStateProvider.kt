package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys

interface WalletRestoringStateProvider : StorageProvider<WalletRestoringState>

class WalletRestoringStateProviderImpl(
    override val preferenceHolder: StandardPreferenceProvider,
) : BaseStorageProvider<WalletRestoringState>(),
    WalletRestoringStateProvider {
    override val default: PreferenceDefault<WalletRestoringState> = WalletRestoringStatePreferenceDefault()
}

private class WalletRestoringStatePreferenceDefault : PreferenceDefault<WalletRestoringState> {
    private val internal = StandardPreferenceKeys.WALLET_RESTORING_STATE

    override val key: PreferenceKey = internal.key

    override suspend fun getValue(preferenceProvider: PreferenceProvider): WalletRestoringState =
        WalletRestoringState.fromNumber(internal.getValue(preferenceProvider))

    override suspend fun putValue(preferenceProvider: PreferenceProvider, newValue: WalletRestoringState) {
        internal.putValue(
            preferenceProvider = preferenceProvider,
            newValue = newValue.toNumber()
        )
    }
}
