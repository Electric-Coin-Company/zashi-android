package co.electriccoin.zcash.ui.screen.addressbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.usecase.GetWalletAccountsUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveAddressBookContactsUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactPickedUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.listitem.ContactListItemState
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.addressbook.model.AddressBookItem
import co.electriccoin.zcash.ui.screen.addressbook.model.AddressBookState
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.scan.Scan
import co.electriccoin.zcash.ui.screen.scan.ScanFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SelectRecipientViewModel(
    observeAddressBookContacts: ObserveAddressBookContactsUseCase,
    getWalletAccountsUseCase: GetWalletAccountsUseCase,
    private val observeContactPicked: ObserveContactPickedUseCase,
    private val navigationRouter: NavigationRouter
) : ViewModel() {
    val state =
        combine(observeAddressBookContacts(), getWalletAccountsUseCase.observe()) { contacts, accounts ->
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

    @Suppress("SpreadOperator")
    private fun createStateWithAccounts(
        contacts: List<AddressBookContact>?,
        accounts: List<WalletAccount>
    ): AddressBookState {
        val accountItems =
            listOf(
                AddressBookItem.Title(stringRes(R.string.address_book_multiple_wallets_title)),
                *accounts
                    .map { account ->
                        AddressBookItem.Contact(
                            ContactListItemState(
                                icon =
                                    imageRes(
                                        when (account) {
                                            is KeystoneAccount ->
                                                co.electriccoin.zcash.ui.design.R.drawable.ic_item_keystone
                                            is ZashiAccount ->
                                                co.electriccoin.zcash.ui.design.R.drawable.ic_item_zashi
                                        }
                                    ),
                                isShielded = false,
                                name = account.name,
                                address = stringRes("${account.unified.address.address.take(ADDRESS_MAX_LENGTH)}..."),
                                onClick = { onWalletAccountClick(account) }
                            )
                        )
                    }.toTypedArray()
            )

        val addressBookItems =
            if (contacts.isNullOrEmpty()) {
                listOf(AddressBookItem.Empty)
            } else {
                listOf(
                    AddressBookItem.Title(stringRes(R.string.address_book_multiple_wallets_contacts_title)),
                    *contacts
                        .map { contact ->
                            AddressBookItem.Contact(
                                ContactListItemState(
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
            title = stringRes(R.string.address_book_select_recipient_title)
        )
    }

    private fun createStateWithoutAccounts(contacts: List<AddressBookContact>?): AddressBookState =
        AddressBookState(
            isLoading = contacts == null,
            items =
                contacts
                    ?.map { contact ->
                        AddressBookItem.Contact(
                            ContactListItemState(
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
            title = stringRes(R.string.address_book_select_recipient_title)
        )

    private fun onWalletAccountClick(account: WalletAccount) =
        viewModelScope.launch {
            observeContactPicked.onWalletAccountPicked(account)
            navigationRouter.back()
        }

    private fun getContactInitials(contact: AddressBookContact): ImageResource =
        imageRes(
            contact.name
                .split(" ")
                .mapNotNull { part ->
                    part.takeIf { it.isNotEmpty() }?.first()?.toString()
                }.take(2)
                .joinToString(separator = "")
        )

    private fun onBack() = navigationRouter.back()

    private fun onContactClick(contact: AddressBookContact) =
        viewModelScope.launch {
            observeContactPicked.onContactPicked(contact)
            navigationRouter.back()
        }

    private fun onAddContactManuallyClick() = navigationRouter.forward(AddContactArgs(null))

    private fun onScanContactClick() = navigationRouter.forward(Scan(ScanFlow.ADDRESS_BOOK))
}
