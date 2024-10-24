package co.electriccoin.zcash.ui.common.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.extension.toZecStringFull
import cash.z.ecc.sdk.type.ZcashCurrency
import co.electriccoin.zcash.ui.common.extension.asZecAmountTriple
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.LottieProgress
import co.electriccoin.zcash.ui.design.component.Reference
import co.electriccoin.zcash.ui.design.component.StyledBalance
import co.electriccoin.zcash.ui.design.component.StyledBalanceDefaults
import co.electriccoin.zcash.ui.design.component.ZecAmountTriple
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.ObserveFiatCurrencyResultFixture
import co.electriccoin.zcash.ui.screen.exchangerate.widget.StyledExchangeBalance

@Preview(device = Devices.PIXEL_2)
@Composable
private fun BalanceWidgetPreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface(
            modifier = Modifier.fillMaxWidth()
        ) {
            @Suppress("MagicNumber")
            (
                BalanceWidget(
                    balanceState =
                        BalanceState.Available(
                            totalBalance = Zatoshi(1234567891234567L),
                            spendableBalance = Zatoshi(1234567891234567L),
                            exchangeRate = ObserveFiatCurrencyResultFixture.new()
                        ),
                    isHideBalances = false,
                    isReferenceToBalances = true,
                    onReferenceClick = {},
                    modifier = Modifier,
                )
            )
        }
    }
}

@Preview(device = Devices.PIXEL_2)
@Composable
private fun BalanceWidgetNotAvailableYetPreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface(
            modifier = Modifier.fillMaxWidth()
        ) {
            @Suppress("MagicNumber")
            BalanceWidget(
                balanceState =
                    BalanceState.Loading(
                        totalBalance = Zatoshi(value = 0L),
                        spendableBalance = Zatoshi(value = 0L),
                        exchangeRate = ObserveFiatCurrencyResultFixture.new()
                    ),
                isHideBalances = false,
                isReferenceToBalances = true,
                onReferenceClick = {},
                modifier = Modifier,
            )
        }
    }
}

@Preview
@Composable
private fun BalanceWidgetHiddenAmountPreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface(
            modifier = Modifier.fillMaxWidth()
        ) {
            @Suppress("MagicNumber")
            BalanceWidget(
                balanceState =
                    BalanceState.Loading(
                        totalBalance = Zatoshi(0L),
                        spendableBalance = Zatoshi(0L),
                        exchangeRate = ObserveFiatCurrencyResultFixture.new()
                    ),
                isHideBalances = true,
                isReferenceToBalances = true,
                onReferenceClick = {},
                modifier = Modifier,
            )
        }
    }
}

sealed interface BalanceState {
    val totalBalance: Zatoshi
    val spendableBalance: Zatoshi
    val exchangeRate: ExchangeRateState

    data class None(
        override val exchangeRate: ExchangeRateState
    ) : BalanceState {
        override val totalBalance: Zatoshi = Zatoshi(0L)
        override val spendableBalance: Zatoshi = Zatoshi(0L)
    }

    data class Loading(
        override val totalBalance: Zatoshi,
        override val spendableBalance: Zatoshi,
        override val exchangeRate: ExchangeRateState
    ) : BalanceState

    data class Available(
        override val totalBalance: Zatoshi,
        override val spendableBalance: Zatoshi,
        override val exchangeRate: ExchangeRateState
    ) : BalanceState
}

@Composable
@Suppress("LongMethod")
fun BalanceWidget(
    balanceState: BalanceState,
    isReferenceToBalances: Boolean,
    isHideBalances: Boolean,
    onReferenceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier =
            Modifier
                .wrapContentSize()
                .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BalanceWidgetBigLineOnly(
            isHideBalances = isHideBalances,
            parts = balanceState.totalBalance.toZecStringFull().asZecAmountTriple()
        )

        if (balanceState.exchangeRate is ExchangeRateState.Data) {
            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingMid))
        }

        StyledExchangeBalance(
            zatoshi = balanceState.totalBalance,
            state = balanceState.exchangeRate,
            isHideBalances = isHideBalances
        )

        if (balanceState.exchangeRate is ExchangeRateState.Data) {
            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isReferenceToBalances) {
                Reference(
                    text = stringResource(id = co.electriccoin.zcash.ui.R.string.balance_widget_available),
                    onClick = onReferenceClick,
                    fontWeight = FontWeight.Normal,
                    modifier =
                        Modifier
                            .padding(
                                vertical = ZcashTheme.dimens.spacingSmall,
                                horizontal = ZcashTheme.dimens.spacingMini,
                            )
                )
            } else {
                Body(
                    text = stringResource(id = co.electriccoin.zcash.ui.R.string.balance_widget_available),
                    modifier =
                        Modifier
                            .padding(
                                vertical = ZcashTheme.dimens.spacingSmall,
                                horizontal = ZcashTheme.dimens.spacingMini,
                            )
                )
            }

            Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingTiny))

            when (balanceState) {
                is BalanceState.None, is BalanceState.Loading -> LottieProgress()

                is BalanceState.Available -> {
                    StyledBalance(
                        balanceParts = balanceState.spendableBalance.toZecStringFull().asZecAmountTriple(),
                        isHideBalances = isHideBalances,
                        textStyle =
                            StyledBalanceDefaults.textStyles(
                                mostSignificantPart = ZcashTheme.extendedTypography.balanceWidgetStyles.third,
                                leastSignificantPart = ZcashTheme.extendedTypography.balanceWidgetStyles.fourth
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingMin))

            Body(
                text = ZcashCurrency.getLocalizedName(LocalContext.current),
                textFontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BalanceWidgetBigLineOnly(
    parts: ZecAmountTriple,
    isHideBalances: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StyledBalance(
            balanceParts = parts,
            isHideBalances = isHideBalances,
            textStyle =
                StyledBalanceDefaults.textStyles(
                    mostSignificantPart = ZcashTheme.extendedTypography.balanceWidgetStyles.first,
                    leastSignificantPart = ZcashTheme.extendedTypography.balanceWidgetStyles.second
                )
        )

        Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingSmall))

        Image(
            painter = painterResource(id = R.drawable.ic_zcash_zec_icon),
            contentDescription = null,
        )
    }
}
