package co.electriccoin.zcash.ui.fixture

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.block.CompactBlockProcessor
import cash.z.ecc.android.sdk.model.WalletBalance
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.screen.home.model.WalletSnapshot

@Suppress("MagicNumber")
object WalletSnapshotFixture {
    // Should fill in with non-empty values for better example values in tests and UI previews
    @Suppress("LongParameterList")
    fun new(
        status: Synchronizer.Status = Synchronizer.Status.SYNCED,
        processorInfo: CompactBlockProcessor.ProcessorInfo = CompactBlockProcessor.ProcessorInfo(),
        orchardBalance: WalletBalance = WalletBalance(Zatoshi(5), Zatoshi(2)),
        saplingBalance: WalletBalance = WalletBalance(Zatoshi(4), Zatoshi(4)),
        transparentBalance: WalletBalance = WalletBalance(Zatoshi(8), Zatoshi(1)),
        pendingCount: Int = 0
    ) = WalletSnapshot(status, processorInfo, orchardBalance, saplingBalance, transparentBalance, pendingCount)
}
