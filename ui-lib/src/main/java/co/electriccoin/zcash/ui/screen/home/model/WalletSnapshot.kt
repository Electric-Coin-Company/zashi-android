package co.electriccoin.zcash.ui.screen.home.model

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.block.CompactBlockProcessor
import cash.z.ecc.android.sdk.ext.ZcashSdk
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.type.WalletBalance
import cash.z.ecc.sdk.model.PercentDecimal
import co.electriccoin.zcash.ui.screen.home.viewmodel.SynchronizerError

// TODO [#292]: Should be moved to SDK-EXT-UI module.
data class WalletSnapshot(
    val status: Synchronizer.Status,
    val processorInfo: CompactBlockProcessor.ProcessorInfo,
    val orchardBalance: WalletBalance,
    val saplingBalance: WalletBalance,
    val transparentBalance: WalletBalance,
    val pendingCount: Int,
    val progress: PercentDecimal,
    val synchronizerError: SynchronizerError?
) {
    // Note: the wallet is effectively empty if it cannot cover the miner's fee
    val hasFunds = saplingBalance.available.value >
        (ZcashSdk.MINERS_FEE.value.toDouble() / Zatoshi.ZATOSHI_PER_ZEC) // 0.00001
    val hasSaplingBalance = saplingBalance.total.value > 0

    val isSendEnabled: Boolean get() = status == Synchronizer.Status.SYNCED && hasFunds

    // TODO this will go away before PR merged
    override fun toString(): String {
        return "WalletSnapshot(" +
            "status=$status," +
            " processorInfo=$processorInfo," +
            " orchardBalance=$orchardBalance," +
            " saplingBalance=$saplingBalance," +
            " transparentBalance=$transparentBalance," +
            " pendingCount=$pendingCount," +
            " hasFunds=$hasFunds," +
            " hasSaplingBalance=$hasSaplingBalance," +
            " isSendEnabled=$isSendEnabled," +
            " synchronizerError=$synchronizerError," +
            " progress=${progress.decimal})"
    }
}

fun WalletSnapshot.totalBalance() = orchardBalance.total + saplingBalance.total + transparentBalance.total

// Note that considering both to be spendable is subject to change.
// The user experience could be confusing, and in the future we might prefer to ask users
// to transfer their balance to the latest balance type to make it spendable.
fun WalletSnapshot.spendableBalance() = orchardBalance.available + saplingBalance.available
