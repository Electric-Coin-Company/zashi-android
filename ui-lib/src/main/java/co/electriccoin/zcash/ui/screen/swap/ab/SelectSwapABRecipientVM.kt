package co.electriccoin.zcash.ui.screen.swap.ab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.ContactWithSwapAsset
import co.electriccoin.zcash.ui.common.usecase.GetABSwapContactsUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToScanSwapAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToSelectABSwapRecipientUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.listitem.ContactListItemState
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByAddress
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookItem
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookState
import co.electriccoin.zcash.ui.screen.swap.scan.ScanSwapAddressArgs
import co.electriccoin.zcash.ui.screen.swap.info.SwapInfoArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SelectSwapABRecipientVM(
    getAddressBookSwapContacts: GetABSwapContactsUseCase,
    private val args: SelectABSwapRecipientArgs,
    private val navigateToSelectSwapRecipient: NavigateToSelectABSwapRecipientUseCase,
    private val navigationRouter: NavigationRouter,
    private val navigateToScanAddress: NavigateToScanSwapAddressUseCase
) : ViewModel() {
    val state =
        getAddressBookSwapContacts.observe()
            .map { contacts ->
                createState(contacts)
            }
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = createState(contacts = null)
            )

    private fun createState(contacts: List<ContactWithSwapAsset>?): AddressBookState =
        AddressBookState(
            isLoading = contacts == null,
            items =
                contacts
                    ?.map { contact ->
                        AddressBookItem.Contact(
                            ContactListItemState(
                                bigIcon = getContactInitials(contact),
                                smallIcon = contact.asset.chainIcon,
                                isShielded = false,
                                name = stringRes(contact.contact.name),
                                address = stringResByAddress(contact.contact.address, abbreviated = true),
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
            title = stringRes(R.string.address_book_select_recipient_title),
            info = IconButtonState(R.drawable.ic_help, onClick = ::onInfoClick)
        )

    private fun onInfoClick() = navigationRouter.forward(SwapInfoArgs)

    private fun getContactInitials(contact: ContactWithSwapAsset): ImageResource =
        imageRes(
            contact.contact.name
                .split(" ")
                .mapNotNull { part ->
                    part.takeIf { it.isNotEmpty() }?.first()?.toString()
                }
                .take(2)
                .joinToString(separator = "")
        )

    private fun onBack() = viewModelScope.launch { navigateToSelectSwapRecipient.onSelectionCancelled(args) }

    private fun onContactClick(contact: ContactWithSwapAsset) =
        viewModelScope.launch { navigateToSelectSwapRecipient.onSelected(contact, args) }

    private fun onAddContactManuallyClick() = navigationRouter.forward(
        AddSwapABContactArgs(
            address = null,
            chain = null
        )
    )

    private fun onScanContactClick() =
        viewModelScope.launch { navigateToScanAddress(ScanSwapAddressArgs.Mode.SWAP_SCAN_CONTACT_ADDRESS) }
}
