package co.electriccoin.zcash.ui.common.model

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.PercentDecimal
import co.electriccoin.zcash.ui.common.viewmodel.SynchronizerError

// TODO [#292]: Should be moved to SDK-EXT-UI module.
// TODO [#292]: https://github.com/Electric-Coin-Company/zashi-android/issues/292
data class WalletSnapshot(
    val status: Synchronizer.Status,
    val progress: PercentDecimal,
    val synchronizerError: SynchronizerError?,
    val isSpendable: Boolean,
    val restoringState: WalletRestoringState
)
