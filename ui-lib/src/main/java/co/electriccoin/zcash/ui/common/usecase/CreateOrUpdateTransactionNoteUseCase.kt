package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.MetadataRepository

class CreateOrUpdateTransactionNoteUseCase(
    private val metadataRepository: MetadataRepository,
    private val navigationRouter: NavigationRouter
) {
    suspend operator fun invoke(
        txId: String,
        note: String
    ) {
        metadataRepository.createOrUpdateTxNote(txId, note.trim())
        navigationRouter.back()
    }
}
