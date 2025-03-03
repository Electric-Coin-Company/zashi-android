package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class GetTransactionMetadataUseCase(
    private val metadataRepository: MetadataRepository,
) {
    suspend operator fun invoke(txId: String) = observe(txId).filterNotNull().first()

    fun observe(txId: String) = metadataRepository.observeTransactionMetadataByTxId(txId)
}
