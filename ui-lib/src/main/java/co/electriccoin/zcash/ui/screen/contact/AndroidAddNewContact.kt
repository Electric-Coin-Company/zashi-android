@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.contact

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.addressbook.view.AddressBookView
import co.electriccoin.zcash.ui.screen.addressbook.viewmodel.AddressBookViewModel
import co.electriccoin.zcash.ui.screen.contact.view.ContactView
import co.electriccoin.zcash.ui.screen.contact.viewmodel.AddNewContactViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WrapAddNewContact() {
    val navController = LocalNavController.current
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val viewModel = koinViewModel<AddNewContactViewModel>()
    val walletState by walletViewModel.walletStateInformation.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationCommand.collect {
            navController.navigate(it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.backNavigationCommand.collect {
            navController.popBackStack()
        }
    }

    BackHandler {
        state?.onBack?.invoke()
    }

    state?.let {
        ContactView(
            state = it,
            topAppBarSubTitleState = walletState,
        )
    }
}
