package co.electriccoin.zcash.ui.screen.swap.receiver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapTokenChain
import co.electriccoin.zcash.ui.common.usecase.CancelSwapUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedTokenChainUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToAddressBookUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactByAddressUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.PickerState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookArgs
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.swap.amount.SwapAmount
import co.electriccoin.zcash.ui.screen.swap.receiver.picker.SwapReceiverPicker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SwapReceiverViewModel(
    getSelectedTokenChain: GetSelectedTokenChainUseCase,
    private val cancelSwap: CancelSwapUseCase,
    private val navigationRouter: NavigationRouter,
    private val observeContactByAddress: ObserveContactByAddressUseCase,
    private val navigateToAddressBook: NavigateToAddressBookUseCase
) : ViewModel() {
    private val addressText = MutableStateFlow("")

    private val address =
        addressText
            .map { text ->
                createAddressState(text)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = createAddressState(addressText.value)
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val isAbleToAddContact =
        addressText
            .flatMapLatest { text ->
                if (text.isBlank()) {
                    flowOf(false)
                } else {
                    observeContactByAddress(text).mapLatest { it == null }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = false
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val isAddressBookHintVisible =
        isAbleToAddContact
            .flatMapLatest {
                if (it) {
                    flow {
                        emit(true)
                        delay(3.seconds)
                        emit(false)
                    }
                } else {
                    flowOf(false)
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = false
            )

    private val addressBookButtonState =
        isAbleToAddContact
            .map { isAbleToAdd ->
                createAddressBookButtonState(isAbleToAdd)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = createAddressBookButtonState(isAbleToAddContact.value)
            )

    val state: StateFlow<SwapReceiverState> =
        combine(
            getSelectedTokenChain.observe(),
            address,
            addressBookButtonState,
            isAddressBookHintVisible
        ) { tokenChain, address, addressBookButton, isAddressBookHintVisible ->
            createState(
                tokenChain = tokenChain,
                addressState = address,
                addressBookButton = addressBookButton,
                isAddressBookHintVisible = isAddressBookHintVisible
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue =
                createState(
                    tokenChain = getSelectedTokenChain.observe().value,
                    addressState = address.value,
                    addressBookButton = addressBookButtonState.value,
                    isAddressBookHintVisible = isAddressBookHintVisible.value
                )
        )

    private fun createState(
        tokenChain: SwapTokenChain?,
        addressState: TextFieldState,
        addressBookButton: IconButtonState,
        isAddressBookHintVisible: Boolean
    ): SwapReceiverState =
        SwapReceiverState(
            address = addressState,
            chainToken =
                PickerState(
                    icon = tokenChain?.tokenIcon,
                    badge = tokenChain?.chainIcon,
                    text = tokenChain?.tokenTicker?.let { stringRes(it) },
                    placeholder = stringRes("Select..."),
                    onClick = ::onSelectChainTokenClick
                ),
            isAddressBookHintVisible = isAddressBookHintVisible,
            addressBookButton = addressBookButton,
            qrScannerButton =
                IconButtonState(
                    icon = R.drawable.qr_code_icon,
                    onClick = ::onQrScannerClick
                ),
            positiveButton =
                ButtonState(
                    text = stringRes("Next"),
                    isEnabled = addressState.error == null && tokenChain != null,
                    onClick = ::onPositiveClick
                ),
            onBack = ::onBack,
        )

    private fun createAddressState(text: String) =
        TextFieldState(
            error =
                when {
                    text.isEmpty() -> null
                    text.isBlank() -> stringRes("")
                    else -> null
                },
            value = stringRes(text),
            onValueChange = ::onAddressChange
        )

    private fun createAddressBookButtonState(isAbleToAdd: Boolean) =
        if (isAbleToAdd) {
            IconButtonState(
                icon = R.drawable.send_address_book_plus,
                onClick = { onSaveAddressBookContact(addressText.value) }
            )
        } else {
            IconButtonState(
                icon = R.drawable.send_address_book,
                onClick = ::onPickAddressBookContact
            )
        }

    private fun onBack() = cancelSwap()

    private fun onSelectChainTokenClick() = navigationRouter.forward(SwapReceiverPicker)

    private fun onPickAddressBookContact() =
        viewModelScope.launch { navigateToAddressBook(AddressBookArgs.PICK_CONTACT) }

    private fun onSaveAddressBookContact(address: String) = navigationRouter.forward(AddContactArgs(address))

    @Suppress("ForbiddenComment")
    private fun onQrScannerClick() {
        // TODO swap
    }

    private fun onPositiveClick() = navigationRouter.forward(SwapAmount)

    private fun onAddressChange(new: String) = addressText.update { new }
}
