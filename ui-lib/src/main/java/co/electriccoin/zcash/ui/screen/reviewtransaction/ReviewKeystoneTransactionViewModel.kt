package co.electriccoin.zcash.ui.screen.reviewtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.common.usecase.GetLoadedExchangeRateUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactByAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.KeystoneSignTransaction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ReviewKeystoneTransactionViewModel(
    keystoneProposalRepository: KeystoneProposalRepository,
    observeContactByAddress: ObserveContactByAddressUseCase,
    observeSelectedWalletAccount: ObserveSelectedWalletAccountUseCase,
    private val getLoadedExchangeRate: GetLoadedExchangeRateUseCase,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val state =
        combine(observeSelectedWalletAccount(), keystoneProposalRepository.completeZecSend) { wallet, zecSend ->
            wallet to zecSend
        }.flatMapLatest { (wallet, completeZecSend) ->
            observeContactByAddress(completeZecSend?.destination?.address.orEmpty()).map { addressBookContact ->
                ReviewTransactionState(
                    title = stringRes("Review"), // TODO keystone string
                    items = listOfNotNull(
                        AmountState(
                            title = stringRes("Total Amount"), // TODO keystone string
                            amount = completeZecSend?.amount ?: Zatoshi(0),
                            exchangeRate = getLoadedExchangeRate(),
                        ),
                        ReceiverState(
                            title = stringRes("Sending to"), // TODO keystone string
                            name = addressBookContact?.name?.let { stringRes(it) },
                            address = stringRes(completeZecSend?.destination?.address.orEmpty())
                        ),
                        SenderState(
                            title = stringRes("Sending from"), // TODO keystone string
                            icon = wallet.icon,
                            name = wallet.name
                        ),
                        FinancialInfoState(
                            title = stringRes("Amount"), // TODO keystone string
                            amount = completeZecSend?.amount ?: Zatoshi(0)
                        ),
                        FinancialInfoState(
                            title = stringRes("Fee"), // TODO keystone string
                            amount = completeZecSend?.proposal?.totalFeeRequired() ?: Zatoshi(0)
                        ),
                        completeZecSend?.memo?.takeIf { it.value.isNotEmpty() }?.let {
                            MessageState(
                                title = stringRes("Message"), // TODO keystone string
                                message = stringRes(it.value)
                            )
                        }
                    ),
                    primaryButton = ButtonState(
                        stringRes("Confirm with Keystone"), // TODO keystone string
                        onClick = ::onConfirmClick
                    ),
                    negativeButton = ButtonState(
                        stringRes("Cancel"), // TODO keystone string
                        onClick = ::onCancelClick
                    ),
                    onBack = ::onBack,
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)

    private fun onBack() {
        navigationRouter.backToRoot()
    }

    private fun onCancelClick() {
        navigationRouter.backToRoot()
    }

    private fun onConfirmClick() {
        navigationRouter.forward(KeystoneSignTransaction)
    }
}