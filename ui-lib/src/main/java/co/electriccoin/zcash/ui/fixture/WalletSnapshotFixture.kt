package co.electriccoin.zcash.ui.fixture

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.block.processor.CompactBlockProcessor
import cash.z.ecc.android.sdk.model.PercentDecimal
import cash.z.ecc.android.sdk.model.WalletBalance
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.viewmodel.SynchronizerError

@Suppress("MagicNumber")
object WalletSnapshotFixture {
    val STATUS = Synchronizer.Status.SYNCED
    val PROGRESS = PercentDecimal.ZERO_PERCENT
    val TRANSPARENT_BALANCE: WalletBalance = WalletBalance(Zatoshi(8), Zatoshi(1))
    val ORCHARD_BALANCE: WalletBalance = WalletBalance(Zatoshi(5), Zatoshi(2))
    val SAPLING_BALANCE: WalletBalance = WalletBalance(Zatoshi(4), Zatoshi(4))

    // Should fill in with non-empty values for better example values in tests and UI previews
    @Suppress("LongParameterList")
    fun new(
        status: Synchronizer.Status = STATUS,
        processorInfo: CompactBlockProcessor.ProcessorInfo =
            CompactBlockProcessor.ProcessorInfo(
                null,
                null,
                null
            ),
        orchardBalance: WalletBalance = ORCHARD_BALANCE,
        saplingBalance: WalletBalance = SAPLING_BALANCE,
        transparentBalance: WalletBalance = TRANSPARENT_BALANCE,
        progress: PercentDecimal = PROGRESS,
        synchronizerError: SynchronizerError? = null
    ) = WalletSnapshot(
        status,
        processorInfo,
        orchardBalance,
        saplingBalance,
        transparentBalance,
        progress,
        synchronizerError
    )
}
