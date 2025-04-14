@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.advancedsettings

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WrapAdvancedSettings(
    goDeleteWallet: () -> Unit,
    goExportPrivateData: () -> Unit,
) {
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val viewModel = koinViewModel<AdvancedSettingsViewModel>()
    val walletState = walletViewModel.walletStateInformation.collectAsStateWithLifecycle().value
    val originalState = viewModel.state.collectAsStateWithLifecycle().value
    val state =
        originalState.copy(
            deleteButton = originalState.deleteButton.copy(onClick = goDeleteWallet),
            items =
                originalState.items
                    .mapIndexed { index, item ->
                        when (index) {
                            1 -> item.copy(onClick = goExportPrivateData)
                            else -> item
                        }
                    }.toImmutableList()
        )

    BackHandler {
        viewModel.onBack()
    }

    AdvancedSettings(
        state = state,
        topAppBarSubTitleState = walletState,
    )
}
