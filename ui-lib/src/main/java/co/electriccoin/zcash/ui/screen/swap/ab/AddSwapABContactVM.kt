package co.electriccoin.zcash.ui.screen.swap.ab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.ContactAddressValidationResult
import co.electriccoin.zcash.ui.common.usecase.GetSwapAssetBlockchainUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToSelectSwapBlockchainUseCase
import co.electriccoin.zcash.ui.common.usecase.SaveABContactUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateContactNameResult
import co.electriccoin.zcash.ui.common.usecase.ValidateGenericABContactNameUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateSwapABContactAddressUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.PickerState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.contact.ABContactState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddSwapABContactVM(
    args: AddSwapABContactArgs,
    getSwapAssetBlockchain: GetSwapAssetBlockchainUseCase,
    private val validateABSwapContactAddress: ValidateSwapABContactAddressUseCase,
    private val validateGenericABContactName: ValidateGenericABContactNameUseCase,
    private val saveABContact: SaveABContactUseCase,
    private val navigationRouter: NavigationRouter,
    private val navigateToSelectSwapBlockchain: NavigateToSelectSwapBlockchainUseCase
) : ViewModel() {
    private val contactAddress = MutableStateFlow(args.address.orEmpty())
    private val contactName = MutableStateFlow("")
    private val selectedBlockchain = MutableStateFlow(getSwapAssetBlockchain(args.chain))
    private val isSavingContact = MutableStateFlow(false)

    private val blockChainPickerState =
        selectedBlockchain
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
        combine(contactAddress, selectedBlockchain) { address, blockchain ->
            if (address.isEmpty()) {
                null
            } else {
                when (validateABSwapContactAddress(address, blockchain)) {
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
                onValueChange = ::onAddressChange
            )
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val contactNameError =
        contactName
            .mapLatest { name ->
                if (name.isEmpty()) {
                    null
                } else {
                    when (validateGenericABContactName(name)) {
                        ValidateContactNameResult.TooLong ->
                            stringRes(R.string.contact_name_error_too_long)

                        ValidateContactNameResult.NotUnique ->
                            stringRes(R.string.contact_name_error_not_unique)

                        ValidateContactNameResult.Valid ->
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

    private val saveButtonState =
        combine(
            contactAddressState,
            contactNameState,
            isSavingContact,
            selectedBlockchain
        ) { address, name, isSavingContact, blockchain ->
            ButtonState(
                text = stringRes(R.string.add_new_contact_primary_btn),
                isEnabled =
                    blockchain != null &&
                        address.error == null &&
                        name.error == null &&
                        contactAddress.value.isNotEmpty() &&
                        contactName.value.isNotEmpty(),
                onClick = ::onSaveButtonClick,
                isLoading = isSavingContact
            )
        }

    val state =
        combine(
            contactAddressState,
            contactNameState,
            saveButtonState,
            blockChainPickerState,
        ) { address, name, saveButton, picker ->
            ABContactState(
                title = stringRes(R.string.add_new_contact_title),
                isLoading = false,
                walletAddress = address,
                contactName = name,
                negativeButton = null,
                positiveButton = saveButton,
                onBack = ::onBack,
                chain = picker,
                info = null
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private fun onBlockchainClick() =
        viewModelScope.launch {
            val result = navigateToSelectSwapBlockchain()
            if (result != null) {
                selectedBlockchain.update { result }
            }
        }

    private fun onBack() = navigationRouter.back()

    private fun onAddressChange(newValue: String) = contactAddress.update { newValue.trim() }

    private fun onSaveButtonClick() =
        viewModelScope.launch {
            if (isSavingContact.value) return@launch
            isSavingContact.update { true }
            saveABContact(
                name = contactName.value,
                address = contactAddress.value,
                chain = selectedBlockchain.value?.chainTicker
            )
            isSavingContact.update { false }
        }
}
