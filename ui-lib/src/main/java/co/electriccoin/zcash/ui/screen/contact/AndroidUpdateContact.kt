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
import co.electriccoin.zcash.ui.screen.contact.view.ContactView
import co.electriccoin.zcash.ui.screen.contact.viewmodel.UpdateContactViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun WrapUpdateContact(contactAddress: String) {
    val navController = LocalNavController.current
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val viewModel = koinViewModel<UpdateContactViewModel> { parametersOf(contactAddress) }
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

object UpdateContactArgs {
    private const val PATH = "update_contact"
    const val CONTACT_ADDRESS = "contactAddress"
    const val ROUTE = "$PATH/{$CONTACT_ADDRESS}"

    operator fun invoke(contactAddress: String) = "$PATH/$contactAddress"
}
