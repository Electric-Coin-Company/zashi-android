@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.addressbook

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.screen.addressbook.view.AddressBookView
import co.electriccoin.zcash.ui.screen.addressbook.viewmodel.AddressBookViewModel
import co.electriccoin.zcash.ui.screen.addressbook.viewmodel.SelectRecipientViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WrapAddressBook(args: AddressBookArgs) {
    when (args) {
        AddressBookArgs.DEFAULT -> WrapAddressBook()
        AddressBookArgs.PICK_CONTACT -> WrapSelectRecipient()
    }
}

@Composable
private fun WrapAddressBook() {
    val viewModel = koinViewModel<AddressBookViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler {
        state.onBack()
    }

    AddressBookView(
        state = state,
    )
}

@Composable
private fun WrapSelectRecipient() {
    val viewModel = koinViewModel<SelectRecipientViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler {
        state.onBack()
    }

    AddressBookView(
        state = state,
    )
}

enum class AddressBookArgs {
    DEFAULT,
    PICK_CONTACT;

    companion object {
        private const val PATH = "address_book"
        const val MODE = "mode"

        const val ROUTE = "$PATH/{$MODE}"

        operator fun invoke(mode: AddressBookArgs) = "$PATH/$mode"
    }
}
