package co.electriccoin.zcash.ui.screen.addressbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact
import co.electriccoin.zcash.ui.common.usecase.GetABContactsUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.listitem.ContactListItemState
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.contact.AddABContactArgs
import co.electriccoin.zcash.ui.screen.contact.UpdateABContactArgs
import co.electriccoin.zcash.ui.screen.scan.ScanArgs
import co.electriccoin.zcash.ui.screen.scan.ScanFlow
import co.electriccoin.zcash.ui.screen.swap.ab.UpdateABSwapContactArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AddressBookViewModel(
    getAddressBookContacts: GetABContactsUseCase,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    val state =
        getAddressBookContacts.observe(zcashContactsOnly = false)
            .map { contacts -> createState(contacts = contacts) }
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = createState(contacts = null)
            )

    private fun createState(contacts: List<EnhancedABContact>?) =
        AddressBookState(
            isLoading = contacts == null,
            items =
                contacts
                    ?.map { contact ->
                        AddressBookItem.Contact(
                            ContactListItemState(
                                bigIcon = getContactInitials(contact),
                                smallIcon = null,
                                isShielded = false,
                                name = stringRes(contact.name),
                                address = stringRes("${contact.address.take(ADDRESS_MAX_LENGTH)}..."),
                                onClick = { onContactClick(contact) },
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
            title = stringRes(R.string.address_book_title),
            info = null
        )

    private fun getContactInitials(contact: EnhancedABContact) =
        imageRes(
            contact.name
                .split(" ")
                .mapNotNull { part ->
                    part.takeIf { it.isNotEmpty() }?.first()?.toString()
                }.take(2)
                .joinToString(separator = "")
        )

    private fun onBack() = navigationRouter.back()

    private fun onContactClick(contact: EnhancedABContact) {
        if (contact.blockchain == null) {
            navigationRouter.forward(UpdateABContactArgs(address = contact.address))
        } else {
            navigationRouter.forward(
                UpdateABSwapContactArgs(
                    address = contact.address,
                    chain = contact.blockchain.chainTicker
                )
            )
        }
    }

    private fun onAddContactManuallyClick() = navigationRouter.forward(AddABContactArgs(null))

    private fun onScanContactClick() = navigationRouter.forward(ScanArgs(ScanFlow.ADDRESS_BOOK))
}

internal const val ADDRESS_MAX_LENGTH = 20
