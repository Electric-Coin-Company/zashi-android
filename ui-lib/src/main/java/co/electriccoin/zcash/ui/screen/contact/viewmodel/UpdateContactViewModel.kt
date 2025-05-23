package co.electriccoin.zcash.ui.screen.contact.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.usecase.DeleteContactUseCase
import co.electriccoin.zcash.ui.common.usecase.GetContactByAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.UpdateContactUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateContactAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateContactNameUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.contact.model.ContactState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpdateContactViewModel(
    private val originalContactAddress: String,
    private val validateContactAddress: ValidateContactAddressUseCase,
    private val validateContactName: ValidateContactNameUseCase,
    private val updateContact: UpdateContactUseCase,
    private val deleteContact: DeleteContactUseCase,
    private val getContactByAddress: GetContactByAddressUseCase,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    private var contact = MutableStateFlow<AddressBookContact?>(null)
    private val contactAddress = MutableStateFlow("")
    private val contactName = MutableStateFlow("")

    private val isUpdatingContact = MutableStateFlow(false)
    private val isDeletingContact = MutableStateFlow(false)
    private val isLoadingContact = MutableStateFlow(true)

    private val contactAddressError =
        combine(contact, contactAddress) { contact, address ->
            if (address.isEmpty() || contact == null) {
                null
            } else {
                when (validateContactAddress(address = address, exclude = contact)) {
                    ValidateContactAddressUseCase.Result.Invalid ->
                        stringRes(R.string.contact_address_error_invalid)

                    ValidateContactAddressUseCase.Result.NotUnique ->
                        stringRes(R.string.contact_address_error_not_unique)

                    ValidateContactAddressUseCase.Result.Valid -> null
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
        combine(contactName, contact) { name, contact ->
            if (name.isEmpty() || contact == null) {
                null
            } else {
                when (validateContactName(name = name, exclude = contact)) {
                    ValidateContactNameUseCase.Result.TooLong ->
                        stringRes(R.string.contact_name_error_too_long)

                    ValidateContactNameUseCase.Result.NotUnique ->
                        stringRes(R.string.contact_name_error_not_unique)

                    ValidateContactNameUseCase.Result.Valid -> null
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
        combine(contactAddressState, contactNameState, isUpdatingContact, contact) {
            address,
            name,
            isUpdatingContact,
            contact
            ->
            ButtonState(
                text = stringRes(R.string.update_contact_primary_btn),
                isEnabled =
                    address.error == null &&
                        name.error == null &&
                        contactAddress.value.isNotEmpty() &&
                        contactName.value.isNotEmpty() &&
                        (contactName.value.trim() != contact?.name || contactAddress.value.trim() != contact.address),
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
            isLoadingContact
        ) { address, name, saveButton, deleteButton, isLoadingContact ->
            ContactState(
                title = stringRes(R.string.update_contact_title),
                isLoading = isLoadingContact,
                walletAddress = address,
                contactName = name,
                negativeButton = deleteButton,
                positiveButton = saveButton,
                onBack = ::onBack,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            getContactByAddress(originalContactAddress).let { contact ->
                contactAddress.update { contact?.address.orEmpty() }
                contactName.update { contact?.name.orEmpty() }
                this@UpdateContactViewModel.contact.update { contact }
            }
            isLoadingContact.update { false }
        }
    }

    private fun onBack() = navigationRouter.back()

    private fun onUpdateButtonClick() =
        viewModelScope.launch {
            if (isDeletingContact.value || isUpdatingContact.value) return@launch
            contact.value?.let {
                isUpdatingContact.update { true }
                updateContact(contact = it, name = contactName.value, address = contactAddress.value)
                navigationRouter.back()
                isUpdatingContact.update { false }
            }
        }

    private fun onDeleteButtonClick() =
        viewModelScope.launch {
            if (isDeletingContact.value || isUpdatingContact.value) return@launch
            contact.value?.let {
                isDeletingContact.update { true }
                deleteContact(it)
                navigationRouter.back()
                isDeletingContact.update { false }
            }
        }
}
