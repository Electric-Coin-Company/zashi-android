package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class FlipTransactionBookmarkUseCase(
    private val metadataRepository: MetadataRepository,
) {
    suspend operator fun invoke(txId: String) {
        val isBookmark =
            metadataRepository.metadata
                .filterNotNull()
                .first()
                .transactions.find { transaction -> transaction.txId == txId }?.isBookmark ?: false

        metadataRepository.markTxAsBookmark(txId, !isBookmark)
    }
}
