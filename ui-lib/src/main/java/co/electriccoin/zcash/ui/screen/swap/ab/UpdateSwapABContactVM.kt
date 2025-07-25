package co.electriccoin.zcash.ui.screen.swap.ab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapAssetBlockchain
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact
import co.electriccoin.zcash.ui.common.usecase.ContactAddressValidationResult
import co.electriccoin.zcash.ui.common.usecase.DeleteABContactUseCase
import co.electriccoin.zcash.ui.common.usecase.GetABContactByIdUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToSelectSwapBlockchainUseCase
import co.electriccoin.zcash.ui.common.usecase.UpdateABContactUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateABSwapContactAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateABSwapContactNameUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateContactNameResult
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.PickerState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.combine
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.contact.ABContactState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpdateSwapABContactVM(
    private val args: UpdateSwapABContactArgs,
    private val validateSwapContactAddress: ValidateABSwapContactAddressUseCase,
    private val validateSwapContactName: ValidateABSwapContactNameUseCase,
    private val updateContact: UpdateABContactUseCase,
    private val deleteContact: DeleteABContactUseCase,
    private val getContactByAddress: GetABContactByIdUseCase,
    private val navigationRouter: NavigationRouter,
    private val navigateToSelectSwapBlockchain: NavigateToSelectSwapBlockchainUseCase
) : ViewModel() {
    private val originalContact = MutableStateFlow<EnhancedABContact?>(null)
    private val contactAddress = MutableStateFlow("")
    private val contactName = MutableStateFlow("")
    private val selectedBlockchain = MutableStateFlow<SwapAssetBlockchain?>(null)

    private val isUpdatingContact = MutableStateFlow(false)
    private val isDeletingContact = MutableStateFlow(false)
    private val isLoadingContact = MutableStateFlow(true)

    private val blockChainPickerState = selectedBlockchain
        .map {
            PickerState(
                bigIcon = it?.chainIcon,
                smallIcon = null,
                text = it?.chainName,
                placeholder = stringRes("Select..."),
                onClick = ::onBlockchainClick
            )
        }

    private val contactAddressError =
        combine(originalContact, contactAddress, selectedBlockchain) { contact, address, blockchain ->
            if (address.isEmpty() || contact == null) {
                null
            } else {
                when (validateSwapContactAddress(address, blockchain, contact)) {
                    ContactAddressValidationResult.Invalid ->
                        stringRes(R.string.contact_address_error_invalid)

                    ContactAddressValidationResult.NotUnique ->
                        stringRes(R.string.contact_address_error_not_unique)

                    ContactAddressValidationResult.Valid -> null
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private val contactAddressState =
        combine(contactAddress, contactAddressError) { address, contactAddressError ->
            TextFieldState(
                value = stringRes(address),
                error = contactAddressError,
                onValueChange = { newValue ->
                    contactAddress.update { newValue }
                }
            )
        }

    private val contactNameError =
        combine(contactName, originalContact) { name, contact ->
            if (name.isEmpty() || contact == null) {
                null
            } else {
                when (validateSwapContactName(name = name, exclude = contact)) {
                    ValidateContactNameResult.TooLong ->
                        stringRes(R.string.contact_name_error_too_long)

                    ValidateContactNameResult.NotUnique ->
                        stringRes(R.string.contact_name_error_not_unique)

                    ValidateContactNameResult.Valid -> null
                }
            }
        }

    private val contactNameState =
        combine(contactName, contactNameError) { name, contactNameError ->
            TextFieldState(
                value = stringRes(name),
                error = contactNameError,
                onValueChange = { newValue ->
                    contactName.update { newValue }
                }
            )
        }

    private val updateButtonState =
        combine(
            contactAddressState,
            contactNameState,
            isUpdatingContact,
            originalContact,
            selectedBlockchain
        ) { address,
            name,
            isUpdatingContact,
            contact,
            blockchain
            ->
            val nameChanged = contactName.value.trim() != contact?.name
            val addressChanged = contactAddress.value.trim() != contact?.address
            val blockchainChanged = blockchain != contact?.blockchain
            ButtonState(
                text = stringRes(R.string.update_contact_primary_btn),
                isEnabled =
                    address.error == null &&
                        name.error == null &&
                        contactAddress.value.isNotEmpty() &&
                        contactName.value.isNotEmpty() &&
                        (nameChanged || addressChanged || blockchainChanged),
                onClick = ::onUpdateButtonClick,
                isLoading = isUpdatingContact
            )
        }

    private val deleteButtonState =
        isDeletingContact.map { isDeletingContact ->
            ButtonState(
                text = stringRes(R.string.update_contact_secondary_btn),
                onClick = ::onDeleteButtonClick,
                isLoading = isDeletingContact
            )
        }

    val state =
        combine(
            contactAddressState,
            contactNameState,
            updateButtonState,
            deleteButtonState,
            isLoadingContact,
            blockChainPickerState
        ) { address, name, saveButton, deleteButton, isLoadingContact, blockchainPicker ->
            ABContactState(
                title = stringRes(R.string.update_contact_title),
                isLoading = isLoadingContact,
                walletAddress = address,
                contactName = name,
                negativeButton = deleteButton,
                positiveButton = saveButton,
                onBack = ::onBack,
                chain = blockchainPicker,
                info = null
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            val contact = getContactByAddress(address = args.address, chain = args.chain)
            contactAddress.update { contact?.address.orEmpty() }
            contactName.update { contact?.name.orEmpty() }
            originalContact.update { contact }
            selectedBlockchain.update { contact?.blockchain }
            isLoadingContact.update { false }
        }
    }

    private fun onBack() = navigationRouter.back()

    private fun onUpdateButtonClick() =
        viewModelScope.launch {
            val selectedBlockchain = selectedBlockchain.value
            if (isDeletingContact.value || isUpdatingContact.value || selectedBlockchain == null) return@launch
            originalContact.value?.let {
                isUpdatingContact.update { true }
                updateContact(
                    contact = it,
                    name = contactName.value,
                    address = contactAddress.value,
                    chain = selectedBlockchain.chainTicker
                )
                navigationRouter.back()
                isUpdatingContact.update { false }
            }
        }

    private fun onDeleteButtonClick() =
        viewModelScope.launch {
            if (isDeletingContact.value || isUpdatingContact.value) return@launch
            originalContact.value?.let {
                isDeletingContact.update { true }
                deleteContact(it)
                navigationRouter.back()
                isDeletingContact.update { false }
            }
        }

    private fun onBlockchainClick() =
        viewModelScope.launch {
            val result = navigateToSelectSwapBlockchain()
            if (result != null) {
                selectedBlockchain.update { result }
            }
        }
}
