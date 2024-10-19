@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.paymentrequest.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
                    arguments = PaymentRequestArgumentsFixture.new(),
                    contact = null,
                    exchangeRateState = ExchangeRateState.Data(onRefresh = {}),
                    monetarySeparators = MonetarySeparators.current(),
                    onAddToContacts = {},
                    onClose = {},
                    onSend = {},
                    zecSend = PaymentRequestArgumentsFixture.new().toZecSend(),
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
            onClick = { state.onSend(state.arguments.zip321Uri!!) },
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
    var isShowingFullAddress by rememberSaveable {
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

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacing4xl))

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
            text = if (isShowingFullAddress) {
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
            modifier = Modifier
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
        modifier = modifier
            .background(ZashiColors.Btns.Tertiary.btnTertiaryBg, RoundedCornerShape(ZashiDimensions.Radius.radiusMd))
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
