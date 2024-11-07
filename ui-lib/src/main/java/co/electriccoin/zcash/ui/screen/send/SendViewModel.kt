package co.electriccoin.zcash.ui.screen.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.usecase.ObserveContactByAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactPickedUseCase
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookArgs
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.send.model.RecipientAddressState
import co.electriccoin.zcash.ui.screen.send.model.SendAddressBookState
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
                            onButtonClick = { onButtonClick(mode, recipientAddressState) }
                        )
                    )

                    if (isHintVisible) {
                        delay(3.seconds)
                        emit(
                            SendAddressBookState(
                                mode = mode,
                                isHintVisible = false,
                                onButtonClick = { onButtonClick(mode, recipientAddressState) }
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
                    onButtonClick(
                        mode = SendAddressBookState.Mode.PICK_FROM_ADDRESS_BOOK,
                        recipient = recipientAddressState.value
                    )
                }
            )
        )

    private fun onButtonClick(
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

    init {
        viewModelScope.launch {
            observeContactPicked().collect {
                onRecipientAddressChanged(it)
            }
        }
    }

    fun onRecipientAddressChanged(state: RecipientAddressState) {
        recipientAddressState.update { state }
    }
}
