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
    val isZashi: Boolean,
    val status: Synchronizer.Status,
    val processorInfo: CompactBlockProcessor.ProcessorInfo,
    val orchardBalance: WalletBalance,
    val saplingBalance: WalletBalance?,
    val transparentBalance: Zatoshi,
    val progress: PercentDecimal,
    val synchronizerError: SynchronizerError?
)

// TODO [#1370]: WalletSnapshot.canSpend() calculation limitation
// TODO [#1370]: https://github.com/Electric-Coin-Company/zashi-android/issues/1370
// Note this check is not entirely correct - it does not calculate the resulting fee using the new Proposal API. It's
// fine for now, but it's subject to improvement later once we figure out how to handle it in such cases.
fun WalletSnapshot.canSpend(amount: Zatoshi): Boolean = spendableBalance() >= amount

fun WalletSnapshot.totalBalance() = orchardBalance.total + (saplingBalance?.total ?: Zatoshi(0)) + transparentBalance

// Note that considering both to be spendable is subject to change.
// The user experience could be confusing, and in the future we might prefer to ask users
// to transfer their balance to the latest balance type to make it spendable.
fun WalletSnapshot.spendableBalance() = orchardBalance.available + (saplingBalance?.available ?: Zatoshi(0))
