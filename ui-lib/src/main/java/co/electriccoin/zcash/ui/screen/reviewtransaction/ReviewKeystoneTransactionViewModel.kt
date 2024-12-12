package co.electriccoin.zcash.ui.screen.reviewtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.repository.RegularTransactionProposal
import co.electriccoin.zcash.ui.common.repository.SendTransactionProposal
import co.electriccoin.zcash.ui.common.repository.Zip321TransactionProposal
import co.electriccoin.zcash.ui.common.usecase.CancelKeystoneProposalFlowUseCase
import co.electriccoin.zcash.ui.common.usecase.GetLoadedExchangeRateUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactByAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveKeystoneSendTransactionProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ZashiChipButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.addressbook.viewmodel.ADDRESS_MAX_LENGTH
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.SignKeystoneTransaction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ReviewKeystoneTransactionViewModel(
    observeContactByAddress: ObserveContactByAddressUseCase,
    observeSelectedWalletAccount: ObserveSelectedWalletAccountUseCase,
    observeKeystoneSendTransactionProposal: ObserveKeystoneSendTransactionProposalUseCase,
    private val cancelKeystoneProposalFlow: CancelKeystoneProposalFlowUseCase,
    private val getLoadedExchangeRate: GetLoadedExchangeRateUseCase,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    private val isReceiverExpanded = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state =
        combine(
            observeSelectedWalletAccount.require(),
            observeKeystoneSendTransactionProposal(),
            isReceiverExpanded
        ) { wallet, zecSend, isReceiverExpanded ->
            Triple(wallet, zecSend, isReceiverExpanded)
        }.flatMapLatest { (wallet, proposal, isReceiverExpanded) ->
            observeContactByAddress(proposal?.destination?.address.orEmpty()).map { addressBookContact ->
                when (proposal) {
                    is RegularTransactionProposal ->
                        createState(
                            transactionProposal = proposal,
                            addressBookContact = addressBookContact,
                            wallet = wallet
                        )
                    is Zip321TransactionProposal ->
                        createZip321State(
                            transactionProposal = proposal,
                            addressBookContact = addressBookContact,
                            wallet = wallet,
                            isReceiverExpanded = isReceiverExpanded
                        )
                    null -> null
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private suspend fun createState(
        transactionProposal: SendTransactionProposal,
        addressBookContact: AddressBookContact?,
        wallet: WalletAccount
    ) = ReviewTransactionState(
        title = stringRes(R.string.review_keystone_transaction_title),
        items =
            listOfNotNull(
                AmountState(
                    title = stringRes(R.string.send_confirmation_amount),
                    amount = transactionProposal.amount,
                    exchangeRate = getLoadedExchangeRate(),
                ),
                ReceiverState(
                    title = stringRes(R.string.send_confirmation_address),
                    name = addressBookContact?.name?.let { stringRes(it) },
                    address = stringRes(transactionProposal.destination.address)
                ),
                SenderState(
                    title = stringRes(R.string.send_confirmation_address_from),
                    icon = wallet.icon,
                    name = wallet.name
                ),
                FinancialInfoState(
                    title = stringRes(R.string.send_amount_label),
                    amount = transactionProposal.amount
                ),
                FinancialInfoState(
                    title = stringRes(R.string.send_confirmation_fee),
                    amount = transactionProposal.proposal.totalFeeRequired()
                ),
                transactionProposal.memo.takeIf { it.value.isNotEmpty() }
                    ?.let {
                        MessageState(
                            title = stringRes(R.string.send_memo_label),
                            message = stringRes(it.value)
                        )
                    }
                    ?.takeIf { transactionProposal.destination !is WalletAddress.Transparent },
                MessagePlaceholderState(
                    title = stringRes(R.string.send_memo_label),
                    message = stringRes(R.string.send_transparent_memo),
                    icon = R.drawable.ic_confirmation_message_info,
                ).takeIf { transactionProposal.destination is WalletAddress.Transparent },
            ),
        primaryButton =
            ButtonState(
                stringRes(R.string.review_keystone_transaction_positive),
                onClick = ::onConfirmClick
            ),
        negativeButton =
            ButtonState(
                stringRes(R.string.review_keystone_transaction_negative),
                onClick = ::onCancelClick
            ),
        onBack = ::onBack,
    )

    private suspend fun createZip321State(
        transactionProposal: SendTransactionProposal,
        addressBookContact: AddressBookContact?,
        wallet: WalletAccount,
        isReceiverExpanded: Boolean,
    ) = ReviewTransactionState(
        title = stringRes(R.string.payment_request_title),
        items =
            listOfNotNull(
                AmountState(
                    title = null,
                    amount = transactionProposal.amount,
                    exchangeRate = getLoadedExchangeRate(),
                ),
                SenderState(
                    title = stringRes(R.string.send_confirmation_address_from),
                    icon = wallet.icon,
                    name = wallet.name
                ),
                ReceiverExpandedState(
                    title = stringRes(R.string.payment_request_requested_by),
                    name = addressBookContact?.name?.let { stringRes(it) },
                    address =
                        if (isReceiverExpanded) {
                            stringRes(transactionProposal.destination.address)
                        } else {
                            stringRes("${transactionProposal.destination.address.take(ADDRESS_MAX_LENGTH)}...")
                        },
                    showButton =
                        ZashiChipButtonState(
                            icon = if (isReceiverExpanded) R.drawable.ic_chevron_up else R.drawable.ic_chevron_down,
                            text = stringRes(R.string.payment_request_btn_show_address),
                            onClick = ::onExpandReceiverClick
                        ),
                    saveButton =
                        ZashiChipButtonState(
                            icon = R.drawable.ic_user_plus,
                            text = stringRes(R.string.payment_request_btn_save_contact),
                            onClick = { onAddContactClick(transactionProposal.destination.address) }
                        ).takeIf { addressBookContact == null }
                ),
                transactionProposal.memo.takeIf { it.value.isNotEmpty() }?.let {
                    MessageState(
                        title = stringRes(R.string.payment_request_memo),
                        message = stringRes(it.value)
                    )
                },
                FinancialInfoState(
                    title = stringRes(R.string.payment_request_fee),
                    amount = transactionProposal.proposal.totalFeeRequired()
                )
            ),
        primaryButton =
            ButtonState(
                stringRes(R.string.review_keystone_transaction_positive),
                onClick = ::onConfirmClick
            ),
        negativeButton =
            ButtonState(
                stringRes(R.string.review_keystone_transaction_negative),
                onClick = ::onCancelClick
            ),
        onBack = ::onBack,
    )

    private fun onExpandReceiverClick() {
        isReceiverExpanded.update { !it }
    }

    private fun onBack() {
        cancelKeystoneProposalFlow(clearSendForm = false)
    }

    private fun onCancelClick() {
        cancelKeystoneProposalFlow(clearSendForm = false)
    }

    private fun onConfirmClick() {
        navigationRouter.forward(SignKeystoneTransaction)
    }

    private fun onAddContactClick(address: String) {
        navigationRouter.forward(AddContactArgs(address))
    }
}
