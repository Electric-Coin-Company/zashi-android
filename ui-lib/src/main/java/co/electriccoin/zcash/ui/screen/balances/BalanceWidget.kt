package co.electriccoin.zcash.ui.screen.balances

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.extension.toZecStringFull
import co.electriccoin.zcash.ui.common.extension.asZecAmountTriple
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.StyledBalance
import co.electriccoin.zcash.ui.design.component.StyledBalanceDefaults
import co.electriccoin.zcash.ui.design.component.ZecAmountTriple
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.balances.LocalBalancesAvailable
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.fixture.ObserveFiatCurrencyResultFixture
import co.electriccoin.zcash.ui.screen.balances.BalanceTag.BALANCE_VIEWS
import co.electriccoin.zcash.ui.screen.exchangerate.widget.StyledExchangeBalance

@Composable
fun BalanceWidget(
    balanceState: BalanceState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier =
            Modifier
                .wrapContentSize()
                .then(modifier)
                .testTag(BALANCE_VIEWS),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BalanceWidgetHeader(
            parts = balanceState.totalBalance.toZecStringFull().asZecAmountTriple()
        )

        if (balanceState.exchangeRate is ExchangeRateState.Data) {
            Spacer(modifier = Modifier.height(12.dp))
        }

        StyledExchangeBalance(
            zatoshi = balanceState.totalBalance,
            state = balanceState.exchangeRate,
        )

        if (balanceState.exchangeRate is ExchangeRateState.Data) {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun BalanceWidgetHeader(
    parts: ZecAmountTriple,
    modifier: Modifier = Modifier,
    isHideBalances: Boolean = LocalBalancesAvailable.current.not(),
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
                    mostSignificantPart = ZashiTypography.header2.copy(fontWeight = FontWeight.SemiBold),
                    leastSignificantPart = ZashiTypography.textXs.copy(fontWeight = FontWeight.SemiBold),
                )
        )

        Spacer(modifier = Modifier.width(4.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_zcash_zec_icon),
            contentDescription = null,
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
                balanceState =
                    BalanceState.Available(
                        totalBalance = Zatoshi(1234567891234567L),
                        spendableBalance = Zatoshi(1234567891234567L),
                        exchangeRate = ObserveFiatCurrencyResultFixture.new()
                    ),
                modifier = Modifier,
            )
        }
    }
}
