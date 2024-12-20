@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.paymentrequest.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.sdk.extension.toZecStringFull
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.BalanceWidgetBigLineOnly
import co.electriccoin.zcash.ui.common.extension.asZecAmountTriple
import co.electriccoin.zcash.ui.common.extension.totalAmount
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.StyledBalance
import co.electriccoin.zcash.ui.design.component.StyledBalanceDefaults
import co.electriccoin.zcash.ui.design.component.ZashiBottomBar
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.fixture.SendConfirmationStateFixture
import co.electriccoin.zcash.ui.screen.exchangerate.widget.StyledExchangeLabel
import co.electriccoin.zcash.ui.screen.paymentrequest.PaymentRequestArgumentsFixture
import co.electriccoin.zcash.ui.screen.paymentrequest.model.PaymentRequestStage
import co.electriccoin.zcash.ui.screen.paymentrequest.model.PaymentRequestState
import co.electriccoin.zcash.ui.screen.send.ext.abbreviated
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SendConfirmationState
import co.electriccoin.zcash.ui.screen.sendconfirmation.view.SendConfirmationExpandedInfo

@Composable
@PreviewScreens
private fun PaymentRequestLoadingPreview() =
    ZcashTheme(forceDarkMode = true) {
        PaymentRequestView(
            state = PaymentRequestState.Loading,
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            snackbarHostState = SnackbarHostState(),
            infoState = SendConfirmationStateFixture.new()
        )
    }

@Composable
@PreviewScreens
private fun PaymentRequestPreview() =
    ZcashTheme(forceDarkMode = false) {
        PaymentRequestView(
            state =
                PaymentRequestState.Prepared(
                    contact = null,
                    exchangeRateState = ExchangeRateState.Data(onRefresh = {}),
                    monetarySeparators = MonetarySeparators.current(),
                    onAddToContacts = {},
                    onContactSupport = { _ -> },
                    onBack = {},
                    onClose = {},
                    onSend = {},
                    zecSend = PaymentRequestArgumentsFixture.new().toZecSend(),
                    stage = PaymentRequestStage.Initial,
                ),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            snackbarHostState = SnackbarHostState(),
            infoState = SendConfirmationStateFixture.new()
        )
    }

@Composable
internal fun PaymentRequestView(
    state: PaymentRequestState,
    topAppBarSubTitleState: TopAppBarSubTitleState,
    snackbarHostState: SnackbarHostState,
    infoState: SendConfirmationState
) {
    when (state) {
        PaymentRequestState.Loading -> {
            CircularScreenProgressIndicator()
        }
        is PaymentRequestState.Prepared -> {
            BlankBgScaffold(
                topBar = {
                    PaymentRequestTopAppBar(
                        onClose = state.onClose,
                        subTitleState = topAppBarSubTitleState,
                    )
                },
                bottomBar = {
                    PaymentRequestBottomBar(state = state)
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
            ) { paddingValues ->
                Box {
                    PaymentRequestContents(
                        state = state,
                        infoState = infoState,
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .verticalScroll(
                                    rememberScrollState()
                                )
                                .scaffoldPadding(paddingValues),
                    )
                    when (state.stage) {
                        PaymentRequestStage.FailureGrpc -> {
                            PaymentRequestSendFailureGrpc(
                                onDone = state.onBack
                            )
                        }
                        is PaymentRequestStage.Failure -> {
                            PaymentRequestSendFailure(
                                onDone = state.onBack,
                                onReport = { status -> state.onContactSupport(status.stackTrace) },
                                stage = state.stage,
                            )
                        }
                        else -> {
                            // No action needed
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentRequestTopAppBar(
    onClose: () -> Unit,
    subTitleState: TopAppBarSubTitleState,
) {
    ZashiSmallTopAppBar(
        subtitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        title = stringResource(id = R.string.payment_request_title),
        navigationAction = {
            IconButton(
                onClick = onClose,
                modifier =
                    Modifier
                        .padding(horizontal = ZcashTheme.dimens.spacingDefault)
                        // Making the size bigger by 3.dp so the rounded image corners are not stripped out
                        .size(43.dp),
            ) {
                Image(
                    painter =
                        painterResource(
                            id = co.electriccoin.zcash.ui.design.R.drawable.ic_close_full
                        ),
                    contentDescription = stringResource(id = R.string.payment_request_close_content_description),
                    modifier =
                        Modifier
                            .padding(all = 3.dp)
                )
            }
        },
    )
}

@Composable
private fun PaymentRequestBottomBar(
    state: PaymentRequestState.Prepared,
    modifier: Modifier = Modifier,
) {
    ZashiBottomBar(modifier = modifier.fillMaxWidth()) {
        ZashiButton(
            text = stringResource(id = R.string.payment_request_send_btn),
            onClick = { state.onSend(state.zecSend.proposal!!) },
            enabled = state.stage != PaymentRequestStage.Sending && state.stage != PaymentRequestStage.Confirmed,
            isLoading = state.stage == PaymentRequestStage.Sending,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun PaymentRequestContents(
    state: PaymentRequestState.Prepared,
    infoState: SendConfirmationState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingXl))

        PaymentRequestBalances(state)

        Spacer(modifier = Modifier.height(24.dp))

        infoState.from?.let {
            SendConfirmationExpandedInfo(it)
            Spacer(modifier = Modifier.height(24.dp))
        }

        PaymentRequestAddresses(state)

        if (state.zecSend.memo.value.isNotEmpty()) {
            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacing4xl))
            PaymentRequestMemo(state)
        }

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacing3xl))

        PaymentRequestAmounts(state)
    }
}

@Composable
private fun PaymentRequestBalances(
    state: PaymentRequestState.Prepared,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BalanceWidgetBigLineOnly(
            parts = state.zecSend.amount.toZecStringFull().asZecAmountTriple(),
            // We don't hide any balance in confirmation screen
            isHideBalances = false
        )

        StyledExchangeLabel(
            zatoshi = state.zecSend.amount,
            state = state.exchangeRateState,
            isHideBalances = false,
            style = ZashiTypography.textMd.copy(fontWeight = FontWeight.SemiBold),
            textColor = ZashiColors.Text.textPrimary
        )
    }
}

@Composable
private fun PaymentRequestAddresses(
    state: PaymentRequestState.Prepared,
    modifier: Modifier = Modifier
) {
    var isShowingFullAddress by rememberSaveable {
        mutableStateOf(
            when (state.zecSend.destination) {
                is WalletAddress.Transparent -> true
                else -> false
            }
        )
    }
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.payment_request_requested_by),
            color = ZashiColors.Text.textTertiary,
            style = ZashiTypography.textSm,
            fontWeight = FontWeight.Medium
        )

        if (state.contact != null) {
            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingSm))
            Text(
                text = state.contact.name,
                color = ZashiColors.Inputs.Filled.label,
                style = ZashiTypography.textSm,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingSm))

        Text(
            text =
                if (isShowingFullAddress) {
                    state.zecSend.destination.address
                } else {
                    state.zecSend.destination.abbreviated()
                },
            color = ZashiColors.Text.textPrimary,
            style = ZashiTypography.textXs,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.animateContentSize()
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingXl))

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
        ) {
            if (state.zecSend.destination !is WalletAddress.Transparent) {
                if (isShowingFullAddress) {
                    PaymentRequestChipText(
                        text = stringResource(id = R.string.payment_request_btn_hide_address),
                        icon = painterResource(id = R.drawable.ic_chevron_up),
                        onClick = { isShowingFullAddress = false }
                    )
                } else {
                    PaymentRequestChipText(
                        text = stringResource(id = R.string.payment_request_btn_show_address),
                        icon = painterResource(id = R.drawable.ic_chevron_down),
                        onClick = { isShowingFullAddress = true }
                    )
                }
                Spacer(modifier = Modifier.width(ZashiDimensions.Spacing.spacingLg))
            }
            if (state.contact == null) {
                PaymentRequestChipText(
                    text = stringResource(id = R.string.payment_request_btn_save_contact),
                    icon = painterResource(id = R.drawable.ic_user_plus),
                    onClick = { state.onAddToContacts(state.zecSend.destination.address) }
                )
            }
        }
    }
}

@Composable
private fun PaymentRequestChipText(
    text: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier =
            modifier
                .background(
                    ZashiColors.Btns.Tertiary.btnTertiaryBg,
                    RoundedCornerShape(ZashiDimensions.Radius.radiusMd)
                )
                .clip(RoundedCornerShape(ZashiDimensions.Radius.radiusMd))
                .clickable { onClick() }
                .padding(
                    horizontal = ZashiDimensions.Spacing.spacingXl,
                    vertical = 10.dp
                ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = icon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(ZashiColors.Btns.Tertiary.btnTertiaryFg)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = ZashiColors.Btns.Tertiary.btnTertiaryFg,
            style = ZashiTypography.textSm,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PaymentRequestMemo(
    state: PaymentRequestState.Prepared,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.payment_request_memo),
            color = ZashiColors.Text.textPrimary,
            style = ZashiTypography.textSm,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingSm))

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(ZashiColors.Inputs.Filled.bg, RoundedCornerShape(ZashiDimensions.Radius.radiusIg))
                    .padding(all = ZashiDimensions.Spacing.spacingXl),
        ) {
            Text(
                text = state.zecSend.memo.value,
                color = ZashiColors.Inputs.Filled.text,
                style = ZashiTypography.textXs,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PaymentRequestAmounts(
    state: PaymentRequestState.Prepared,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.payment_request_fee),
                color = ZashiColors.Text.textTertiary,
                style = ZashiTypography.textSm,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(ZashiDimensions.Spacing.spacingMd))
            StyledBalance(
                balanceParts = state.zecSend.proposal!!.totalFeeRequired().toZecStringFull().asZecAmountTriple(),
                textColor = ZashiColors.Text.textPrimary,
                textStyle =
                    StyledBalanceDefaults.textStyles(
                        mostSignificantPart = ZashiTypography.textSm.copy(fontWeight = FontWeight.SemiBold),
                        leastSignificantPart = ZashiTypography.textXxs.copy(fontWeight = FontWeight.SemiBold),
                    )
            )
        }

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacing2xl))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.payment_request_total),
                color = ZashiColors.Text.textTertiary,
                style = ZashiTypography.textSm,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(ZashiDimensions.Spacing.spacingMd))
            StyledBalance(
                balanceParts = state.zecSend.totalAmount().toZecStringFull().asZecAmountTriple(),
                textColor = ZashiColors.Text.textPrimary,
                textStyle =
                    StyledBalanceDefaults.textStyles(
                        mostSignificantPart = ZashiTypography.textSm.copy(fontWeight = FontWeight.SemiBold),
                        leastSignificantPart = ZashiTypography.textXxs.copy(fontWeight = FontWeight.SemiBold),
                    )
            )
        }
    }
}

@Composable
@Preview("SendConfirmationFailure")
private fun PreviewSendConfirmationFailure() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            PaymentRequestSendFailure(
                onDone = {},
                onReport = {},
                stage = PaymentRequestStage.Failure("Failed - network error", "Failed stackTrace"),
            )
        }
    }
}

@Composable
private fun PaymentRequestSendFailure(
    onDone: () -> Unit,
    onReport: (PaymentRequestStage.Failure) -> Unit,
    stage: PaymentRequestStage.Failure,
) {
    // TODO [#1276]: Once we ensure that the reason contains a localized message, we can leverage it for the UI prompt
    // TODO [#1276]: Consider adding support for a specific exception in AppAlertDialog
    // TODO [#1276]: https://github.com/Electric-Coin-Company/zashi-android/issues/1276

    AppAlertDialog(
        title = stringResource(id = R.string.payment_request_dialog_error_title),
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(id = R.string.payment_request_dialog_error_text),
                    color = ZcashTheme.colors.textPrimary,
                )

                if (stage.error.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

                    Text(
                        text = stage.error,
                        fontStyle = FontStyle.Italic,
                        color = ZcashTheme.colors.textPrimary,
                    )
                }
            }
        },
        confirmButtonText = stringResource(id = R.string.payment_request_dialog_error_ok_btn),
        onConfirmButtonClick = onDone,
        dismissButtonText = stringResource(id = R.string.payment_request_dialog_error_report_btn),
        onDismissButtonClick = { onReport(stage) },
    )
}

@Composable
private fun PaymentRequestSendFailureGrpc(onDone: () -> Unit) {
    AppAlertDialog(
        title = stringResource(id = R.string.payment_request_dialog_error_grpc_title),
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(id = R.string.payment_request_dialog_error_grpc_text),
                    color = ZcashTheme.colors.textPrimary,
                )
            }
        },
        confirmButtonText = stringResource(id = R.string.payment_request_dialog_error_grpc_btn),
        onConfirmButtonClick = onDone
    )
}
