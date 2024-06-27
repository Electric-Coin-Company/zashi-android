package co.electriccoin.zcash.ui.common.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.CircularSmallProgressIndicator
import co.electriccoin.zcash.ui.design.component.Reference
import co.electriccoin.zcash.ui.design.component.StyledBalance
import co.electriccoin.zcash.ui.design.component.ZecAmountTriple
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

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
                            spendableBalance = Zatoshi(1234567891234567L)
                        ),
                    isReferenceToBalances = true,
                    onReferenceClick = {},
                    modifier = Modifier
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
                balanceState = BalanceState.Loading(Zatoshi(0L)),
                isReferenceToBalances = true,
                onReferenceClick = {},
                modifier = Modifier
            )
        }
    }
}

sealed class BalanceState(open val totalBalance: Zatoshi) {
    data object None : BalanceState(Zatoshi(0L))

    data class Loading(override val totalBalance: Zatoshi) : BalanceState(totalBalance)

    data class Available(override val totalBalance: Zatoshi, val spendableBalance: Zatoshi) : BalanceState(totalBalance)
}

@Composable
@Suppress("LongMethod")
fun BalanceWidget(
    balanceState: BalanceState,
    isReferenceToBalances: Boolean,
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
        BalanceWidgetBigLineOnly(parts = balanceState.totalBalance.toZecStringFull().asZecAmountTriple())

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.animateContentSize()
        ) {
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
                BalanceState.None, is BalanceState.Loading -> {
                    CircularSmallProgressIndicator(color = ZcashTheme.colors.circularProgressBarSmallDark)
                }
                is BalanceState.Available -> {
                    StyledBalance(
                        balanceParts = balanceState.spendableBalance.toZecStringFull().asZecAmountTriple(),
                        textStyles =
                            Pair(
                                ZcashTheme.extendedTypography.balanceWidgetStyles.third,
                                ZcashTheme.extendedTypography.balanceWidgetStyles.fourth
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StyledBalance(
            balanceParts = parts,
            textStyles =
                Pair(
                    ZcashTheme.extendedTypography.balanceWidgetStyles.first,
                    ZcashTheme.extendedTypography.balanceWidgetStyles.second
                )
        )

        Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingSmall))

        Image(
            painter = painterResource(id = R.drawable.ic_zcash_zec_icon),
            contentDescription = null,
        )
    }
}
