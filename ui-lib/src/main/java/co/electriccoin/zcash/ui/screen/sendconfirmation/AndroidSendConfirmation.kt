@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.sendconfirmation

import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.android.sdk.model.ZecSend
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.send.ext.Saver
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SendConfirmationArgsWrapper
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SendConfirmationStage
import co.electriccoin.zcash.ui.screen.sendconfirmation.view.SendConfirmation
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.WrapSendConfirmation(
    goBack: () -> Unit,
    goHome: () -> Unit,
    arguments: SendConfirmationArgsWrapper
) {
    val walletViewModel by this.viewModels<WalletViewModel>()

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val spendingKey = walletViewModel.spendingKey.collectAsStateWithLifecycle().value

    WrapSendConfirmation(
        arguments,
        synchronizer,
        spendingKey,
        goBack,
        goHome,
    )
}

@VisibleForTesting
@Composable
internal fun WrapSendConfirmation(
    arguments: SendConfirmationArgsWrapper,
    synchronizer: Synchronizer?,
    spendingKey: UnifiedSpendingKey?,
    goBack: () -> Unit,
    goHome: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val zecSend by rememberSaveable(stateSaver = ZecSend.Saver) { mutableStateOf(arguments.toZecSend()) }

    // Because of the [zecSend] has the same Saver as on the Send screen, we do not expect this to be ever null
    checkNotNull(zecSend)

    val (stage, setStage) =
        rememberSaveable(stateSaver = SendConfirmationStage.Saver) {
            mutableStateOf(SendConfirmationStage.Confirmation)
        }

    val onBackAction = {
        when (stage) {
            SendConfirmationStage.Confirmation -> goBack()
            SendConfirmationStage.Sending -> { /* no action - wait until the sending is done */ }
            is SendConfirmationStage.Failure -> setStage(SendConfirmationStage.Confirmation)
            is SendConfirmationStage.MultipleTrxFailure -> {
                // TODO [#1161]: Remove Send-Success and rework Send-Failure
                // TODO [#1161]: https://github.com/Electric-Coin-Company/zashi-android/issues/1161
                setStage(SendConfirmationStage.Confirmation)
            }
        }
    }

    BackHandler {
        onBackAction()
    }

    if (null == synchronizer || null == spendingKey) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        SendConfirmation(
            stage = stage,
            onStageChange = setStage,
            zecSend = zecSend!!,
            onBack = onBackAction,
            onCreateAndSend = { newZecSend ->
                scope.launch {
                    Twig.debug { "Sending transactions" }
                    // TODO [#1294]: Add Send.Multiple-Trx-Failed screen
                    // TODO [#1294]: Note that the following processing is not entirely correct and will be reworked
                    // TODO [#1294]: https://github.com/Electric-Coin-Company/zashi-android/issues/1294
                    runCatching {
                        // The not-null assertion operator is necessary here even if we check its nullability before
                        // due to: "Smart cast to 'Proposal' is impossible, because 'zecSend.proposal' is a public API
                        // property declared in different module
                        // See more details on the Kotlin forum
                        checkNotNull(newZecSend.proposal)
                        synchronizer.createProposedTransactions(newZecSend.proposal!!, spendingKey).collect {
                            Twig.info { "Printing only for now. Will be reworked. Result: $it" }
                        }
                    }
                        .onSuccess {
                            Twig.debug { "Transaction submitted successfully" }
                            setStage(SendConfirmationStage.Confirmation)
                            goHome()
                        }
                        .onFailure {
                            Twig.error(it) { "Transaction submission failed" }
                            setStage(SendConfirmationStage.Failure(it.message ?: ""))
                        }
                }
            }
        )
    }
}
