package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.Zatoshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PrefillSendUseCase {
    private val bus = Channel<PrefillSendData>()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    operator fun invoke() = bus.receiveAsFlow()

    fun request(value: DetailedTransactionData) =
        scope.launch {
            bus.send(
                PrefillSendData.All(
                    amount = value.transaction.amount,
                    address = value.recipientAddress?.address,
                    fee = value.transaction.fee,
                    memos = value.memos
                )
            )
        }

    fun request(value: PrefillSendData) =
        scope.launch {
            bus.send(value)
        }
}

sealed interface PrefillSendData {
    data class All(
        val amount: Zatoshi,
        val address: String?,
        val fee: Zatoshi?,
        val memos: List<String>?,
    ) : PrefillSendData

    data class FromAddressScan(val address: String) : PrefillSendData
}
