@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.sendconfirmation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import cash.z.ecc.android.sdk.model.FirstClassByteArray
import cash.z.ecc.android.sdk.model.TransactionSubmitResult
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.toZecString
import cash.z.ecc.sdk.fixture.MemoFixture
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.BalanceWidgetBigLineOnly
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.Small
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.StyledBalance
import co.electriccoin.zcash.ui.design.component.Tiny
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.sendconfirmation.SendConfirmationTag
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SendConfirmationStage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.runBlocking

@Composable
@Preview("SendConfirmation")
private fun PreviewSendConfirmation() {
    ZcashTheme(forceDarkMode = false) {
        SendConfirmationContent(
            zecSend =
                ZecSend(
                    destination = runBlocking { WalletAddressFixture.sapling() },
                    amount = ZatoshiFixture.new(),
                    memo = MemoFixture.new(),
                    proposal = null,
                ),
            onConfirmation = {},
            onBack = {},
            isSending = false
        )
    }
}

@Composable
@Preview("SendMultipleTransactionFailure")
private fun PreviewSendMultipleTransactionFailure() {
    ZcashTheme(forceDarkMode = false) {
        @Suppress("MagicNumber")
        MultipleSubmissionFailure(
            onContactSupport = {},
            // Rework this into a test fixture
            submissionResults =
                persistentListOf(
                    TransactionSubmitResult.Failure(
                        FirstClassByteArray("test_transaction_id_1".toByteArray()),
                        true,
                        123,
                        "test transaction id failure"
                    ),
                    TransactionSubmitResult.NotAttempted(
                        FirstClassByteArray("test_transaction_id_2".toByteArray())
                    ),
                    TransactionSubmitResult.NotAttempted(
                        FirstClassByteArray("test_transaction_id_3".toByteArray())
                    )
                )
        )
    }
}

// TODO [#1260]: Cover Send screens UI with tests
// TODO [#1260]: https://github.com/Electric-Coin-Company/zashi-android/issues/1260

@Composable
@Suppress("LongParameterList")
fun SendConfirmation(
    onBack: () -> Unit,
    onContactSupport: () -> Unit,
    onConfirmation: () -> Unit,
    snackbarHostState: SnackbarHostState,
    stage: SendConfirmationStage,
    submissionResults: ImmutableList<TransactionSubmitResult>,
    zecSend: ZecSend,
    walletRestoringState: WalletRestoringState,
) {
    BlankBgScaffold(
        topBar = {
            SendConfirmationTopAppBar(
                onBack = onBack,
                stage = stage,
                showRestoring = walletRestoringState == WalletRestoringState.RESTORING,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        SendConfirmationMainContent(
            onBack = onBack,
            onContactSupport = onContactSupport,
            onConfirmation = onConfirmation,
            stage = stage,
            submissionResults = submissionResults,
            zecSend = zecSend,
            modifier =
                Modifier
                    .padding(
                        top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingDefault,
                        bottom = paddingValues.calculateBottomPadding(),
                        start = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                        end = ZcashTheme.dimens.screenHorizontalSpacingRegular
                    )
        )
    }
}

@Composable
private fun SendConfirmationTopAppBar(
    onBack: () -> Unit,
    stage: SendConfirmationStage,
    showRestoring: Boolean
) {
    when (stage) {
        SendConfirmationStage.Confirmation,
        SendConfirmationStage.Sending,
        is SendConfirmationStage.Failure -> {
            SmallTopAppBar(
                restoringLabel =
                    if (showRestoring) {
                        stringResource(id = R.string.restoring_wallet_label)
                    } else {
                        null
                    },
                titleText = stringResource(id = R.string.send_stage_confirmation_title),
            )
        }
        SendConfirmationStage.MultipleTrxFailure -> {
            SmallTopAppBar(
                restoringLabel =
                    if (showRestoring) {
                        stringResource(id = R.string.restoring_wallet_label)
                    } else {
                        null
                    },
                titleText = stringResource(id = R.string.send_confirmation_multiple_error_title),
            )
        }
        SendConfirmationStage.MultipleTrxFailureReported -> {
            SmallTopAppBar(
                restoringLabel =
                    if (showRestoring) {
                        stringResource(id = R.string.restoring_wallet_label)
                    } else {
                        null
                    },
                titleText = stringResource(id = R.string.send_confirmation_multiple_error_title),
                backText = stringResource(id = R.string.send_confirmation_multiple_error_back),
                backContentDescriptionText =
                    stringResource(
                        id = R.string.send_confirmation_multiple_error_back_content_description
                    ),
                onBack = onBack,
            )
        }
    }
}

@Composable
@Suppress("LongParameterList")
private fun SendConfirmationMainContent(
    onBack: () -> Unit,
    onContactSupport: () -> Unit,
    onConfirmation: () -> Unit,
    stage: SendConfirmationStage,
    submissionResults: ImmutableList<TransactionSubmitResult>,
    zecSend: ZecSend,
    modifier: Modifier = Modifier,
) {
    when (stage) {
        SendConfirmationStage.Confirmation, SendConfirmationStage.Sending, is SendConfirmationStage.Failure -> {
            SendConfirmationContent(
                zecSend = zecSend,
                onBack = onBack,
                onConfirmation = onConfirmation,
                isSending = stage == SendConfirmationStage.Sending,
                modifier = modifier
            )
            if (stage is SendConfirmationStage.Failure) {
                SendFailure(
                    onDone = onBack,
                    reason = stage.error,
                )
            }
        }
        is SendConfirmationStage.MultipleTrxFailure, SendConfirmationStage.MultipleTrxFailureReported -> {
            MultipleSubmissionFailure(
                onContactSupport = onContactSupport,
                submissionResults = submissionResults,
                modifier = modifier
            )
        }
    }
}

@Composable
@Suppress("LongMethod")
private fun SendConfirmationContent(
    zecSend: ZecSend,
    onConfirmation: () -> Unit,
    onBack: () -> Unit,
    isSending: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        Small(stringResource(R.string.send_confirmation_amount))

        BalanceWidgetBigLineOnly(text = zecSend.amount.toZecString())

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Small(stringResource(R.string.send_confirmation_address))

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingTiny))

        Tiny(zecSend.destination.address)

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingUpLarge))

        Small(stringResource(R.string.send_confirmation_fee))

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingTiny))

        StyledBalance(
            // The not-null assertion operator is necessary here even if we check its nullability before
            // due to: "Smart cast to 'Proposal' is impossible, because 'zecSend.proposal' is a public API
            // property declared in different module. See more details on the Kotlin forum.
            balanceString = zecSend.proposal!!.totalFeeRequired().toZecString(),
            textStyles =
                Pair(
                    ZcashTheme.extendedTypography.balanceSingleStyles.first,
                    ZcashTheme.extendedTypography.balanceSingleStyles.second
                )
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingUpLarge))

        if (zecSend.memo.value.isNotEmpty()) {
            Small(stringResource(R.string.send_confirmation_memo))

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingTiny))

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = ZcashTheme.colors.textFieldFrame)
            ) {
                Tiny(
                    text = zecSend.memo.value,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(all = ZcashTheme.dimens.spacingMid)
                )
            }

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingUpLarge))
        }

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        SendConfirmationActionButtons(
            isSending = isSending,
            onBack = onBack,
            onConfirmation = onConfirmation
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingHuge))
    }
}

const val BUTTON_WIDTH_RATIO = 0.5f

@Composable
fun SendConfirmationActionButtons(
    onConfirmation: () -> Unit,
    onBack: () -> Unit,
    isSending: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        PrimaryButton(
            text = stringResource(id = R.string.send_confirmation_send_button),
            onClick = onConfirmation,
            enabled = !isSending,
            showProgressBar = isSending,
            minHeight = ZcashTheme.dimens.buttonHeightSmall,
            modifier =
                Modifier
                    .testTag(SendConfirmationTag.SEND_CONFIRMATION_SEND_BUTTON)
                    .weight(BUTTON_WIDTH_RATIO)
        )

        Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingLarge))

        PrimaryButton(
            text = stringResource(R.string.send_confirmation_back_button),
            onClick = onBack,
            enabled = !isSending,
            minHeight = ZcashTheme.dimens.buttonHeightSmall,
            modifier =
                Modifier
                    .testTag(SendConfirmationTag.SEND_CONFIRMATION_BACK_BUTTON)
                    .weight(BUTTON_WIDTH_RATIO)
        )
    }
}

@Composable
@Preview("SendConfirmationFailure")
private fun PreviewSendConfirmationFailure() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            SendFailure(
                onDone = {},
                reason = "Failed due to network error"
            )
        }
    }
}

@Composable
private fun SendFailure(
    onDone: () -> Unit,
    reason: String?,
) {
    // TODO [#1276]: Once we ensure that the reason contains a localized message, we can leverage it for the UI prompt
    // TODO [#1276]: Consider adding support for a specific exception in AppAlertDialog
    // TODO [#1276]: https://github.com/Electric-Coin-Company/zashi-android/issues/1276

    AppAlertDialog(
        title = stringResource(id = R.string.send_confirmation_dialog_error_title),
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(text = stringResource(id = R.string.send_confirmation_dialog_error_text))

                if (!reason.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

                    Text(
                        text = reason,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        },
        confirmButtonText = stringResource(id = R.string.send_confirmation_dialog_error_btn),
        onConfirmButtonClick = onDone
    )
}

@Composable
fun MultipleSubmissionFailure(
    onContactSupport: () -> Unit,
    submissionResults: ImmutableList<TransactionSubmitResult>,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.zashi_logo_sign),
                contentDescription = null,
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_alert_circle_fill),
                contentDescription = null,
                modifier = Modifier.padding(bottom = ZcashTheme.dimens.spacingMid)
            )
        }

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingBig))

        Body(
            text = stringResource(id = R.string.send_confirmation_multiple_error_text_1),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Body(
            text = stringResource(id = R.string.send_confirmation_multiple_error_text_2),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingBig))

        if (submissionResults.isNotEmpty()) {
            TransactionSubmitResultWidget(submissionResults)
        }

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Spacer(modifier = Modifier.weight(1f, true))

        PrimaryButton(
            onClick = onContactSupport,
            text = stringResource(id = R.string.send_confirmation_multiple_error_btn)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingHuge))
    }
}

@Composable
fun TransactionSubmitResultWidget(
    submissionResults: ImmutableList<TransactionSubmitResult>,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Small(text = stringResource(id = R.string.send_confirmation_multiple_error_trx_title))

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        submissionResults.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Small(
                    text =
                        stringResource(
                            id = R.string.send_confirmation_multiple_error_trx_item,
                            index + 1
                        ),
                    modifier = Modifier.wrapContentSize()
                )
                Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingTiny))
                Small(text = item.txIdString())
            }
        }
    }
}
