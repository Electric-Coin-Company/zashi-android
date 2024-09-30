@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.addressbook

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
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WrapAddressBook() {
    val navController = LocalNavController.current
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val viewModel = koinViewModel<AddressBookViewModel>()
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
        state.onBack()
    }

    AddressBookView(
        state = state,
        topAppBarSubTitleState = walletState,
    )
}
