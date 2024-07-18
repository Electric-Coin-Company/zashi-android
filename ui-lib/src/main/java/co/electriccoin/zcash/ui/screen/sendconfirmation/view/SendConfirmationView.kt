@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.sendconfirmation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import cash.z.ecc.android.sdk.model.FirstClassByteArray
import cash.z.ecc.android.sdk.model.TransactionSubmitResult
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.sdk.extension.toZecStringFull
import cash.z.ecc.sdk.fixture.MemoFixture
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.BalanceWidgetBigLineOnly
import co.electriccoin.zcash.ui.common.extension.asZecAmountTriple
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.BubbleArrowAlignment
import co.electriccoin.zcash.ui.design.component.BubbleMessage
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.SecondaryButton
import co.electriccoin.zcash.ui.design.component.Small
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.StyledBalance
import co.electriccoin.zcash.ui.design.component.StyledBalanceDefaults
import co.electriccoin.zcash.ui.design.component.Tiny
import co.electriccoin.zcash.ui.design.component.TopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.sendconfirmation.SendConfirmationTag
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SendConfirmationStage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.runBlocking

@Preview
@Composable
private fun SendConfirmationPreview() {
    ZcashTheme(forceDarkMode = false) {
        SendConfirmation(
            snackbarHostState = SnackbarHostState(),
            zecSend =
                ZecSend(
                    destination = runBlocking { WalletAddressFixture.sapling() },
                    amount = ZatoshiFixture.new(),
                    memo = MemoFixture.new(),
                    proposal = null,
                ),
            onConfirmation = {},
            onBack = {},
            stage = SendConfirmationStage.Confirmation,
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            onContactSupport = {},
            submissionResults = emptyList<TransactionSubmitResult>().toImmutableList()
        )
    }
}

@Preview
@Composable
private fun SendConfirmationDarkPreview() {
    ZcashTheme(forceDarkMode = true) {
        SendConfirmation(
            snackbarHostState = SnackbarHostState(),
            zecSend =
                ZecSend(
                    destination = runBlocking { WalletAddressFixture.sapling() },
                    amount = ZatoshiFixture.new(),
                    memo = MemoFixture.new(),
                    proposal = null,
                ),
            onConfirmation = {},
            onBack = {},
            stage = SendConfirmationStage.Confirmation,
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            onContactSupport = {},
            submissionResults = emptyList<TransactionSubmitResult>().toImmutableList()
        )
    }
}

@Preview
@Composable
private fun SendMultipleErrorPreview() {
    ZcashTheme(forceDarkMode = false) {
        SendConfirmation(
            snackbarHostState = SnackbarHostState(),
            zecSend =
                ZecSend(
                    destination = runBlocking { WalletAddressFixture.sapling() },
                    amount = ZatoshiFixture.new(),
                    memo = MemoFixture.new(),
                    proposal = null,
                ),
            onConfirmation = {},
            onBack = {},
            stage = SendConfirmationStage.MultipleTrxFailure,
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            onContactSupport = {},
            submissionResults = emptyList<TransactionSubmitResult>().toImmutableList()
        )
    }
}

@Preview
@Composable
private fun SendMultipleErrorDarkPreview() {
    ZcashTheme(forceDarkMode = true) {
        SendConfirmation(
            snackbarHostState = SnackbarHostState(),
            zecSend =
                ZecSend(
                    destination = runBlocking { WalletAddressFixture.sapling() },
                    amount = ZatoshiFixture.new(),
                    memo = MemoFixture.new(),
                    proposal = null,
                ),
            onConfirmation = {},
            onBack = {},
            stage = SendConfirmationStage.MultipleTrxFailure,
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            onContactSupport = {},
            submissionResults = emptyList<TransactionSubmitResult>().toImmutableList()
        )
    }
}

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

@Preview
@Composable
private fun SendMultipleTransactionFailurePreview() {
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

@Preview
@Composable
private fun SendMultipleTransactionFailureDarkPreview() {
    ZcashTheme(forceDarkMode = true) {
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
    onConfirmation: () -> Unit,
    onContactSupport: () -> Unit,
    snackbarHostState: SnackbarHostState,
    stage: SendConfirmationStage,
    submissionResults: ImmutableList<TransactionSubmitResult>,
    topAppBarSubTitleState: TopAppBarSubTitleState,
    zecSend: ZecSend,
) {
    BlankBgScaffold(
        topBar = {
            SendConfirmationTopAppBar(
                onBack = onBack,
                stage = stage,
                subTitleState = topAppBarSubTitleState,
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
    subTitleState: TopAppBarSubTitleState
) {
    val subTitle =
        when (subTitleState) {
            TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
            TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
            TopAppBarSubTitleState.None -> null
        }
    when (stage) {
        SendConfirmationStage.Confirmation,
        SendConfirmationStage.Sending,
        is SendConfirmationStage.Failure -> {
            SmallTopAppBar(
                subTitle = subTitle,
                titleText = stringResource(id = R.string.send_stage_confirmation_title),
            )
        }
        SendConfirmationStage.MultipleTrxFailure -> {
            SmallTopAppBar(
                subTitle = subTitle,
                titleText = stringResource(id = R.string.send_confirmation_multiple_error_title),
            )
        }
        SendConfirmationStage.MultipleTrxFailureReported -> {
            SmallTopAppBar(
                subTitle = subTitle,
                titleText = stringResource(id = R.string.send_confirmation_multiple_error_title),
                navigationAction = {
                    TopAppBarBackNavigation(
                        backText = stringResource(id = R.string.back_navigation).uppercase(),
                        backContentDescriptionText = stringResource(R.string.back_navigation_content_description),
                        onBack = onBack
                    )
                },
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

        BalanceWidgetBigLineOnly(
            parts = zecSend.amount.toZecStringFull().asZecAmountTriple(),
            // We don't hide any balance in confirmation screen
            isHideBalances = false
        )

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
            balanceParts = zecSend.proposal!!.totalFeeRequired().toZecStringFull().asZecAmountTriple(),
            // We don't hide any balance in confirmation screen
            isHideBalances = false,
            textStyle =
                StyledBalanceDefaults.textStyles(
                    integerPart = ZcashTheme.extendedTypography.balanceSingleStyles.first,
                    floatingPart = ZcashTheme.extendedTypography.balanceSingleStyles.second
                ),
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingUpLarge))

        if (zecSend.memo.value.isNotEmpty()) {
            Small(stringResource(R.string.send_confirmation_memo))

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingTiny))

            BubbleMessage(
                modifier = Modifier.fillMaxWidth(),
                arrowAlignment = BubbleArrowAlignment.BottomLeft,
                backgroundColor = Color.Transparent
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
            buttonColors = ZcashTheme.colors.tertiaryButtonColors,
            modifier =
                Modifier
                    .testTag(SendConfirmationTag.SEND_CONFIRMATION_SEND_BUTTON)
                    .weight(BUTTON_WIDTH_RATIO)
        )

        Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingLarge))

        SecondaryButton(
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
                Text(
                    text = stringResource(id = R.string.send_confirmation_dialog_error_text),
                    color = ZcashTheme.colors.textPrimary,
                )

                if (!reason.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

                    Text(
                        text = reason,
                        fontStyle = FontStyle.Italic,
                        color = ZcashTheme.colors.textPrimary,
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

        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_zashi_logo_sign_warn),
            colorFilter = ColorFilter.tint(color = ZcashTheme.colors.secondaryColor),
            contentDescription = null,
        )

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
