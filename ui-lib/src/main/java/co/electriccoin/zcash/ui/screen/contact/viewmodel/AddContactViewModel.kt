package co.electriccoin.zcash.ui.screen.contact.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.SaveContactUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateContactAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateContactNameUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.contact.model.ContactState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddContactViewModel(
    address: String? = null,
    private val validateContactAddress: ValidateContactAddressUseCase,
    private val validateContactName: ValidateContactNameUseCase,
    private val saveContact: SaveContactUseCase
) : ViewModel() {
    private val contactAddress = MutableStateFlow(address.orEmpty())
    private val contactName = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    private val contactAddressError =
        contactAddress.mapLatest { address ->
            if (address.isEmpty()) {
                null
            } else {
                when (validateContactAddress(address)) {
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private val contactNameError =
        contactName.mapLatest { name ->
            if (name.isEmpty()) {
                null
            } else {
                when (validateContactName(name)) {
                    ValidateContactNameUseCase.Result.TooLong ->
                        stringRes(R.string.contact_name_error_too_long)

                    ValidateContactNameUseCase.Result.NotUnique ->
                        stringRes(R.string.contact_name_error_not_unique)

                    ValidateContactNameUseCase.Result.Valid ->
                        null
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

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

    private val isSavingContact = MutableStateFlow(false)

    private val saveButtonState =
        combine(contactAddressState, contactNameState, isSavingContact) { address, name, isSavingContact ->
            ButtonState(
                text = stringRes(R.string.add_new_contact_primary_btn),
                isEnabled =
                    address.error == null &&
                        name.error == null &&
                        contactAddress.value.isNotEmpty() &&
                        contactName.value.isNotEmpty(),
                onClick = ::onSaveButtonClick,
                isLoading = isSavingContact
            )
        }

    val state =
        combine(contactAddressState, contactNameState, saveButtonState) { address, name, saveButton ->
            ContactState(
                title = stringRes(R.string.add_new_contact_title),
                isLoading = false,
                walletAddress = address,
                contactName = name,
                negativeButton = null,
                positiveButton = saveButton,
                onBack = ::onBack,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    val navigationCommand = MutableSharedFlow<String>()

    val backNavigationCommand = MutableSharedFlow<Unit>()

    private fun onBack() =
        viewModelScope.launch {
            backNavigationCommand.emit(Unit)
        }

    private fun onSaveButtonClick() =
        viewModelScope.launch {
            if (isSavingContact.value) return@launch

            isSavingContact.update { true }
            saveContact(name = contactName.value, address = contactAddress.value)
            backNavigationCommand.emit(Unit)
            isSavingContact.update { false }
        }
}
