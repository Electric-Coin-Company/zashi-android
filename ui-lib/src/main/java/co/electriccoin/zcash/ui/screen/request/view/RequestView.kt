@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.request.view

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cash.z.ecc.android.sdk.internal.Twig
import cash.z.ecc.android.sdk.model.Locale
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.toJavaLocale
import cash.z.ecc.sdk.type.ZcashCurrency
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.ZashiBottomBar
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.screen.request.model.AmountState
import co.electriccoin.zcash.ui.screen.request.model.MemoState
import co.electriccoin.zcash.ui.screen.request.model.OnAmount
import co.electriccoin.zcash.ui.screen.request.model.OnSwitch
import co.electriccoin.zcash.ui.screen.request.model.Request
import co.electriccoin.zcash.ui.screen.request.model.RequestState

@Composable
@PreviewScreens
private fun RequestLoadingPreview() =
    ZcashTheme(forceDarkMode = true) {
        RequestView(
            state = RequestState.Loading,
            snackbarHostState = SnackbarHostState(),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }

@Composable
@PreviewScreens
private fun RequestPreview() =
    ZcashTheme(forceDarkMode = false) {
        RequestView(
            state =
                RequestState.Amount(
                    request = Request(
                        amountState = AmountState.Valid("2.25"),
                        memoState = MemoState.Valid(""),
                    ),
                    exchangeRateState = ExchangeRateState.OptedOut,
                    zcashCurrency = ZcashCurrency.ZEC,
                    onAmount = {},
                    onBack = {},
                    onDone = {},
                    onSwitch = {},
                    monetarySeparators = MonetarySeparators.current(Locale.getDefault().toJavaLocale())
                ),
            snackbarHostState = SnackbarHostState(),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }

@Composable
internal fun RequestView(
    state: RequestState,
    snackbarHostState: SnackbarHostState,
    topAppBarSubTitleState: TopAppBarSubTitleState,
) {
    when (state) {
        RequestState.Loading -> {
            CircularScreenProgressIndicator()
        }
        is RequestState.Prepared -> {
            BlankBgScaffold(
                topBar = {
                    RequestTopAppBar(
                        onBack = state.onBack,
                        subTitleState = topAppBarSubTitleState,
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    RequestBottomBar(state = state)
                }
            ) { paddingValues ->
                RequestContents(
                    state = state,
                    modifier =
                        Modifier.padding(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding()
                        ),
                )
            }
        }
    }
}

@Composable
private fun RequestTopAppBar(
    onBack: () -> Unit,
    subTitleState: TopAppBarSubTitleState,
) {
    ZashiSmallTopAppBar(
        subtitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        title = stringResource(id = R.string.request_title),
        navigationAction = {
            ZashiTopAppBarBackNavigation(onBack = onBack)
        },
    )
}

@Composable
private fun RequestBottomBar(
    state: RequestState.Prepared,
    modifier: Modifier = Modifier,
) {
    val btnModifier = modifier
        .padding(horizontal = 24.dp)
        .fillMaxWidth()

    ZashiBottomBar {
        when (state) {
            is RequestState.Amount -> {
                ZashiButton(
                    text = stringResource(id = R.string.request_amount_btn),
                    onClick = { state.onDone() },
                    enabled = state.request.amountState.isValid(),
                    modifier = btnModifier
                )
            }
            is RequestState.Memo -> {
                ZashiButton(
                    text = stringResource(id = R.string.request_memo_btn),
                    onClick = { state.onDone() },
                    modifier = btnModifier
                )
            }
            is RequestState.QrCode -> {
                ZashiButton(
                    text = stringResource(id = R.string.request_share_btn),
                    leadingIcon = painterResource(R.drawable.ic_share),
                    onClick = { state.onDone() },
                    modifier = btnModifier
                )
            }
        }
    }
}

@Composable
private fun RequestContents(
    state: RequestState.Prepared,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        when (state) {
            is RequestState.Amount -> {
                RequestAmountView(state = state)
            }
            is RequestState.Memo -> {
                RequestMemoView(state = state)
            }
            is RequestState.QrCode -> {
                RequestQrCodeView(state = state)
            }
        }
    }
}

@Composable
private fun RequestAmountWithMainFiatView(
    state: RequestState.Amount,
    onFiatPreferenceSwitch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        state.exchangeRateState as ExchangeRateState.Data
        val fiatText = buildAnnotatedString {
            withStyle(style = SpanStyle(color = ZashiColors.Text.textQuaternary)) {
                append(state.exchangeRateState.fiatCurrency.symbol)
            }
            append("\u2009") // Add an extra thin space between the texts
            withStyle(style = SpanStyle(color = ZashiColors.Text.textPrimary)) {
                append(
                    if (state.exchangeRateState.currencyConversion != null) {
                        state.request.amountState.amount
                    } else {
                        stringResource(id = R.string.request_amount_empty)
                    }
                )
            }
        }

        AutoSizingText(
            text = fiatText,
            style = ZashiTypography.header1.copy(
                fontWeight = FontWeight.SemiBold
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            val zecText = buildAnnotatedString {
                withStyle(style = SpanStyle(color = ZashiColors.Text.textPrimary)) {
                    append(
                        if (state.exchangeRateState.currencyConversion != null) {
                            state.request.amountState.toZecString(
                                state.exchangeRateState.currencyConversion
                            )
                        } else {
                            stringResource(id = R.string.request_amount_empty)
                        }
                    )
                }
                append(" ") // Add an extra space between the texts
                withStyle(style = SpanStyle(color = ZashiColors.Text.textQuaternary)) {
                    append(state.zcashCurrency.localizedName(LocalContext.current))
                }
            }

            Text(
                text = zecText,
                style = ZashiTypography.textLg,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )

            Spacer(modifier = Modifier.width(10.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_switch),
                contentDescription = null,
                modifier = Modifier.clickable { onFiatPreferenceSwitch() }
            )
        }
    }
}

@Composable
private fun RequestAmountWithMainZecView(
    state: RequestState.Amount,
    onFiatPreferenceSwitch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        state.exchangeRateState as ExchangeRateState.Data
        val zecText = buildAnnotatedString {
            withStyle(style = SpanStyle(color = ZashiColors.Text.textPrimary)) {
                append(state.request.amountState.amount)
            }
            append("\u2009") // Add an extra thin space between the texts
            withStyle(style = SpanStyle(color = ZashiColors.Text.textQuaternary)) {
                append(state.zcashCurrency.localizedName(LocalContext.current))
            }
        }

        AutoSizingText(
            text = zecText,
            style = ZashiTypography.header1.copy(
                fontWeight = FontWeight.SemiBold
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            val fiatText = buildAnnotatedString {
                withStyle(style = SpanStyle(color = ZashiColors.Text.textQuaternary)) {
                    append(state.exchangeRateState.fiatCurrency.symbol)
                }
                append(" ") // Add an extra space between the texts
                withStyle(style = SpanStyle(color = ZashiColors.Text.textPrimary)) {
                    append(
                        if (state.exchangeRateState.currencyConversion != null) {
                            state.request.amountState.toFiatString(
                                LocalContext.current,
                                state.exchangeRateState.currencyConversion
                            )
                        } else {
                            stringResource(id = R.string.request_amount_empty)
                        }
                    )
                }
            }

            Text(
                text = fiatText,
                style = ZashiTypography.textLg,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.width(10.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_switch),
                contentDescription = null,
                modifier = Modifier.clickable { onFiatPreferenceSwitch() }
            )
        }
    }
}

@Composable
private fun RequestAmountNoFiatView(
    state: RequestState.Amount,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        val fiatText = buildAnnotatedString {
            withStyle(style = SpanStyle(color = ZashiColors.Text.textPrimary)) {
                append(state.request.amountState.amount)
            }
            append("\u2009") // Add an extra thin space between the texts
            withStyle(style = SpanStyle(color = ZashiColors.Text.textQuaternary)) {
                append(state.zcashCurrency.localizedName(LocalContext.current))
            }
        }

        AutoSizingText(
            text = fiatText,
            style = ZashiTypography.header1.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
private fun AutoSizingText(
    text: AnnotatedString,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    var fontSize by remember { mutableStateOf(style.fontSize) }

    Text(
        text = text,
        fontSize = fontSize,
        fontFamily = style.fontFamily,
        lineHeight = style.lineHeight,
        fontWeight = style.fontWeight,
        maxLines = 1,
        modifier = modifier,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowHeight) {
                fontSize = (fontSize.value - 1).sp
            } else {
                // We should make the text bigger again
            }
        }
    )
}

@Composable
private fun RequestAmountView(
    state: RequestState.Amount,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        InvalidAmountView(state.request.amountState)

        var zecValuePreferred by rememberSaveable { mutableStateOf(true) }

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        when(state.exchangeRateState) {
            is ExchangeRateState.Data -> {
                if (zecValuePreferred) {
                    RequestAmountWithMainZecView(
                        state = state,
                        onFiatPreferenceSwitch = {
                            state.onSwitch(OnSwitch.ToFiat)
                            zecValuePreferred = !zecValuePreferred
                        },
                        modifier = Modifier.padding(horizontal = ZcashTheme.dimens.screenHorizontalSpacingRegular)
                    )
                } else {
                    RequestAmountWithMainFiatView(
                        state = state,
                        onFiatPreferenceSwitch = {
                            state.onSwitch(OnSwitch.ToZec)
                            zecValuePreferred = !zecValuePreferred
                        },
                        modifier = Modifier.padding(horizontal = ZcashTheme.dimens.screenHorizontalSpacingRegular)
                    )
                }
            }
            else -> {
                RequestAmountNoFiatView(
                    state = state,
                    modifier = Modifier.padding(horizontal = ZcashTheme.dimens.screenHorizontalSpacingRegular)
                )
            }
        }

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Spacer(modifier = Modifier.weight(1f))

        RequestAmountKeyboardView(
            state = state
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))
    }
}

@Composable
private fun RequestAmountKeyboardView(
    state: RequestState.Amount,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RequestAmountKeyboardTextButton(
                text = stringResource(id = R.string.request_amount_keyboard_one),
                onClick = { state.onAmount(OnAmount.Number(1)) }
            )
            RequestAmountKeyboardTextButton(
                text = stringResource(id = R.string.request_amount_keyboard_four),
                onClick = { state.onAmount(OnAmount.Number(4)) }
            )
            RequestAmountKeyboardTextButton(
                text = stringResource(id = R.string.request_amount_keyboard_seven),
                onClick = { state.onAmount(OnAmount.Number(7)) }
            )
            RequestAmountKeyboardTextButton(
                text = state.monetarySeparators.decimal.toString(),
                onClick = { state.onAmount(OnAmount.Separator(state.monetarySeparators.decimal.toString())) }
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RequestAmountKeyboardTextButton(
                text = stringResource(id = R.string.request_amount_keyboard_two),
                onClick = { state.onAmount(OnAmount.Number(2)) }
            )
            RequestAmountKeyboardTextButton(
                text = stringResource(id = R.string.request_amount_keyboard_five),
                onClick = { state.onAmount(OnAmount.Number(5)) }
            )
            RequestAmountKeyboardTextButton(
                text = stringResource(id = R.string.request_amount_keyboard_eight),
                onClick = { state.onAmount(OnAmount.Number(8)) }
            )
            RequestAmountKeyboardTextButton(
                text = stringResource(id = R.string.request_amount_keyboard_zero),
                onClick = { state.onAmount(OnAmount.Number(0)) }
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RequestAmountKeyboardTextButton(
                text = stringResource(id = R.string.request_amount_keyboard_three),
                onClick = { state.onAmount(OnAmount.Number(3)) }
            )
            RequestAmountKeyboardTextButton(
                text = stringResource(id = R.string.request_amount_keyboard_six),
                onClick = { state.onAmount(OnAmount.Number(6)) }
            )
            RequestAmountKeyboardTextButton(
                text = stringResource(id = R.string.request_amount_keyboard_nine),
                onClick = { state.onAmount(OnAmount.Number(9)) }
            )
            RequestAmountKeyboardIconButton(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = stringResource(id = R.string.request_amount_keyboard_delete),
                onClick = { state.onAmount(OnAmount.Delete) }
            )
        }
    }
}

@Composable
private fun RequestAmountKeyboardTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = ZashiTypography.header4,
        fontWeight = FontWeight.SemiBold,
        color = ZashiColors.Text.textPrimary,
        modifier = modifier
            .wrapContentHeight()
            .clip(RoundedCornerShape(ZcashTheme.dimens.regularRippleEffectCorner))
            .clickable { onClick() }
            .padding(horizontal = 42.dp, vertical = 10.dp)
    )
}

@Composable
private fun RequestAmountKeyboardIconButton(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        colorFilter = ColorFilter.tint(ZashiColors.Btns.Ghost.btnGhostFg),
        modifier = modifier
            .clip(RoundedCornerShape(ZcashTheme.dimens.regularRippleEffectCorner))
            .clickable { onClick() }
            .padding(horizontal = 32.dp, vertical = 10.dp)
    )
}

@Composable
private fun InvalidAmountView(
    amountState: AmountState,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .requiredHeight(48.dp)
    ) {
        if (amountState is AmountState.InValid) {
            Image(
                painter = painterResource(id = R.drawable.ic_alert_outline),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(id = R.string.request_amount_invalid),
                color = ZashiColors.Utility.WarningYellow.utilityOrange700,
                style = ZashiTypography.textSm,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.wrapContentWidth()
            )
        }
    }
}

@Composable
private fun RequestQrCodeView(
    state: RequestState.QrCode,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        Text(text = "QrCode")
    }
}

@Composable
private fun RequestMemoView(
    state: RequestState.Memo,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        Text(text = "Memo")
    }
}
