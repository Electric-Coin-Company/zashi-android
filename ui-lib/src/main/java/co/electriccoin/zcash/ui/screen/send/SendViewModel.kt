package co.electriccoin.zcash.ui.screen.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.usecase.CreateProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToAddressBookUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactByAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactPickedUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveWalletAccountsUseCase
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookArgs
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.send.model.RecipientAddressState
import co.electriccoin.zcash.ui.screen.send.model.SendAddressBookState
import co.electriccoin.zcash.ui.screen.send.model.SendStage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SendViewModel(
    private val observeContactByAddress: ObserveContactByAddressUseCase,
    private val observeContactPicked: ObserveContactPickedUseCase,
    private val createProposal: CreateProposalUseCase,
    private val observeWalletAccounts: ObserveWalletAccountsUseCase,
    private val navigateToAddressBook: NavigateToAddressBookUseCase
) : ViewModel() {
    val recipientAddressState = MutableStateFlow(RecipientAddressState.new("", null))

    val navigateCommand = MutableSharedFlow<String>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val sendAddressBookState =
        recipientAddressState.flatMapLatest { recipientAddressState ->
            combine(observeWalletAccounts.require(), observeContactByAddress(recipientAddressState.address)) {
                    accounts, contact ->
                accounts to contact
            }.flatMapLatest { (accounts, contact) ->
                flow {
                    val exists =
                        contact != null ||
                            accounts.any { it.unified.address.address == recipientAddressState.address }
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
                    navigateToAddressBook(AddressBookArgs.PICK_CONTACT)
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

    @Suppress("TooGenericExceptionCaught")
    fun onCreateZecSendClick(
        newZecSend: ZecSend,
        setSendStage: (SendStage) -> Unit
    ) = viewModelScope.launch {
        try {
            createProposal(newZecSend)
        } catch (e: Exception) {
            setSendStage(SendStage.SendFailure(e.cause?.message ?: e.message ?: ""))
            Twig.error(e) { "Error creating proposal" }
        }
    }
}
