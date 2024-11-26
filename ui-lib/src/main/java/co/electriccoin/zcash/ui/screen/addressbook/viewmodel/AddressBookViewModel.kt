package co.electriccoin.zcash.ui.screen.addressbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.usecase.ObserveAddressBookContactsUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactPickedUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookArgs
import co.electriccoin.zcash.ui.screen.addressbook.model.AddressBookItem
import co.electriccoin.zcash.ui.screen.addressbook.model.AddressBookState
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.contact.UpdateContactArgs
import co.electriccoin.zcash.ui.screen.scan.ScanNavigationArgs
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
    private val args: AddressBookArgs,
    private val observeContactPicked: ObserveContactPickedUseCase
) : ViewModel() {
    val state =
        observeAddressBookContacts()
            .map { contacts -> createState(contacts = contacts) }
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = createState(contacts = null)
            )

    val navigationCommand = MutableSharedFlow<String>()

    val backNavigationCommand = MutableSharedFlow<Unit>()

    private fun createState(contacts: List<AddressBookContact>?) =
        AddressBookState(
            isLoading = contacts == null,
            contacts =
                contacts?.map { contact ->
                    AddressBookItem.Contact(
                        initials = getContactInitials(contact),
                        isShielded = false,
                        name = stringRes(contact.name),
                        address = stringRes("${contact.address.take(ADDRESS_MAX_LENGTH)}..."),
                        onClick = { onContactClick(contact) }
                    )
                }.orEmpty(),
            onBack = ::onBack,
            manualButton =
                ButtonState(
                    onClick = ::onAddContactManuallyClick,
                    text = stringRes(R.string.address_book_manual_btn)
                ),
            scanButton =
                ButtonState(
                    onClick = ::onScanContactClick,
                    text = stringRes(R.string.address_book_scan_btn)
                )
        )

    private fun getContactInitials(contact: AddressBookContact) =
        stringRes(
            contact.name
                .split(" ")
                .mapNotNull { part ->
                    part.takeIf { it.isNotEmpty() }?.first()?.toString()
                }
                .take(2)
                .joinToString(separator = "")
        )

    private fun onBack() =
        viewModelScope.launch {
            backNavigationCommand.emit(Unit)
        }

    private fun onContactClick(contact: AddressBookContact) =
        viewModelScope.launch {
            when (args) {
                AddressBookArgs.DEFAULT -> {
                    navigationCommand.emit(UpdateContactArgs(contact.address))
                }

                AddressBookArgs.PICK_CONTACT -> {
                    observeContactPicked.onContactPicked(contact)
                    backNavigationCommand.emit(Unit)
                }
            }
        }

    private fun onAddContactManuallyClick() =
        viewModelScope.launch {
            navigationCommand.emit(AddContactArgs(null))
        }

    private fun onScanContactClick() =
        viewModelScope.launch {
            navigationCommand.emit(ScanNavigationArgs(ScanNavigationArgs.ADDRESS_BOOK))
        }
}

private const val ADDRESS_MAX_LENGTH = 20
