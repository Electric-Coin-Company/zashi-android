package co.electriccoin.zcash.ui.screen.balances

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.extension.toZecStringFull
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.extension.asZecAmountTriple
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.StyledBalance
import co.electriccoin.zcash.ui.design.component.StyledBalanceDefaults
import co.electriccoin.zcash.ui.design.component.ZecAmountTriple
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.balances.LocalBalancesAvailable
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.fixture.ObserveFiatCurrencyResultFixture
import co.electriccoin.zcash.ui.screen.balances.BalanceTag.BALANCE_VIEWS
import co.electriccoin.zcash.ui.screen.exchangerate.widget.StyledExchangeBalance

@Composable
fun BalanceWidget(state: BalanceWidgetState, modifier: Modifier = Modifier) {
    Column(
        modifier =
            Modifier
                .wrapContentSize()
                .then(modifier)
                .testTag(BALANCE_VIEWS),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BalanceWidgetHeader(
            parts = state.totalBalance.toZecStringFull().asZecAmountTriple(),
            showDust = state.showDust
        )

        state.button?.let {
            Spacer(12.dp)
            BalanceWidgetButton(it)
        }

        state.exchangeRate?.let {
            if (state.exchangeRate is ExchangeRateState.Data) {
                Spacer(12.dp)
            }
            StyledExchangeBalance(state = it, zatoshi = state.totalBalance)
        }
    }
}

@Composable
fun BalanceWidgetHeader(
    parts: ZecAmountTriple,
    modifier: Modifier = Modifier,
    isHideBalances: Boolean = LocalBalancesAvailable.current.not(),
    showDust: Boolean = true,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_balance_zec),
            contentDescription = null,
            colorFilter = ColorFilter.tint(ZashiColors.Text.textPrimary)
        )
        Spacer(6.dp)
        StyledBalance(
            showDust = showDust,
            balanceParts = parts,
            isHideBalances = isHideBalances,
            textStyle =
                StyledBalanceDefaults.textStyles(
                    mostSignificantPart = ZashiTypography.header2.copy(fontWeight = FontWeight.SemiBold),
                    leastSignificantPart = ZashiTypography.textXs.copy(fontWeight = FontWeight.SemiBold),
                )
        )
    }
}

@PreviewScreens
@Composable
private fun BalanceWidgetPreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface(
            modifier = Modifier.fillMaxWidth()
        ) {
            BalanceWidget(
                state =
                    BalanceWidgetState(
                        totalBalance = Zatoshi(1234567891234567L),
                        button = BalanceButtonState(
                            icon = R.drawable.ic_help,
                            text = stringRes("text"),
                            amount = Zatoshi(1000),
                            onClick = {}
                        ),
                        exchangeRate = ObserveFiatCurrencyResultFixture.new(),
                        showDust = true
                    ),
                modifier = Modifier,
            )
        }
    }
}
