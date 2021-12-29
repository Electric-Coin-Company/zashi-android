package cash.z.ecc.ui.fixture

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.block.CompactBlockProcessor
import cash.z.ecc.android.sdk.type.WalletBalance
import cash.z.ecc.ui.screen.home.model.WalletSnapshot

@Suppress("MagicNumber")
object WalletSnapshotFixture {
    // Should fill in with non-empty values for better example values in tests and UI previews
    @Suppress("LongParameterList")
    fun new(
        status: Synchronizer.Status = Synchronizer.Status.SYNCED,
        processorInfo: CompactBlockProcessor.ProcessorInfo = CompactBlockProcessor.ProcessorInfo(),
        orchardBalance: WalletBalance = WalletBalance(5, 2),
        saplingBalance: WalletBalance = WalletBalance(4, 4),
        transparentBalance: WalletBalance = WalletBalance(8, 1),
        pendingCount: Int = 0
    ) = WalletSnapshot(status, processorInfo, orchardBalance, saplingBalance, transparentBalance, pendingCount)
}
