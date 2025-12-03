package co.electriccoin.zcash.ui.screen.exchangerate.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ZashiBaseSettingsOptIn
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.exchangerate.SecondaryCard

@Composable
internal fun ExchangeRateSettingsView(state: ExchangeRateSettingsState) {
    ZashiBaseSettingsOptIn(
        header = stringResource(id = R.string.exchange_rate_opt_in_subtitle),
        image = R.drawable.exchange_rate,
        onDismiss = state.onBack,
        info = state.info?.getValue(),
        content = {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.exchange_rate_opt_in_description_settings),
                style = ZashiTypography.textSm,
                color = ZashiColors.Text.textTertiary,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Option(
                modifier = Modifier.fillMaxWidth(),
                image = R.drawable.ic_opt_in,
                isChecked = state.isOptedIn.isChecked,
                title = stringResource(R.string.exchange_rate_opt_in_option_title),
                subtitle = stringResource(R.string.exchange_rate_opt_in_option_subtitle),
                onClick = state.isOptedIn.onClick
            )
            Spacer(modifier = Modifier.height(12.dp))
            Option(
                modifier = Modifier.fillMaxWidth(),
                image = R.drawable.ic_opt_out,
                isChecked = state.isOptedOut.isChecked,
                title = stringResource(R.string.exchange_rate_opt_out_option_title),
                subtitle = stringResource(R.string.exchange_rate_opt_out_option_subtitle),
                onClick = state.isOptedOut.onClick
            )
        },
        footer = {
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                state = state.saveButton,
                defaultPrimaryColors = ZashiButtonDefaults.primaryColors(),
            )
        },
    )
}

@Suppress("LongParameterList")
@Composable
fun Option(
    @DrawableRes image: Int,
    isChecked: Boolean,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val onClick =
        remember(isChecked, onClick) {
            if (isChecked) {
                onClick
            } else {
                {
                    haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    onClick()
                }
            }
        }

    SecondaryCard(
        modifier =
            modifier.clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            )
    ) {
        Row(
            Modifier.padding(20.dp)
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = ZashiTypography.textSm,
                    color = ZashiColors.Text.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = ZashiTypography.textSm,
                    color = ZashiColors.Text.textTertiary,
                )
            }
            Image(
                painter =
                    painterResource(
                        if (isChecked) R.drawable.ic_checkbox_checked else R.drawable.ic_checkbox_unchecked
                    ),
                contentDescription = null
            )
        }
    }
}

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun SettingsExchangeRateOptInPreview() =
    ZcashTheme {
        BlankSurface {
            ExchangeRateSettingsView(
                state =
                    ExchangeRateSettingsState(
                        isOptedIn =
                            SimpleCheckboxState(
                                isChecked = true,
                                onClick = {}
                            ),
                        isOptedOut =
                            SimpleCheckboxState(
                                isChecked = false,
                                onClick = {}
                            ),
                        saveButton =
                            ButtonState(
                                text = stringRes(R.string.exchange_rate_opt_in_save),
                                onClick = {}
                            ),
                        onBack = {},
                        info = stringRes(R.string.exchange_rate_tor_enabled_footer)
                    )
            )
        }
    }
