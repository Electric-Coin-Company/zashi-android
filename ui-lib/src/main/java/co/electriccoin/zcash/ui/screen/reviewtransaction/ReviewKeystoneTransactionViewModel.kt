package co.electriccoin.zcash.ui.screen.reviewtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.usecase.GetLoadedExchangeRateUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactByAddressUseCase
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ReviewKeystoneTransactionViewModel(
    args: ReviewKeystoneTransaction,
    observeContactByAddress: ObserveContactByAddressUseCase,
    private val getLoadedExchangeRate: GetLoadedExchangeRateUseCase
) : ViewModel() {
    val state = observeContactByAddress(args.addressString).map { addressBookContact ->
        ReviewTransactionState(
            title = stringRes("Review"),
            items = listOfNotNull(
                AmountState(
                    title = stringRes("Total Amount"),
                    amount = args.amount,
                    exchangeRate = getLoadedExchangeRate(),
                ),
                ReceiverState(
                    title = stringRes("Sending to"),
                    name = addressBookContact?.name?.let { stringRes(it) },
                    address = stringRes(args.addressString)
                ),
                SenderState(
                    title = stringRes("Sending from"),
                    icon = R.drawable.ic_item_keystone,
                    name = stringRes("Keystone wallet"),
                ),
                FinancialInfoState(
                    title = stringRes("Amount"),
                    amount = args.amount
                ),
                FinancialInfoState(
                    title = stringRes("Fee"),
                    amount = args.amount
                ),
                args.memo?.let {
                    MessageState(
                        title = stringRes("Message"),
                        message = stringRes(it.value)
                    )
                }
            ),
            primaryButton = ButtonState(
                stringRes("Confirm with Keystone"),
                onClick = ::onConfirmClick
            ),
            negativeButton = ButtonState(
                stringRes("Cancel"),
                onClick = ::onCancelClick
            ),
            onBack = ::onBack,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)

    private fun onBack() {
        TODO("Not yet implemented")
    }

    private fun onCancelClick() {
        TODO("Not yet implemented")
    }

    private fun onConfirmClick() {
        TODO("Not yet implemented")
    }
}