@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.FiatCurrencyConversion
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.toFiatString
import co.electriccoin.zcash.ui.common.extension.toKotlinLocale
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.ObserveFiatCurrencyResultFixture
import co.electriccoin.zcash.ui.util.PreviewScreens
import co.electriccoin.zcash.ui.util.StringResource
import co.electriccoin.zcash.ui.util.getValue
import co.electriccoin.zcash.ui.util.stringRes
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.datetime.Clock

@Suppress("LongParameterList", "ComplexCondition")
@Composable
fun StyledExchangeBalance(
    zatoshi: Zatoshi,
    state: ExchangeRateState,
    modifier: Modifier = Modifier,
    isHideBalances: Boolean = false,
    hiddenBalancePlaceholder: StringResource =
        stringRes(co.electriccoin.zcash.ui.design.R.string.hide_balance_placeholder),
    textColor: Color = ZcashTheme.colors.textPrimary,
    style: TextStyle = ZcashTheme.typography.primary.titleSmall.copy(fontWeight = FontWeight.SemiBold)
) {
    if ((state.isStale && !state.isLoading) ||
        (!state.isLoading && state.currencyConversion == null)
    ) {
        ExchangeRateUnavailableButton(
            textColor = textColor,
            style = style,
            modifier = modifier
        )
    } else {
        ExchangeAvailableRateButton(
            style = style,
            textColor = textColor,
            zatoshi = zatoshi,
            isHideBalances = isHideBalances,
            state = state,
            hiddenBalancePlaceholder = hiddenBalancePlaceholder
        )
    }
}

@Suppress("LongParameterList", "LongMethod")
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ExchangeAvailableRateButton(
    style: TextStyle,
    textColor: Color,
    zatoshi: Zatoshi,
    isHideBalances: Boolean,
    state: ExchangeRateState,
    hiddenBalancePlaceholder: StringResource,
    modifier: Modifier = Modifier,
) {
    val currencySymbol = state.fiatCurrency.symbol
    val text =
        if (isHideBalances) {
            "${currencySymbol}${hiddenBalancePlaceholder.getValue()}"
        } else if (state.currencyConversion != null) {
            val value =
                zatoshi.toFiatString(
                    currencyConversion = state.currencyConversion,
                    locale = Locale.current.toKotlinLocale(),
                    monetarySeparators = MonetarySeparators.current(java.util.Locale.getDefault()),
                    includeSymbols = false
                )

            "$currencySymbol$value"
        } else {
            currencySymbol
        }

    val isEnabled = !state.isLoading && state.isRefreshEnabled

    ExchangeRateButton(
        modifier =
            modifier
                .basicMarquee()
                .animateContentSize(),
        onClick = state.onRefresh,
        isEnabled = isEnabled,
        textColor = textColor,
    ) {
        Text(
            text = text,
            style = style,
            maxLines = 1,
            color = textColor
        )

        Spacer(modifier = Modifier.width(12.dp))

        if (state.isLoading) {
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(
                    if (isSystemInDarkTheme()) R.raw.exchange_rate_loading_white else R.raw.exchange_rate_loading
                )
            )
            val progress by animateLottieCompositionAsState(
                iterations = LottieConstants.IterateForever,
                composition = composition
            )
            LottieAnimation(
                modifier =
                    Modifier
                        .align(CenterVertically)
                        .size(16.dp),
                composition = composition,
                progress = { progress },
                maintainOriginalImageBounds = true
            )
        } else {
            Image(
                modifier =
                    Modifier
                        .align(CenterVertically)
                        .size(16.dp),
                painter = painterResource(R.drawable.ic_exchange_rate_retry),
                contentDescription = "",
                colorFilter =
                    ColorFilter.tint(
                        if (state.isRefreshEnabled) {
                            textColor
                        } else {
                            ZcashTheme.colors.textDisabled
                        }
                    )
            )
        }
    }
}

@Composable
private fun ExchangeRateUnavailableButton(
    textColor: Color,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    val transitionState = remember { MutableTransitionState(false) }

    ExchangeRateButton(
        modifier = modifier,
        onClick = {
            if (transitionState.targetState && transitionState.currentState) {
                transitionState.targetState = false
            } else if (!transitionState.targetState && !transitionState.currentState) {
                transitionState.targetState = true
            }
        },
        isEnabled = true,
        enableBorder = false,
        textColor = textColor,
    ) {
        Text(
            text = stringResource(id = R.string.balances_exchange_rate_unavailable),
            style = style,
            maxLines = 1,
            color = textColor
        )

        Spacer(modifier = Modifier.width(10.dp))

        Image(
            modifier =
                Modifier
                    .align(CenterVertically),
            painter = painterResource(R.drawable.ic_unavailable_exchange_rate),
            contentDescription = "",
            colorFilter = ColorFilter.tint(textColor)
        )
    }

    if (transitionState.currentState || transitionState.targetState || !transitionState.isIdle) {
        val offset = with(LocalDensity.current) { 64.dp.toPx() }.toInt()
        UnavailableExchangeRatePopup(
            onDismissRequest = {
                transitionState.targetState = false
            },
            transitionState = transitionState,
            offset = IntOffset(0, offset)
        )
    }
}

@Suppress("LongParameterList")
@Composable
private fun ExchangeRateButton(
    isEnabled: Boolean,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enableBorder: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    ElevatedButton(
        modifier =
            modifier
                .animateContentSize(),
        onClick = onClick,
        enabled = isEnabled,
        shape = RoundedCornerShape(6.dp),
        elevation =
            ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 0.dp,
                disabledElevation = 0.dp
            ),
        colors =
            ButtonDefaults.elevatedButtonColors(
                disabledContainerColor = Color.Transparent,
                disabledContentColor = textColor,
                contentColor = textColor
            ),
        border = if (isEnabled && enableBorder) BorderStroke(1.dp, Color.Gray) else null,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
        content = content
    )
}

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun DefaultPreview() =
    ZcashTheme {
        BlankSurface {
            Column {
                StyledExchangeBalance(
                    isHideBalances = false,
                    modifier = Modifier,
                    zatoshi = Zatoshi(1),
                    state =
                        ExchangeRateState(
                            isLoading = false,
                            currencyConversion =
                                FiatCurrencyConversion(
                                    timestamp = Clock.System.now(),
                                    priceOfZec = 25.0
                                ),
                            onRefresh = {}
                        )
                )
            }
        }
    }

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun DefaultNoRefreshPreview() =
    ZcashTheme {
        BlankSurface {
            Column {
                StyledExchangeBalance(
                    isHideBalances = false,
                    modifier = Modifier,
                    zatoshi = Zatoshi(1),
                    state =
                        ExchangeRateState(
                            isLoading = false,
                            currencyConversion =
                                FiatCurrencyConversion(
                                    timestamp = Clock.System.now(),
                                    priceOfZec = 25.0
                                ),
                            isRefreshEnabled = false,
                            onRefresh = {}
                        )
                )
            }
        }
    }

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun HiddenPreview() =
    ZcashTheme {
        BlankSurface {
            Column {
                StyledExchangeBalance(
                    isHideBalances = true,
                    modifier = Modifier,
                    zatoshi = Zatoshi(1),
                    state =
                        ExchangeRateState(
                            isLoading = false,
                            currencyConversion =
                                FiatCurrencyConversion(
                                    timestamp = Clock.System.now(),
                                    priceOfZec = 25.0
                                ),
                            onRefresh = {}
                        )
                )
            }
        }
    }

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun HiddenStalePreview() =
    ZcashTheme {
        BlankSurface {
            Column {
                StyledExchangeBalance(
                    isHideBalances = true,
                    modifier = Modifier,
                    zatoshi = Zatoshi(1),
                    state =
                        ExchangeRateState(
                            isLoading = false,
                            isStale = true,
                            currencyConversion =
                                FiatCurrencyConversion(
                                    timestamp = Clock.System.now(),
                                    priceOfZec = 25.0
                                ),
                            onRefresh = {}
                        )
                )
            }
        }
    }

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun LoadingPreview() =
    ZcashTheme {
        BlankSurface {
            Column {
                StyledExchangeBalance(
                    isHideBalances = false,
                    modifier = Modifier,
                    zatoshi = Zatoshi(1),
                    state = ObserveFiatCurrencyResultFixture.new()
                )
            }
        }
    }

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun LoadingEmptyPreview() =
    ZcashTheme {
        BlankSurface {
            Column {
                StyledExchangeBalance(
                    isHideBalances = false,
                    modifier = Modifier,
                    zatoshi = Zatoshi(1),
                    state = ObserveFiatCurrencyResultFixture.new(currencyConversion = null)
                )
            }
        }
    }

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun LoadingStalePreview() =
    ZcashTheme {
        BlankSurface {
            Column {
                StyledExchangeBalance(
                    isHideBalances = false,
                    modifier = Modifier,
                    zatoshi = Zatoshi(1),
                    state =
                        ObserveFiatCurrencyResultFixture.new(
                            isStale = true,
                            isLoading = true
                        )
                )
            }
        }
    }

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun StalePreview() =
    ZcashTheme {
        BlankSurface {
            Column {
                StyledExchangeBalance(
                    isHideBalances = false,
                    modifier = Modifier,
                    zatoshi = Zatoshi(1),
                    state =
                        ObserveFiatCurrencyResultFixture.new(
                            isLoading = false,
                            isStale = true,
                        )
                )
            }
        }
    }
