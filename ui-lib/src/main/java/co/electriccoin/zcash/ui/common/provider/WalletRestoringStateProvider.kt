package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.IntegerPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import co.electriccoin.zcash.ui.common.model.WalletRestoringState

interface WalletRestoringStateProvider : StorageProvider<WalletRestoringState>

class WalletRestoringStateProviderImpl(
    override val preferenceHolder: StandardPreferenceProvider,
) : BaseStorageProvider<WalletRestoringState>(),
    WalletRestoringStateProvider {
    override val default: PreferenceDefault<WalletRestoringState> = WalletRestoringStatePreferenceDefault()
}

private class WalletRestoringStatePreferenceDefault : PreferenceDefault<WalletRestoringState> {
    private val internal = WALLET_RESTORING_STATE

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

/**
 * State defining whether the current block synchronization run is in the restoring state or a subsequent
 * synchronization state.
 */
private val WALLET_RESTORING_STATE =
    IntegerPreferenceDefault(
        PreferenceKey("wallet_restoring_state"),
        WalletRestoringState.RESTORING.toNumber()
    )
