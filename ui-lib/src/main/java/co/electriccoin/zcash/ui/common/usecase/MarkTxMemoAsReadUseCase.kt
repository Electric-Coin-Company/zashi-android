package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.MetadataRepository

class MarkTxMemoAsReadUseCase(
    private val metadataRepository: MetadataRepository
) {
    suspend fun invoke(txId: String) {
        metadataRepository.markTxMemoAsRead(txId)
    }
}
