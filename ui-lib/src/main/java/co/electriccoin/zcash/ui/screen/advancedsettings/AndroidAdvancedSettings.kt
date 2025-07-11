@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.advancedsettings

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AdvancedSettingsScreen(
    goDeleteWallet: () -> Unit,
    goExportPrivateData: () -> Unit,
) {
    val viewModel = koinViewModel<AdvancedSettingsVM>()
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
    )
}
