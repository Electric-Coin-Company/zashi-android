package co.electriccoin.zcash.ui.common.model

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.block.processor.CompactBlockProcessor
import cash.z.ecc.android.sdk.ext.ZcashSdk
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
    val transparentBalance: WalletBalance,
    val progress: PercentDecimal,
    val synchronizerError: SynchronizerError?
) {
    // Note: the wallet is effectively empty if it cannot cover the miner's fee
    val hasFunds =
        saplingBalance.available.value >
            (ZcashSdk.MINERS_FEE.value.toDouble() / Zatoshi.ZATOSHI_PER_ZEC) // 0.00001
    val hasSaplingBalance = saplingBalance.total.value > 0

    val isSendEnabled: Boolean get() = status == Synchronizer.Status.SYNCED && hasFunds
}

fun WalletSnapshot.totalBalance() = orchardBalance.total + saplingBalance.total + transparentBalance.total

// Note that considering both to be spendable is subject to change.
// The user experience could be confusing, and in the future we might prefer to ask users
// to transfer their balance to the latest balance type to make it spendable.
fun WalletSnapshot.spendableBalance() = orchardBalance.available + saplingBalance.available
