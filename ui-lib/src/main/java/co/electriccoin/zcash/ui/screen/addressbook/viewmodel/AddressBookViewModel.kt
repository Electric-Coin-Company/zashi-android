package co.electriccoin.zcash.ui.screen.addressbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.usecase.ObserveAddressBookContactsUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.listitem.ZashiContactListItemState
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.addressbook.model.AddressBookItem
import co.electriccoin.zcash.ui.screen.addressbook.model.AddressBookState
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.contact.UpdateContactArgs
import co.electriccoin.zcash.ui.screen.scan.ScanNavigationArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AddressBookViewModel(
    observeAddressBookContacts: ObserveAddressBookContactsUseCase,
    private val navigationRouter: NavigationRouter,
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

    private fun createState(contacts: List<AddressBookContact>?) =
        AddressBookState(
            isLoading = contacts == null,
            items =
                contacts?.map { contact ->
                    AddressBookItem.Contact(
                        ZashiContactListItemState(
                            icon = getContactInitials(contact),
                            isShielded = false,
                            name = stringRes(contact.name),
                            address = stringRes("${contact.address.take(ADDRESS_MAX_LENGTH)}..."),
                            onClick = { onContactClick(contact) }
                        )
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
                ),
            title = stringRes(R.string.address_book_title)
        )

    private fun getContactInitials(contact: AddressBookContact) =
        imageRes(
            contact.name
                .split(" ")
                .mapNotNull { part ->
                    part.takeIf { it.isNotEmpty() }?.first()?.toString()
                }
                .take(2)
                .joinToString(separator = "")
        )

    private fun onBack() = navigationRouter.back()

    private fun onContactClick(contact: AddressBookContact) {
        navigationRouter.forward(UpdateContactArgs(contact.address))
    }

    private fun onAddContactManuallyClick() = navigationRouter.forward(AddContactArgs(null))

    private fun onScanContactClick() = navigationRouter.forward(ScanNavigationArgs(ScanNavigationArgs.ADDRESS_BOOK))
}

internal const val ADDRESS_MAX_LENGTH = 20
