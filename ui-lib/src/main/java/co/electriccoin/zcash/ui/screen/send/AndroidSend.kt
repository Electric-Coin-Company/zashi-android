@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.send

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.isFailedSubmit
import cash.z.ecc.android.sdk.model.isSubmitSuccess
import cash.z.ecc.sdk.send
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.home.model.spendableBalance
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.send.ext.Saver
import co.electriccoin.zcash.ui.screen.send.model.SendArgumentsWrapper
import co.electriccoin.zcash.ui.screen.send.model.SendStage
import co.electriccoin.zcash.ui.screen.send.view.Send
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.WrapSend(
    sendArgumentsWrapper: SendArgumentsWrapper?,
    goToQrScanner: () -> Unit,
    goBack: () -> Unit
) {
    WrapSend(this, sendArgumentsWrapper, goToQrScanner, goBack)
}

@Composable
private fun WrapSend(
    activity: ComponentActivity,
    sendArgumentsWrapper: SendArgumentsWrapper?,
    goToQrScanner: () -> Unit,
    goBack: () -> Unit
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val spendableBalance = walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value?.spendableBalance()

    val spendingKey = walletViewModel.spendingKey.collectAsStateWithLifecycle().value

    WrapSend(sendArgumentsWrapper, synchronizer, spendableBalance, spendingKey, goToQrScanner, goBack)
}

@Suppress("LongParameterList")
@VisibleForTesting
@Composable
internal fun WrapSend(
    sendArgumentsWrapper: SendArgumentsWrapper?,
    synchronizer: Synchronizer?,
    spendableBalance: Zatoshi?,
    spendingKey: UnifiedSpendingKey?,
    goToQrScanner: () -> Unit,
    goBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    // For now, we're avoiding sub-navigation to keep the navigation logic simple.  But this might
    // change once deep-linking support  is added.  It depends on whether deep linking should do one of:
    // 1. Use a different UI flow entirely
    // 2. Show a pre-filled Send form
    // 3. Go directly to the Confirmation screen
    val (sendStage, setSendStage) = rememberSaveable { mutableStateOf(SendStage.Form) }

    val (zecSend, setZecSend) = rememberSaveable(stateSaver = ZecSend.Saver) { mutableStateOf(null) }

    val onBackAction = {
        when (sendStage) {
            SendStage.Form -> goBack()
            SendStage.Confirmation -> setSendStage(SendStage.Form)
            SendStage.Sending -> { /* no action - wait until done */ }
            SendStage.SendFailure -> setSendStage(SendStage.Form)
            SendStage.SendSuccessful -> goBack()
        }
    }

    BackHandler {
        onBackAction()
    }

    if (null == synchronizer || null == spendableBalance || null == spendingKey) {
        // Display loading indicator
    } else {
        Send(
            mySpendableBalance = spendableBalance,
            sendArgumentsWrapper = sendArgumentsWrapper,
            sendStage = sendStage,
            onSendStageChange = setSendStage,
            zecSend = zecSend,
            onZecSendChange = setZecSend,
            onBack = onBackAction,
            onCreateAndSend = {
                scope.launch {
                    synchronizer.send(spendingKey, it).collect {
                        Twig.debug { "Sending transaction id: ${it.id}" }

                        if (it.isSubmitSuccess()) {
                            setSendStage(SendStage.SendSuccessful)
                            Twig.debug {
                                "Transaction id:${it.id} submitted successfully at ${it.createTime} with " +
                                    "${it.submitAttempts} attempts."
                            }
                        } else if (it.isFailedSubmit()) {
                            Twig.debug { "Transaction id:${it.id} submission failed with: ${it.errorMessage}." }
                            setSendStage(SendStage.SendFailure)
                        }
                        // All other states of Pending transaction mean waiting for one of the states above
                    }
                }
            },
            onQrScannerOpen = goToQrScanner
        )
    }
}
