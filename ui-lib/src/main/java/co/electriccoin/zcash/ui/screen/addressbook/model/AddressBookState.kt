package co.electriccoin.zcash.ui.screen.addressbook.model

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.StringResource

data class AddressBookState(
    val contacts: List<AddressBookContactState>,
    val isLoading: Boolean,
    val onBack: () -> Unit,
    val scanButton: ButtonState,
    val manualButton: ButtonState
)

data class AddressBookContactState(
    val initials: StringResource,
    val isShielded: Boolean,
    val name: StringResource,
    val address: StringResource,
    val onClick: () -> Unit,
)
