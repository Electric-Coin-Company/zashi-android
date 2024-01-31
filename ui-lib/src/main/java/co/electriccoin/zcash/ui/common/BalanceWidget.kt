package co.electriccoin.zcash.ui.common

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
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.WalletBalance
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.toZecString
import cash.z.ecc.sdk.type.ZcashCurrency
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.model.spendableBalance
import co.electriccoin.zcash.ui.common.model.totalBalance
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Reference
import co.electriccoin.zcash.ui.design.component.StyledBalance
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import java.util.Locale

@Preview(device = Devices.PIXEL_2)
@Composable
private fun BalanceWidgetPreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface(
            modifier = Modifier.fillMaxWidth()
        ) {
            @Suppress("MagicNumber")
            BalanceWidget(
                walletSnapshot =
                    WalletSnapshotFixture.new(
                        saplingBalance =
                            WalletBalance(
                                Zatoshi(1234567891234567),
                                Zatoshi(123456789)
                            )
                    ),
                isReferenceToBalances = true,
                onReferenceClick = {},
                modifier = Modifier
            )
        }
    }
}

fun splitBalance(balance: String): Pair<String, String> {
    Twig.debug { "Balance before split: $balance" }

    @Suppress("MAGIC_CONSTANT", "MagicNumber")
    val cutPosition = balance.indexOf(MonetarySeparators.current(Locale.US).decimal) + 4
    val firstPart =
        balance.substring(
            startIndex = 0,
            endIndex = cutPosition
        )
    val secondPart =
        balance.substring(
            startIndex = cutPosition
        )

    Twig.debug { "Balance after split: $firstPart|$secondPart" }

    return Pair(firstPart, secondPart)
}

@Composable
@Suppress("LongMethod")
fun BalanceWidget(
    walletSnapshot: WalletSnapshot,
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
        val totalBalanceSplit = splitBalance(walletSnapshot.totalBalance().toZecString())

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            StyledBalance(
                mainPart = totalBalanceSplit.first,
                secondPart = totalBalanceSplit.second,
                textStyles =
                    Pair(
                        ZcashTheme.extendedTypography.balanceStyles.first,
                        ZcashTheme.extendedTypography.balanceStyles.second
                    )
            )

            Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingSmall))

            Image(
                painter = painterResource(id = R.drawable.ic_zcash_zec_icon),
                contentDescription = null,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isReferenceToBalances) {
                Reference(
                    text = stringResource(id = co.electriccoin.zcash.ui.R.string.balance_widget_available),
                    onClick = onReferenceClick,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(all = ZcashTheme.dimens.spacingTiny)
                )
            } else {
                Body(
                    text = stringResource(id = co.electriccoin.zcash.ui.R.string.balance_widget_available),
                )
            }

            Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingTiny))

            val availableBalanceSplit = splitBalance(walletSnapshot.spendableBalance().toZecString())

            StyledBalance(
                mainPart = availableBalanceSplit.first,
                secondPart = availableBalanceSplit.second,
                textStyles =
                    Pair(
                        ZcashTheme.extendedTypography.balanceStyles.third,
                        ZcashTheme.extendedTypography.balanceStyles.fourth
                    )
            )

            Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingTiny))

            Body(
                text = ZcashCurrency.getLocalizedName(LocalContext.current),
                textFontWeight = FontWeight.Bold
            )
        }
    }
}
