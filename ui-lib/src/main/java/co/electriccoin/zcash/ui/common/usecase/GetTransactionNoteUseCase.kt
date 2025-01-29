package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GetTransactionNoteUseCase(
    private val metadataRepository: MetadataRepository,
) {
    suspend operator fun invoke(txId: String) = observe(txId).first()

    fun observe(txId: String) =
        metadataRepository.metadata
            .filterNotNull()
            .map {
                it.transactions
                    .find { transaction -> transaction.txId == txId }
                    ?.notes
                    ?.firstOrNull()
            }
            .distinctUntilChanged()
}
