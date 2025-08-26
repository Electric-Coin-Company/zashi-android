package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.MetadataRepository

class DeleteTransactionNoteUseCase(
    private val metadataRepository: MetadataRepository,
    private val navigationRouter: NavigationRouter
) {
    operator fun invoke(txId: String) {
        metadataRepository.deleteTxNote(txId)
        navigationRouter.back()
    }
}
