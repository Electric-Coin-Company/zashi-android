package co.electriccoin.zcash.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.model.FiatCurrency
import cash.z.ecc.android.sdk.model.FiatCurrencyConversion
import cash.z.ecc.android.sdk.model.FiatCurrencyConversionRateState
import cash.z.ecc.android.sdk.model.FiatCurrencyResult
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.toFiatCurrencyState
import co.electriccoin.zcash.ui.common.extension.toKotlinLocale
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.CircularSmallProgressIndicator
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.util.StringResource
import co.electriccoin.zcash.ui.util.getValue
import co.electriccoin.zcash.ui.util.stringRes
import kotlinx.datetime.Clock

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StyledExchangeBalance(
    zatoshi: Zatoshi,
    exchangeRate: FiatCurrencyResult,
    modifier: Modifier = Modifier,
    isHideBalances: Boolean = false,
    hiddenBalancePlaceholder: StringResource = stringRes(R.string.hide_balance_placeholder),
    textColor: Color = Color.Unspecified,
    style: TextStyle = ZcashTheme.typography.primary.titleSmall
) {
    val currencySymbol = exchangeRate.fiatCurrency.symbol

    Row(
        modifier =
            modifier
                .basicMarquee()
                .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when {
            exchangeRate is FiatCurrencyResult.Error -> {
                // empty view
            }
            exchangeRate is FiatCurrencyResult.Loading && !isHideBalances -> {
                StyledExchangeText(
                    text = currencySymbol,
                    textColor = textColor,
                    style = style
                )
                CircularSmallProgressIndicator()
            }
            else -> {
                StyledExchangeText(
                    text =
                        if (isHideBalances) {
                            "${currencySymbol}${hiddenBalancePlaceholder.getValue()}"
                        } else {
                            when (
                                val state =
                                    zatoshi.toFiatCurrencyState(
                                        fiatCurrencyResult = exchangeRate,
                                        locale = Locale.current.toKotlinLocale(),
                                        monetarySeparators = MonetarySeparators.current(java.util.Locale.getDefault())
                                    )
                            ) {
                                is FiatCurrencyConversionRateState.Current -> state.formattedFiatValue
                                is FiatCurrencyConversionRateState.Stale -> state.formattedFiatValue
                                FiatCurrencyConversionRateState.Unavailable ->
                                    stringResource(co.electriccoin.zcash.ui.R.string.fiat_currency_conversion_rate_unavailable)
                            }
                        },
                    textColor = textColor,
                    style = style
                )
            }
        }
    }
}

@Composable
private fun StyledExchangeText(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color,
    style: TextStyle
) {
    Text(
        text = text,
        color = textColor,
        style = style,
        maxLines = 1,
        modifier = modifier
    )
}

@Composable
private fun StyledExchangeBalancePreview() =
    BlankSurface {
        Column {
            StyledExchangeBalance(
                isHideBalances = false,
                modifier = Modifier,
                zatoshi = Zatoshi(1),
                exchangeRate =
                    FiatCurrencyResult.Success(
                        FiatCurrencyConversion(
                            fiatCurrency = FiatCurrency.USD,
                            timestamp = Clock.System.now(),
                            priceOfZec = 25.0
                        )
                    )
            )
        }
    }

@Composable
private fun HiddenStyledExchangeBalancePreview() =
    BlankSurface {
        Column {
            StyledExchangeBalance(
                isHideBalances = true,
                modifier = Modifier,
                zatoshi = Zatoshi(1),
                exchangeRate =
                    FiatCurrencyResult.Success(
                        FiatCurrencyConversion(
                            fiatCurrency = FiatCurrency.USD,
                            timestamp = Clock.System.now(),
                            priceOfZec = 25.0
                        )
                    )
            )
        }
    }

@Composable
private fun LoadingStyledExchangeBalancePreview() =
    BlankSurface {
        Column {
            StyledExchangeBalance(
                isHideBalances = true,
                modifier = Modifier,
                zatoshi = Zatoshi(1),
                exchangeRate = FiatCurrencyResult.Loading()
            )
        }
    }

@Preview
@Composable
private fun StyledExchangeBalancePreviewLight() =
    ZcashTheme(forceDarkMode = false) {
        StyledExchangeBalancePreview()
    }

@Preview
@Composable
private fun HiddenStyledExchangeBalancePreviewLight() =
    ZcashTheme(forceDarkMode = false) {
        HiddenStyledExchangeBalancePreview()
    }

@Preview
@Composable
private fun LoadingStyledExchangeBalancePreviewLight() =
    ZcashTheme(forceDarkMode = false) {
        LoadingStyledExchangeBalancePreview()
    }

@Preview
@Composable
private fun StyledExchangeBalancePreviewDark() =
    ZcashTheme(forceDarkMode = true) {
        StyledExchangeBalancePreview()
    }

@Preview
@Composable
private fun HiddenStyledExchangeBalancePreviewDark() =
    ZcashTheme(forceDarkMode = true) {
        HiddenStyledExchangeBalancePreview()
    }

@Preview
@Composable
private fun LoadingStyledExchangeBalancePreviewDark() =
    ZcashTheme(forceDarkMode = true) {
        LoadingStyledExchangeBalancePreview()
    }
