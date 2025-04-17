@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.contact

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.screen.contact.view.ContactView
import co.electriccoin.zcash.ui.screen.contact.viewmodel.AddContactViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun WrapAddContact(address: String?) {
    val viewModel = koinViewModel<AddContactViewModel> { parametersOf(address) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler {
        state?.onBack?.invoke()
    }

    state?.let {
        ContactView(
            state = it,
        )
    }
}

object AddContactArgs {
    private const val PATH = "add_contact"
    const val ADDRESS = "address"
    const val ROUTE = "$PATH/{$ADDRESS}"

    operator fun invoke(address: String?) = "$PATH/$address"
}
