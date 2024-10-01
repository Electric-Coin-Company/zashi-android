package co.electriccoin.zcash.ui.screen.addressbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationTargets.ADD_NEW_CONTACT
import co.electriccoin.zcash.ui.NavigationTargets.UPDATE_CONTACT
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.usecase.ObserveAddressBookContactsUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.addressbook.model.AddressBookContactState
import co.electriccoin.zcash.ui.screen.addressbook.model.AddressBookState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddressBookViewModel(
    observeAddressBookContacts: ObserveAddressBookContactsUseCase,
    getVersionInfo: GetVersionInfoProvider,
) : ViewModel() {

    private val versionInfo = getVersionInfo()

    val state = observeAddressBookContacts()
        .map { contacts -> createState(contacts = contacts, isLoading = false) }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = createState(contacts = emptyList(), isLoading = true)
        )

    val navigationCommand = MutableSharedFlow<String>()

    val backNavigationCommand = MutableSharedFlow<Unit>()

    private fun createState(contacts: List<AddressBookContact>, isLoading: Boolean) = AddressBookState(
        version = stringRes(R.string.address_book_version, versionInfo.versionName),
        isLoading = isLoading,
        contacts = contacts.map { contact ->
            AddressBookContactState(
                initials = getContactInitials(contact),
                isShielded = false,
                name = stringRes(contact.name),
                address = stringRes(contact.address),
                onClick = { onUpdateContactClick(contact) }
            )

        },
        onBack = ::onBack,
        addButton = ButtonState(
            onClick = ::onAddContactClick,
            text = stringRes(R.string.address_book_add)
        )
    )

    private fun getContactInitials(contact: AddressBookContact) = stringRes(
        contact.name
            .split(" ")
            .mapNotNull { part ->
                part.takeIf { it.isNotEmpty() }?.first()?.toString()
            }
            .take(2)
            .joinToString(separator = "")
    )

    private fun onBack() = viewModelScope.launch {
        backNavigationCommand.emit(Unit)
    }

    private fun onUpdateContactClick(contact: AddressBookContact) = viewModelScope.launch {
        navigationCommand.emit("$UPDATE_CONTACT/${contact.id}")
    }

    private fun onAddContactClick() = viewModelScope.launch {
        navigationCommand.emit(ADD_NEW_CONTACT)
    }
}
