package co.electriccoin.zcash.ui.screen.swap.amount

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.AssetCardState
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.NumberTextFieldState
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiAssetCard
import co.electriccoin.zcash.ui.design.component.ZashiNumberTextField
import co.electriccoin.zcash.ui.design.component.ZashiNumberTextFieldDefaults
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber

@Composable
fun SwapTextField(state: SwapTextFieldState, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        SwapTextFieldCard(
            modifier = Modifier.fillMaxWidth(),
            state = state
        )

        if (state.isError) {
            val error = state.error ?: state.textField.errorString

            if (error.getValue().isNotEmpty()) {
                Spacer(8.dp)
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = error.getValue(),
                    style = ZashiTypography.textSm,
                    color = ZashiColors.Inputs.ErrorDefault.hint,
                    textAlign = TextAlign.End
                )
                Spacer(8.dp)
            }
        }
    }
}

@Composable
private fun SwapTextFieldCard(
    state: SwapTextFieldState,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = ZashiColors.Surfaces.bgPrimary
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = state.title.getValue(),
                    style = ZashiTypography.textSm,
                    fontWeight = FontWeight.Medium,
                    color = ZashiColors.Text.textPrimary
                )
                if (state.max != null) {
                    Spacer(1f)
                    Text(
                        text = state.max.getValue(),
                        style = ZashiTypography.textSm,
                        fontWeight = FontWeight.Medium,
                        color = ZashiColors.Text.textTertiary
                    )
                }
            }

            Spacer(8.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(.45f)
                ) {
                    ZashiAssetCard(
                        state = state.token
                    )
                }
                ZashiNumberTextField(
                    state = state.textField,
                    modifier = Modifier.weight(.55f),
                    textStyle = ZashiTypography.header4.copy(
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.End
                    ),
                    contentPadding = if (state.textFieldPrefix == null) {
                        PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    } else {
                        PaddingValues(start = 8.dp, top = 4.dp, end = 12.dp, bottom = 4.dp)
                    },
                    placeholder = {
                        ZashiNumberTextFieldDefaults.Placeholder(
                            modifier = Modifier.fillMaxWidth(),
                            style = ZashiTypography.header4,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.End
                        )
                    },
                    leadingIcon = if (state.textFieldPrefix is ImageResource.ByDrawable) {
                        {
                            Image(
                                painter = painterResource(state.textFieldPrefix.resource),
                                contentDescription = null
                            )
                        }
                    } else {
                        null
                    },
                )
            }
            Spacer(8.dp)
            Row(
                verticalAlignment = CenterVertically
            ) {
                Spacer(1f)
                Text(
                    text = state.secondaryText.getValue(),
                    style = ZashiTypography.textSm,
                    fontWeight = FontWeight.Medium,
                    color = ZashiColors.Text.textTertiary
                )
                Spacer(4.dp)
                Image(
                    modifier = Modifier.clickable(onClick = state.onSwapChange),
                    painter = painterResource(R.drawable.ic_swap_recipient),
                    contentDescription = null
                )
            }
        }
    }
}

@Immutable
data class SwapTextFieldState(
    val title: StringResource,
    val max: StringResource?,
    val error: StringResource?,
    val token: AssetCardState,
    val textFieldPrefix: ImageResource?,
    val textField: NumberTextFieldState,
    val secondaryText: StringResource,
    val onSwapChange: () -> Unit
) {
    val isError = error != null || textField.isError
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            SwapTextField(
                state =
                    SwapTextFieldState(
                        title = stringRes("From"),
                        error = null,
                        token = AssetCardState(
                            ticker = stringRes("USDT"),
                            token = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_token_zec),
                            chain = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_chain_zec),
                            onClick = null
                        ),
                        textFieldPrefix = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_zec_symbol),
                        textField = NumberTextFieldState {},
                        secondaryText = stringResByDynamicCurrencyNumber(100, "USDT"),
                        max = stringResByDynamicCurrencyNumber(1000, "$"),
                        onSwapChange = {},
                    )
            )
        }
    }

@PreviewScreens
@Composable
private fun ErrorPreview() =
    ZcashTheme {
        BlankSurface {
            SwapTextField(
                state =
                    SwapTextFieldState(
                        title = stringRes("Recipient gets"),
                        error = stringRes("Error"),
                        token = AssetCardState(
                            ticker = stringRes("USDT"),
                            token = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_token_zec),
                            chain = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_chain_zec),
                            onClick = null
                        ),
                        textFieldPrefix = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_zec_symbol),
                        textField = NumberTextFieldState {},
                        secondaryText = stringResByDynamicCurrencyNumber(100, "USDT"),
                        max = stringResByDynamicCurrencyNumber(100, "$"),
                        onSwapChange = {},
                    )
            )
        }
    }
