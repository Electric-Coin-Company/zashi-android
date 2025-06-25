package co.electriccoin.zcash.ui.screen.swap.pay

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.component.ZashiInScreenModalBottomSheet
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.CurrencySymbolLocation
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByAddress
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaySwapView(state: PaySwapState?) {
    ZashiInScreenModalBottomSheet(
        state = state
    ) { innerState ->
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f, false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Pay now",
                style = ZashiTypography.header6,
                fontWeight = FontWeight.SemiBold,
                color = ZashiColors.Text.textPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(24.dp)
            Box {
                Row {
                    SwapTokenAmount(modifier = Modifier.weight(1f), state = innerState.from)
                    Spacer(8.dp)
                    SwapTokenAmount(modifier = Modifier.weight(1f), state = innerState.to)
                }
                Surface(
                    modifier = Modifier.align(Alignment.Center),
                    shape = CircleShape,
                    color = ZashiColors.Surfaces.bgPrimary,
                    shadowElevation = 2.dp
                ) {
                    Box(
                        Modifier.padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(co.electriccoin.zcash.ui.design.R.drawable.ic_arrow_right),
                            contentDescription = null
                        )
                    }
                }
            }
            Spacer(32.dp)
            innerState.items.forEachIndexed { index, item ->
                if (index != 0) {
                    Spacer(12.dp)
                }
                Item(item)
            }
            Spacer(12.dp)
            ZashiHorizontalDivider()
            Spacer(12.dp)
            Item(
                item = innerState.amount,
                descriptionStyle = ZashiTypography.textSm,
                descriptionFontWeight = FontWeight.Medium,
                descriptionColor = ZashiColors.Text.textPrimary
            )
            Spacer(32.dp)
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                state = innerState.primaryButton
            )
        }
    }
}

@Composable
private fun Item(
    item: PaySwapInfoItem,
    descriptionStyle: TextStyle = ZashiTypography.textSm,
    descriptionFontWeight: FontWeight = FontWeight.Normal,
    descriptionColor: Color = ZashiColors.Text.textTertiary
) {
    Row {
        Text(
            modifier = Modifier.weight(1f),
            text = item.description.getValue(),
            style = descriptionStyle,
            fontWeight = descriptionFontWeight,
            color = descriptionColor
        )
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = item.title.getValue(),
                style = ZashiTypography.textSm,
                fontWeight = FontWeight.Medium,
                color = ZashiColors.Text.textPrimary
            )
            if (item.subtitle != null) {
                Text(
                    text = item.subtitle.getValue(),
                    style = ZashiTypography.textXs,
                    color = ZashiColors.Text.textTertiary
                )
            }
        }
    }
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        PaySwapView(
            state =
                PaySwapState(
                    from =
                        SwapTokenAmountState(
                            bigIcon = imageRes(R.drawable.ic_zec_round_full),
                            smallIcon = imageRes(R.drawable.ic_receive_shield),
                            title = stringResByDynamicCurrencyNumber(2.4214, "", CurrencySymbolLocation.HIDDEN),
                            subtitle = stringResByDynamicCurrencyNumber(21312, "$")
                        ),
                    to =
                        SwapTokenAmountState(
                            bigIcon = imageRes(R.drawable.ic_zec_round_full),
                            smallIcon = imageRes(R.drawable.ic_receive_shield),
                            title = stringResByDynamicCurrencyNumber(2.4214, "", CurrencySymbolLocation.HIDDEN),
                            subtitle = stringResByDynamicCurrencyNumber(21312, "$")
                        ),
                    items =
                        listOf(
                            PaySwapInfoItem(
                                description = stringRes("Pay from"),
                                title = stringRes("Zashi"),
                                subtitle = null
                            ),
                            PaySwapInfoItem(
                                description = stringRes("Pay to"),
                                title = stringResByAddress("Asdwae12easdasd", abbreviated = true),
                                subtitle = null
                            ),
                            PaySwapInfoItem(
                                description = stringRes("ZEC transaction fee"),
                                title = stringRes(Zatoshi(1231234)),
                                subtitle = null
                            ),
                            PaySwapInfoItem(
                                description = stringRes("Max slippage 1%"),
                                title = stringRes(Zatoshi(1231234)),
                                subtitle = stringResByDynamicCurrencyNumber(23, "$")
                            )
                        ),
                    amount =
                        PaySwapInfoItem(
                            description = stringRes("Total amount"),
                            title = stringRes(Zatoshi(123213)),
                            subtitle = stringResByDynamicCurrencyNumber(12312, "$")
                        ),
                    primaryButton =
                        ButtonState(
                            text = stringRes("Confirm"),
                            onClick = {}
                        ),
                    onBack = {}
                )
        )
    }
