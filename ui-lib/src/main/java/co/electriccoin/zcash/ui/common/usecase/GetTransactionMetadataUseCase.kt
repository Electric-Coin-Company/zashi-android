package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest

class GetTransactionMetadataUseCase(
    private val metadataRepository: MetadataRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(txId: String) = observe(txId).first()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe(txId: String) =
        transactionRepository
            .observeTransaction(txId)
            .filterNotNull()
            .flatMapLatest {
                metadataRepository.observeTransactionMetadata(it)
            }
}
