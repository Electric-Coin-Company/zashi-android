package co.electriccoin.zcash.ui.screen.addressbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.usecase.ObserveAddressBookContactsUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactPickedUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveWalletAccountsUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.listitem.ZashiContactListItemState
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.addressbook.model.AddressBookItem
import co.electriccoin.zcash.ui.screen.addressbook.model.AddressBookState
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.scan.ScanNavigationArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SelectRecipientViewModel(
    observeAddressBookContacts: ObserveAddressBookContactsUseCase,
    observeWalletAccountsUseCase: ObserveWalletAccountsUseCase,
    private val observeContactPicked: ObserveContactPickedUseCase,
) : ViewModel() {
    val state =
        combine(observeAddressBookContacts(), observeWalletAccountsUseCase()) { contacts, accounts ->
            if (accounts != null && accounts.size > 1) {
                createStateWithAccounts(contacts, accounts)
            } else {
                createStateWithoutAccounts(contacts)
            }
        }.flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = createStateWithoutAccounts(contacts = null)
            )

    val navigationCommand = MutableSharedFlow<String>()

    val backNavigationCommand = MutableSharedFlow<Unit>()

    private fun createStateWithAccounts(
        contacts: List<AddressBookContact>?,
        accounts: List<WalletAccount>
    ): AddressBookState {
        val accountItems = listOf(
            AddressBookItem.Title(stringRes("Your Wallets")), // TODO keystone string
            *accounts.map { account ->
                AddressBookItem.Contact(
                    ZashiContactListItemState(
                        icon = imageRes(
                            when (account) {
                                is KeystoneAccount -> co.electriccoin.zcash.ui.design.R.drawable.ic_item_keystone
                                is ZashiAccount -> co.electriccoin.zcash.ui.design.R.drawable.ic_item_zashi
                            }
                        ),
                        isShielded = false,
                        name = account.name,
                        address = stringRes("${account.unifiedAddress.address.take(ADDRESS_MAX_LENGTH)}..."),
                        onClick = { onWalletAccountClick(account) }
                    )
                )
            }.toTypedArray()
        )

        val addressBookItems = if (contacts.isNullOrEmpty()) {
            listOf(AddressBookItem.Empty)
        } else {
            listOf(
                AddressBookItem.Title(stringRes("Address Book Contacts")), // TODO keystone string
                *contacts.map { contact ->
                    AddressBookItem.Contact(
                        ZashiContactListItemState(
                            icon = getContactInitials(contact),
                            isShielded = false,
                            name = stringRes(contact.name),
                            address = stringRes("${contact.address.take(ADDRESS_MAX_LENGTH)}..."),
                            onClick = { onContactClick(contact) }
                        )
                    )
                }.toTypedArray()
            )
        }

        return AddressBookState(
            isLoading = contacts == null,
            items = accountItems + addressBookItems,
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
            title = stringRes("Select recipient") // TODO keystone string
        )
    }

    private fun createStateWithoutAccounts(contacts: List<AddressBookContact>?): AddressBookState {
        return AddressBookState(
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
            title = stringRes("Select recipient") // TODO keystone string
        )
    }

    private fun onWalletAccountClick(account: WalletAccount) =
        viewModelScope.launch {
            observeContactPicked.onWalletAccountPicked(account)
            backNavigationCommand.emit(Unit)
        }

    private fun getContactInitials(contact: AddressBookContact): ImageResource {
        return imageRes(
            contact.name
                .split(" ")
                .mapNotNull { part ->
                    part.takeIf { it.isNotEmpty() }?.first()?.toString()
                }
                .take(2)
                .joinToString(separator = "")
        )
    }

    private fun onBack() =
        viewModelScope.launch {
            backNavigationCommand.emit(Unit)
        }

    private fun onContactClick(contact: AddressBookContact) =
        viewModelScope.launch {
            observeContactPicked.onContactPicked(contact)
            backNavigationCommand.emit(Unit)
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