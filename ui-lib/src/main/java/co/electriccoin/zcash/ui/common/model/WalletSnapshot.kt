package co.electriccoin.zcash.ui.common.model

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.block.processor.CompactBlockProcessor
import cash.z.ecc.android.sdk.model.PercentDecimal
import cash.z.ecc.android.sdk.model.WalletBalance
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.viewmodel.SynchronizerError

// TODO [#292]: Should be moved to SDK-EXT-UI module.
// TODO [#292]: https://github.com/Electric-Coin-Company/zashi-android/issues/292
data class WalletSnapshot(
    val status: Synchronizer.Status,
    val processorInfo: CompactBlockProcessor.ProcessorInfo,
    val orchardBalance: WalletBalance,
    val saplingBalance: WalletBalance,
    val transparentBalance: Zatoshi,
    val progress: PercentDecimal,
    val synchronizerError: SynchronizerError?
) {
    // Note that both [hasSaplingFunds] and [hasTransparentFunds] checks are not entirely correct - they do not
    // calculate the resulting fee using the new Proposal API. It's fine for now, but it's subject to improvement
    // later once we figure out how to handle it in such cases.

    // Note: the wallet is effectively empty if it cannot cover the miner's fee
    val hasSaplingFunds = saplingBalance.available.value > 0L

    val hasSaplingBalance = saplingBalance.total.value > 0L

    // Note: the wallet's transparent balance is effectively empty if it cannot cover the miner's fee
    val hasTransparentFunds = transparentBalance.value > 0L

    val isSendEnabled: Boolean get() = status == Synchronizer.Status.SYNCED && hasSaplingFunds
}

fun WalletSnapshot.totalBalance() = orchardBalance.total + saplingBalance.total + transparentBalance

// Note that considering both to be spendable is subject to change.
// The user experience could be confusing, and in the future we might prefer to ask users
// to transfer their balance to the latest balance type to make it spendable.
fun WalletSnapshot.spendableBalance() = orchardBalance.available + saplingBalance.available

// Note that summing both values could be confusing, and we might prefer dividing them in the future
fun WalletSnapshot.changePendingBalance() = orchardBalance.changePending + saplingBalance.changePending

// Note that summing both values could be confusing, and we might prefer dividing them in the future
fun WalletSnapshot.valuePendingBalance() = orchardBalance.valuePending + saplingBalance.valuePending
