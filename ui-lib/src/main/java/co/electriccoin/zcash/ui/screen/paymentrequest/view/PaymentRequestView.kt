@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.paymentrequest.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.sdk.extension.toZecStringFull
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.BalanceWidgetBigLineOnly
import co.electriccoin.zcash.ui.common.extension.asZecAmountTriple
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.ZashiBottomBar
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.screen.exchangerate.widget.StyledExchangeLabel
import co.electriccoin.zcash.ui.screen.paymentrequest.PaymentRequestArgumentsFixture
import co.electriccoin.zcash.ui.screen.paymentrequest.model.PaymentRequestState
import co.electriccoin.zcash.ui.screen.send.ext.abbreviated

@Composable
@PreviewScreens
private fun PaymentRequestLoadingPreview() =
    ZcashTheme(forceDarkMode = true) {
        PaymentRequestView(
            state = PaymentRequestState.Loading,
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }

@Composable
@PreviewScreens
private fun PaymentRequestPreview() =
    ZcashTheme(forceDarkMode = false) {
        PaymentRequestView(
            state =
                PaymentRequestState.Prepared(
                    onClose = {},
                    onSend = {},
                    monetarySeparators = MonetarySeparators.current(),
                    arguments = PaymentRequestArgumentsFixture.new(),
                    zecSend = PaymentRequestArgumentsFixture.new().toZecSend(),
                    exchangeRateState = ExchangeRateState.Data(onRefresh = {})
                ),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }

@Composable
internal fun PaymentRequestView(
    state: PaymentRequestState,
    topAppBarSubTitleState: TopAppBarSubTitleState,
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
                }
            ) { paddingValues ->
                PaymentRequestContents(
                    state = state,
                    modifier =
                    Modifier
                        .fillMaxHeight()
                        .verticalScroll(
                            rememberScrollState()
                        )
                        .scaffoldPadding(paddingValues),
                )
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
            onClick = { state.onSend(state.arguments.zip321Uri) },
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
    modifier: Modifier = Modifier,
) {
    var showFullAddress by rememberSaveable {
        mutableStateOf(
            when (state.zecSend.destination) {
                is WalletAddress.Transparent -> true
                else -> false
            }
        )
    }

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingXl))

        Column(
            modifier = Modifier.fillMaxWidth(),
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

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacing6xl))

        Text(
            text = stringResource(id = R.string.payment_request_requested_by),
            color = ZashiColors.Text.textTertiary,
            style = ZashiTypography.textSm,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingSm))

        Text(
            text = if (showFullAddress) {
                state.zecSend.destination.address
            } else {
                state.zecSend.destination.abbreviated()
            },
            color = ZashiColors.Text.textPrimary,
            style = ZashiTypography.textXs,
            fontWeight = FontWeight.Normal
        )
    }
}
