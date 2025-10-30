package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.EphemeralAddressRepository
import co.electriccoin.zcash.ui.common.repository.SendTransaction
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.lang.IllegalStateException

class IsEphemeralAddressLockedUseCase(
    private val transactionRepository: TransactionRepository,
    private val ephemeralAddressRepository: EphemeralAddressRepository
) {
    suspend operator fun invoke(): EphemeralLockState {
        val ephemeral = ephemeralAddressRepository.get()
            ?: throw IllegalStateException("Ephemeral address is null")

        val transactions = transactionRepository.getTransactions()

        return if (ephemeral.gapLimit - ephemeral.gapPosition > 1u) {
            EphemeralLockState.UNLOCKED
        } else {
            val unlockTransaction = transactions
                .filterIsInstance<SendTransaction>()
                .find { it.recipient?.address == ephemeral.address }

            when (unlockTransaction) {
                is SendTransaction.Failed -> EphemeralLockState.LOCKED
                is SendTransaction.Pending -> EphemeralLockState.UNLOCKING
                is SendTransaction.Success -> EphemeralLockState.UNLOCKED
                null -> EphemeralLockState.LOCKED
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe() = ephemeralAddressRepository
        .observe()
        .filterNotNull()
        .flatMapLatest { ephemeral ->
            if (ephemeral.gapLimit - ephemeral.gapPosition > 1u) {
                flowOf(false)
            } else {
                transactionRepository
                    .transactions
                    .filterNotNull()
                    .map { transactions ->
                        transactions
                            .filterIsInstance<SendTransaction.Success>()
                            .none { it.recipient?.address == ephemeral.address }
                    }
            }
        }
}

enum class EphemeralLockState {
    LOCKED,
    UNLOCKING,
    UNLOCKED
}
