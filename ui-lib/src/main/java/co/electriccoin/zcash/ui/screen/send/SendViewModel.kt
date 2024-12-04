package co.electriccoin.zcash.ui.screen.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.proposeSend
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.common.usecase.GetSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.GetZashiSpendingKeyUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactByAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactPickedUseCase
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookArgs
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.reviewtransaction.ReviewKeystoneTransaction
import co.electriccoin.zcash.ui.screen.send.model.RecipientAddressState
import co.electriccoin.zcash.ui.screen.send.model.SendAddressBookState
import co.electriccoin.zcash.ui.screen.send.model.SendStage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SendViewModel(
    private val observeContactByAddress: ObserveContactByAddressUseCase,
    private val observeContactPicked: ObserveContactPickedUseCase,
    private val getZashiSpendingKey: GetZashiSpendingKeyUseCase,
    private val getSynchronizer: GetSynchronizerUseCase,
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val getSelectedWalletAccount: GetSelectedWalletAccountUseCase,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    val recipientAddressState = MutableStateFlow(RecipientAddressState.new("", null))

    val navigateCommand = MutableSharedFlow<String>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val sendAddressBookState =
        recipientAddressState.flatMapLatest { recipientAddressState ->
            observeContactByAddress(recipientAddressState.address).flatMapLatest { contact ->
                flow {
                    val exists = contact != null
                    val isValid = recipientAddressState.type?.isNotValid == false
                    val mode =
                        if (isValid) {
                            if (exists) {
                                SendAddressBookState.Mode.PICK_FROM_ADDRESS_BOOK
                            } else {
                                SendAddressBookState.Mode.ADD_TO_ADDRESS_BOOK
                            }
                        } else {
                            SendAddressBookState.Mode.PICK_FROM_ADDRESS_BOOK
                        }
                    val isHintVisible = !exists && isValid

                    emit(
                        SendAddressBookState(
                            mode = mode,
                            isHintVisible = isHintVisible,
                            onButtonClick = { onAddressBookButtonClicked(mode, recipientAddressState) }
                        )
                    )

                    if (isHintVisible) {
                        delay(3.seconds)
                        emit(
                            SendAddressBookState(
                                mode = mode,
                                isHintVisible = false,
                                onButtonClick = { onAddressBookButtonClicked(mode, recipientAddressState) }
                            )
                        )
                    }
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            SendAddressBookState(
                mode = SendAddressBookState.Mode.PICK_FROM_ADDRESS_BOOK,
                isHintVisible = false,
                onButtonClick = {
                    onAddressBookButtonClicked(
                        mode = SendAddressBookState.Mode.PICK_FROM_ADDRESS_BOOK,
                        recipient = recipientAddressState.value
                    )
                }
            )
        )

    init {
        viewModelScope.launch {
            observeContactPicked().collect {
                onRecipientAddressChanged(it)
            }
        }
    }

    private fun onAddressBookButtonClicked(
        mode: SendAddressBookState.Mode,
        recipient: RecipientAddressState
    ) {
        when (mode) {
            SendAddressBookState.Mode.PICK_FROM_ADDRESS_BOOK ->
                viewModelScope.launch {
                    navigateCommand.emit(AddressBookArgs(AddressBookArgs.PICK_CONTACT))
                }

            SendAddressBookState.Mode.ADD_TO_ADDRESS_BOOK ->
                viewModelScope.launch {
                    navigateCommand.emit(AddContactArgs(recipient.address))
                }
        }
    }

    fun onRecipientAddressChanged(state: RecipientAddressState) {
        recipientAddressState.update { state }
    }

    fun onCreateZecSendClick(
        newZecSend: ZecSend,
        setZecSend: (ZecSend) -> Unit,
        goSendConfirmation: (ZecSend) -> Unit,
        setSendStage: (SendStage) -> Unit
    ) = viewModelScope.launch {
        when (getSelectedWalletAccount()) {
            is KeystoneAccount -> if (keystoneProposalRepository.createProposal(newZecSend)) {
                navigationRouter.forward(ReviewKeystoneTransaction)
            } else {
                setSendStage(SendStage.SendFailure(""))
            }
            is ZashiAccount -> {
                Twig.debug { "Getting send transaction proposal" }
                runCatching {
                    getSynchronizer().proposeSend(getZashiSpendingKey().account, newZecSend)
                }.onSuccess { proposal ->
                    Twig.debug { "Transaction proposal successful: ${proposal.toPrettyString()}" }
                    val enrichedZecSend = newZecSend.copy(proposal = proposal)
                    setZecSend(enrichedZecSend)
                    goSendConfirmation(enrichedZecSend)
                }.onFailure {
                    Twig.error(it) { "Transaction proposal failed" }
                    setSendStage(SendStage.SendFailure(it.message ?: ""))
                }
            }
        }
    }
}
