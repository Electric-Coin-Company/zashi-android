@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.sendconfirmation

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import cash.z.ecc.android.sdk.SdkSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.Proposal
import cash.z.ecc.android.sdk.model.TransactionSubmitResult
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.android.sdk.model.ZecSend
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.viewmodel.AuthenticationViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.authentication.AuthenticationUseCase
import co.electriccoin.zcash.ui.screen.authentication.WrapAuthentication
import co.electriccoin.zcash.ui.screen.send.ext.Saver
import co.electriccoin.zcash.ui.screen.sendconfirmation.ext.toSupportString
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SendConfirmationArguments
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SendConfirmationStage
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SubmitResult
import co.electriccoin.zcash.ui.screen.sendconfirmation.view.SendConfirmation
import co.electriccoin.zcash.ui.screen.sendconfirmation.viewmodel.CreateTransactionsViewModel
import co.electriccoin.zcash.ui.screen.support.model.SupportInfo
import co.electriccoin.zcash.ui.screen.support.model.SupportInfoType
import co.electriccoin.zcash.ui.screen.support.viewmodel.SupportViewModel
import co.electriccoin.zcash.ui.util.EmailUtil
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.WrapSendConfirmation(
    goBack: (clearForm: Boolean) -> Unit,
    goHome: () -> Unit,
    goSupport: () -> Unit,
    arguments: SendConfirmationArguments
) {
    val walletViewModel by viewModels<WalletViewModel>()

    val createTransactionsViewModel by viewModels<CreateTransactionsViewModel>()

    val supportViewModel by viewModels<SupportViewModel>()

    val authenticationViewModel by viewModels<AuthenticationViewModel> {
        AuthenticationViewModel.AuthenticationViewModelFactory(application)
    }

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val spendingKey = walletViewModel.spendingKey.collectAsStateWithLifecycle().value

    val supportMessage = supportViewModel.supportInfo.collectAsStateWithLifecycle().value

    val walletState = walletViewModel.walletStateInformation.collectAsStateWithLifecycle().value

    WrapSendConfirmation(
        activity = this,
        arguments = arguments,
        authenticationViewModel = authenticationViewModel,
        createTransactionsViewModel = createTransactionsViewModel,
        goBack = goBack,
        goHome = goHome,
        goSupport = goSupport,
        lifecycleScope = this.lifecycleScope,
        spendingKey = spendingKey,
        supportMessage = supportMessage,
        synchronizer = synchronizer,
        topAppBarSubTitleState = walletState,
    )
}

@VisibleForTesting
@Composable
@Suppress("LongParameterList", "LongMethod", "CyclomaticComplexMethod")
internal fun WrapSendConfirmation(
    activity: MainActivity,
    arguments: SendConfirmationArguments,
    authenticationViewModel: AuthenticationViewModel,
    createTransactionsViewModel: CreateTransactionsViewModel,
    goBack: (clearForm: Boolean) -> Unit,
    goHome: () -> Unit,
    goSupport: () -> Unit,
    lifecycleScope: CoroutineScope,
    spendingKey: UnifiedSpendingKey?,
    supportMessage: SupportInfo?,
    synchronizer: Synchronizer?,
    topAppBarSubTitleState: TopAppBarSubTitleState,
) {
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    // Helper property for triggering the system security UI from callbacks
    val sendFundsAuthentication = rememberSaveable { mutableStateOf(false) }

    val zecSend by rememberSaveable(stateSaver = ZecSend.Saver) { mutableStateOf(arguments.toZecSend()) }
    // ZecSend object and all its properties are not-null! We just use the common Send and Send.Confirmation Saver
    checkNotNull(zecSend!!.proposal)

    val (stage, setStage) =
        rememberSaveable(stateSaver = SendConfirmationStage.Saver) {
            mutableStateOf(arguments.initialStage ?: SendConfirmationStage.Confirmation)
        }

    val submissionResults = createTransactionsViewModel.submissions.collectAsState().value.toImmutableList()

    val onBackAction = {
        when (stage) {
            SendConfirmationStage.Confirmation -> goBack(false)
            SendConfirmationStage.Sending -> { /* no action - wait until the sending is done */ }
            is SendConfirmationStage.Failure -> setStage(SendConfirmationStage.Confirmation)
            is SendConfirmationStage.MultipleTrxFailure -> { /* no action - wait until report the result */ }
            is SendConfirmationStage.MultipleTrxFailureReported -> goBack(true)
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
            zecSend = zecSend!!,
            submissionResults = submissionResults,
            snackbarHostState = snackbarHostState,
            onBack = onBackAction,
            onContactSupport = {
                val fullMessage =
                    formatMessage(
                        context = activity,
                        appInfo = supportMessage,
                        submissionResults = submissionResults
                    )

                val mailIntent =
                    EmailUtil.newMailActivityIntent(
                        activity.getString(R.string.support_email_address),
                        activity.getString(R.string.app_name),
                        fullMessage
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }

                runCatching {
                    activity.startActivity(mailIntent)
                }.onSuccess {
                    setStage(SendConfirmationStage.MultipleTrxFailureReported)
                }.onFailure {
                    setStage(SendConfirmationStage.MultipleTrxFailureReported)
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = activity.getString(R.string.send_confirmation_multiple_report_unable_open_email)
                        )
                    }
                }
            },
            onConfirmation = {
                // Check and trigger authentication if required, or just submit transactions otherwise
                lifecycleScope.launch {
                    authenticationViewModel.isSendFundsAuthenticationRequired
                        .filterNotNull()
                        .collect { isProtected ->
                            if (isProtected) {
                                sendFundsAuthentication.value = true
                            } else {
                                runSendFundsAction(
                                    createTransactionsViewModel = createTransactionsViewModel,
                                    goHome = goHome,
                                    // The not-null assertion operator is necessary here even if we check its
                                    // nullability before due to property is declared in different module. See more
                                    // details on the Kotlin forum
                                    proposal = zecSend!!.proposal!!,
                                    setStage = setStage,
                                    spendingKey = spendingKey,
                                    synchronizer = synchronizer,
                                )
                            }
                        }
                }
            },
            topAppBarSubTitleState = topAppBarSubTitleState,
        )

        if (sendFundsAuthentication.value) {
            activity.WrapAuthentication(
                goSupport = {
                    sendFundsAuthentication.value = false
                    goSupport()
                },
                onSuccess = {
                    lifecycleScope.launch {
                        runSendFundsAction(
                            createTransactionsViewModel = createTransactionsViewModel,
                            goHome = goHome,
                            // The not-null assertion operator is necessary here even if we check its
                            // nullability before due to property is declared in different module. See more
                            // details on the Kotlin forum
                            proposal = zecSend!!.proposal!!,
                            setStage = setStage,
                            spendingKey = spendingKey,
                            synchronizer = synchronizer,
                        )
                    }
                    sendFundsAuthentication.value = false
                },
                onCancel = {
                    sendFundsAuthentication.value = false
                },
                onFailed = {
                    // No action needed
                },
                useCase = AuthenticationUseCase.SendFunds
            )
        }
    }
}

@Suppress("LongParameterList")
suspend fun runSendFundsAction(
    createTransactionsViewModel: CreateTransactionsViewModel,
    goHome: () -> Unit,
    proposal: Proposal,
    setStage: (SendConfirmationStage) -> Unit,
    spendingKey: UnifiedSpendingKey,
    synchronizer: Synchronizer,
) {
    setStage(SendConfirmationStage.Sending)

    val submitResult =
        submitTransactions(
            createTransactionsViewModel = createTransactionsViewModel,
            proposal = proposal,
            synchronizer = synchronizer,
            spendingKey = spendingKey
        )

    Twig.debug { "Transactions submitted with result: $submitResult" }

    processSubmissionResult(
        goHome = goHome,
        setStage = setStage,
        submitResult = submitResult
    )
}

private suspend fun submitTransactions(
    createTransactionsViewModel: CreateTransactionsViewModel,
    proposal: Proposal,
    synchronizer: Synchronizer,
    spendingKey: UnifiedSpendingKey
): SubmitResult {
    Twig.debug { "Sending transactions..." }

    val result =
        createTransactionsViewModel.runCreateTransactions(
            synchronizer = synchronizer,
            spendingKey = spendingKey,
            proposal = proposal
        )

    // Triggering the transaction history and balances refresh to be notified immediately
    // about the wallet's updated state
    (synchronizer as SdkSynchronizer).run {
        refreshTransactions()
        refreshAllBalances()
    }

    return result
}

private fun processSubmissionResult(
    submitResult: SubmitResult,
    setStage: (SendConfirmationStage) -> Unit,
    goHome: () -> Unit
) {
    when (submitResult) {
        SubmitResult.Success -> {
            setStage(SendConfirmationStage.Confirmation)
            goHome()
        }
        is SubmitResult.SimpleTrxFailure -> {
            setStage(SendConfirmationStage.Failure(submitResult.errorDescription))
        }
        is SubmitResult.MultipleTrxFailure -> {
            setStage(SendConfirmationStage.MultipleTrxFailure)
        }
    }
}

private fun formatMessage(
    context: Context,
    appInfo: SupportInfo?,
    supportInfoValues: Set<SupportInfoType> = SupportInfoType.entries.toSet(),
    submissionResults: ImmutableList<TransactionSubmitResult>
): String =
    buildString {
        appendLine(context.getString(R.string.send_confirmation_multiple_report_text))
        appendLine()
        append(appInfo?.toSupportString(supportInfoValues) ?: "")
        if (submissionResults.isNotEmpty()) {
            append(submissionResults.toSupportString(context))
        }
    }
