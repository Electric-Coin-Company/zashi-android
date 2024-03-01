@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.chooseserver

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.internal.Twig
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.android.sdk.type.ServerValidation
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.viewmodel.SecretState
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.chooseserver.view.ChooseServer
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.WrapChooseServer(goBack: () -> Unit) {
    val secretState = walletViewModel.secretState.collectAsStateWithLifecycle().value

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    WrapChooseServer(
        activity = this,
        goBack = goBack,
        secretState = secretState,
        synchronizer = synchronizer,
        onSynchronizerClose = {
            walletViewModel.closeSynchronizer()
        },
        onWalletPersist = {
            walletViewModel.persistExistingWallet(it)
        }
    )
}

@Composable
@Suppress("LongParameterList")
private fun WrapChooseServer(
    activity: MainActivity,
    goBack: () -> Unit,
    onSynchronizerClose: () -> Unit,
    onWalletPersist: (PersistableWallet) -> Unit,
    secretState: SecretState,
    synchronizer: Synchronizer?,
) {
    if (synchronizer == null || secretState !is SecretState.Ready) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        val wallet = secretState.persistableWallet

        val scope = rememberCoroutineScope()

        val snackbarHostState = remember { SnackbarHostState() }

        var validationResult: ServerValidation by remember { mutableStateOf(ServerValidation.Valid) }

        val onCheckedBack = {
            if (validationResult !is ServerValidation.Running) {
                goBack()
            }
        }

        BackHandler { onCheckedBack() }

        ChooseServer(
            availableServers = AvailableServerProvider.toList(ZcashNetwork.fromResources(activity)),
            onBack = onCheckedBack,
            onServerChange = { newEndpoint ->
                scope.launch {
                    validationResult = ServerValidation.Running
                    validationResult = synchronizer.validateServerEndpoint(activity, newEndpoint)

                    Twig.debug { "Choose Server: Validation result: $validationResult" }

                    when (validationResult) {
                        ServerValidation.Valid -> {
                            onSynchronizerClose()

                            val newWallet =
                                wallet.copy(
                                    endpoint = newEndpoint
                                )

                            Twig.debug { "Choose Server: New wallet: ${newWallet.toSafeString()}" }

                            onWalletPersist(newWallet)

                            snackbarHostState.showSnackbar(
                                message = activity.getString(R.string.choose_server_saved),
                                duration = SnackbarDuration.Short
                            )
                        }
                        is ServerValidation.InValid -> {
                            Twig.error { "Choose Server: Failed to validate the new endpoint: $newEndpoint" }
                        }
                        else -> {
                            // Should not happen
                            Twig.warn { "Choose Server: Server validation state: $validationResult" }
                        }
                    }
                }
            },
            snackbarHostState = snackbarHostState,
            validationResult = validationResult,
            wallet = wallet,
        )
    }
}
