package co.electriccoin.zcash.ui.screen.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact
import co.electriccoin.zcash.ui.common.usecase.ContactAddressValidationResult
import co.electriccoin.zcash.ui.common.usecase.DeleteABContactUseCase
import co.electriccoin.zcash.ui.common.usecase.GetABContactByIdUseCase
import co.electriccoin.zcash.ui.common.usecase.UpdateABContactUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateABContactAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateContactNameResult
import co.electriccoin.zcash.ui.common.usecase.ValidateABContactNameUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpdateABContactVM(
    private val args: UpdateABContactArgs,
    private val validateContactAddress: ValidateABContactAddressUseCase,
    private val validateContactName: ValidateABContactNameUseCase,
    private val updateContact: UpdateABContactUseCase,
    private val deleteContact: DeleteABContactUseCase,
    private val getContactByAddress: GetABContactByIdUseCase,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    private val contact = MutableStateFlow<EnhancedABContact?>(null)
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
        combine(contactName, contact) { name, contact ->
            if (name.isEmpty() || contact == null) {
                null
            } else {
                when (validateContactName(name = name, exclude = contact)) {
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
            ABContactState(
                title = stringRes(R.string.update_contact_title),
                isLoading = isLoadingContact,
                walletAddress = address,
                contactName = name,
                negativeButton = deleteButton,
                positiveButton = saveButton,
                onBack = ::onBack,
                chain = null,
                info = null
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            getContactByAddress(
                address = args.address,
                chain = null
            ).let { contact ->
                contactAddress.update { contact?.address.orEmpty() }
                contactName.update { contact?.name.orEmpty() }
                this@UpdateABContactVM.contact.update { contact }
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
                updateContact(contact = it, name = contactName.value, address = contactAddress.value, chain = null)
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
