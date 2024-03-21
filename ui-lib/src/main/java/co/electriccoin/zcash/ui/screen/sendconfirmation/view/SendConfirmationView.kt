package co.electriccoin.zcash.ui.screen.sendconfirmation.view

import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.toZecString
import cash.z.ecc.sdk.fixture.MemoFixture
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.BalanceWidgetBigLineOnly
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.Small
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.StyledBalance
import co.electriccoin.zcash.ui.design.component.Tiny
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.sendconfirmation.SendConfirmationTag
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SendConfirmationStage
import kotlinx.coroutines.runBlocking

@Composable
@Preview("SendConfirmationFailure")
private fun PreviewSendConfirmationFailure() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            SendFailure(
                onDone = {},
                reason = "Failed due to network error"
            )
        }
    }
}

@Composable
@Preview("SendConfirmation")
private fun PreviewSendConfirmation() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
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
}

// TODO [#1260]: Cover Send screens UI with tests
// TODO [#1260]: https://github.com/Electric-Coin-Company/zashi-android/issues/1260

@Composable
fun SendConfirmation(
    stage: SendConfirmationStage,
    onStageChange: (SendConfirmationStage) -> Unit,
    zecSend: ZecSend,
    onBack: () -> Unit,
    onCreateAndSend: (ZecSend) -> Unit,
) {
    Scaffold(topBar = {
        SendConfirmationTopAppBar()
    }) { paddingValues ->
        SendConfirmationMainContent(
            onBack = onBack,
            stage = stage,
            onStageChange = onStageChange,
            zecSend = zecSend,
            onSendSubmit = onCreateAndSend,
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
private fun SendConfirmationTopAppBar() {
    SmallTopAppBar(
        titleText = stringResource(id = R.string.send_stage_confirmation_title)
    )
}

@Composable
@Suppress("LongParameterList")
private fun SendConfirmationMainContent(
    onBack: () -> Unit,
    zecSend: ZecSend,
    stage: SendConfirmationStage,
    onStageChange: (SendConfirmationStage) -> Unit,
    onSendSubmit: (ZecSend) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (stage) {
        SendConfirmationStage.Confirmation -> {
            SendConfirmationContent(
                zecSend = zecSend,
                onBack = onBack,
                onConfirmation = {
                    onStageChange(SendConfirmationStage.Sending)
                    onSendSubmit(zecSend)
                },
                isSending = false,
                modifier = modifier
            )
        }
        SendConfirmationStage.Sending -> {
            SendConfirmationContent(
                zecSend = zecSend,
                onBack = onBack,
                onConfirmation = {},
                isSending = true,
                modifier = modifier
            )
        }
        is SendConfirmationStage.Failure -> {
            SendFailure(
                onDone = onBack,
                reason = stage.error,
            )
        }
        is SendConfirmationStage.MultipleTrxFailure -> {
            // TODO [#1161]: Remove Send-Success and rework Send-Failure
            // TODO [#1161]: https://github.com/Electric-Coin-Company/zashi-android/issues/1161
            SendFailure(
                onDone = onBack,
                reason = stage.error,
            )
        }
    }
}

const val DEFAULT_LESS_THAN_FEE = 100_000L

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

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingXlarge))

        Small(stringResource(R.string.send_confirmation_fee))

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingTiny))

        StyledBalance(
            balanceString =
                if (zecSend.proposal == null) {
                    Zatoshi(DEFAULT_LESS_THAN_FEE).toZecString()
                } else {
                    // The not-null assertion operator is necessary here even if we check its nullability before
                    // due to: "Smart cast to 'Proposal' is impossible, because 'zecSend.proposal' is a public API
                    // property declared in different module
                    // See more details on the Kotlin forum
                    checkNotNull(zecSend.proposal)
                    zecSend.proposal!!.totalFeeRequired().toZecString()
                },
            textStyles =
                Pair(
                    ZcashTheme.extendedTypography.balanceSingleStyles.first,
                    ZcashTheme.extendedTypography.balanceSingleStyles.second
                )
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingXlarge))

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

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingXlarge))
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
@Suppress("UNUSED_PARAMETER")
private fun SendFailure(
    onDone: () -> Unit,
    reason: String,
) {
    // Once we ensure that the [reason] contains a localized message, we can leverage it for the UI prompt

    AppAlertDialog(
        title = stringResource(id = R.string.send_confirmation_dialog_error_title),
        text = stringResource(id = R.string.send_confirmation_dialog_error_text),
        confirmButtonText = stringResource(id = R.string.send_confirmation_dialog_error_btn),
        onConfirmButtonClick = onDone
    )
}
