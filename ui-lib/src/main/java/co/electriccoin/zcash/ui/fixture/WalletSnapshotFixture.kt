package co.electriccoin.zcash.ui.fixture

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.block.processor.CompactBlockProcessor
import cash.z.ecc.android.sdk.fixture.WalletBalanceFixture
import cash.z.ecc.android.sdk.model.PercentDecimal
import cash.z.ecc.android.sdk.model.WalletBalance
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.viewmodel.SynchronizerError

@Suppress("MagicNumber")
object WalletSnapshotFixture {
    val STATUS = Synchronizer.Status.SYNCED
    val PROGRESS = PercentDecimal.ZERO_PERCENT
    val TRANSPARENT_BALANCE: Zatoshi = ZatoshiFixture.new(8)
    val ORCHARD_BALANCE: WalletBalance = WalletBalanceFixture.newLong(8, 2, 1)
    val SAPLING_BALANCE: WalletBalance = WalletBalanceFixture.newLong(5, 2, 1)

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
        transparentBalance: Zatoshi = TRANSPARENT_BALANCE,
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
