package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.MetadataRepository

class FlipTransactionBookmarkUseCase(
    private val metadataRepository: MetadataRepository,
) {
    suspend operator fun invoke(txId: String) {
        metadataRepository.flipTxBookmark(txId)
    }
}
