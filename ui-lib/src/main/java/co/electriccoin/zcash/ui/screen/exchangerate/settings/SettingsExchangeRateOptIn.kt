package co.electriccoin.zcash.ui.screen.exchangerate.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.exchangerate.BaseExchangeRateOptIn
import co.electriccoin.zcash.ui.screen.exchangerate.SecondaryCard
import co.electriccoin.zcash.ui.screen.exchangerate.ZashiButton
import co.electriccoin.zcash.ui.screen.exchangerate.ZashiButtonDefaults

@Composable
fun SettingsExchangeRateOptIn(
    isOptedIn: Boolean,
    onDismiss: () -> Unit,
    onSaveClick: (Boolean) -> Unit
) {
    var isOptInSelected by remember(isOptedIn) { mutableStateOf(isOptedIn) }

    val isButtonDisabled by remember {
        derivedStateOf {
            (isOptedIn && isOptInSelected) || (!isOptedIn && !isOptInSelected)
        }
    }

    BaseExchangeRateOptIn(
        onDismiss = onDismiss,
        content = {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.exchange_rate_opt_in_description_settings),
                color = ZcashTheme.zashiColors.textTertiary,
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Option(
                modifier = Modifier.fillMaxWidth(),
                image = OptIn,
                selectionImage = if (isOptInSelected) Checked else Unchecked,
                title = stringResource(R.string.exchange_rate_opt_in_option_title),
                subtitle = stringResource(R.string.exchange_rate_opt_in_option_subtitle),
                onClick = { isOptInSelected = true }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Option(
                modifier = Modifier.fillMaxWidth(),
                image = OptOut,
                selectionImage = if (!isOptInSelected) Checked else Unchecked,
                title = stringResource(R.string.exchange_rate_opt_out_option_title),
                subtitle = stringResource(R.string.exchange_rate_opt_out_option_subtitle),
                onClick = { isOptInSelected = false }
            )
        },
        footer = {
            ZashiButton(
                text = stringResource(R.string.exchange_rate_opt_in_save),
                modifier = Modifier.fillMaxWidth(),
                onClick = { onSaveClick(isOptInSelected) },
                enabled = !isButtonDisabled,
                colors = ZashiButtonDefaults.primaryButtonColors()
            )
        }
    )
}

@Suppress("LongParameterList")
@Composable
private fun Option(
    @DrawableRes image: Int,
    @DrawableRes selectionImage: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = ZcashTheme.zashiColors.textPrimary,
                    fontSize = 16.sp,
                    style = ZcashTheme.extendedTypography.restoringTopAppBarStyle,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = ZcashTheme.zashiColors.textTertiary,
                    fontSize = 14.sp,
                )
            }
            Image(
                painter = painterResource(selectionImage),
                contentDescription = ""
            )
        }
    }
}

private val OptIn: Int
    @DrawableRes
    @Composable
    get() =
        if (isSystemInDarkTheme()) {
            R.drawable.ic_opt_in
        } else {
            R.drawable.ic_opt_in_light
        }

private val OptOut: Int
    @DrawableRes
    @Composable
    get() =
        if (isSystemInDarkTheme()) {
            R.drawable.ic_opt_out
        } else {
            R.drawable.ic_opt_out_light
        }

private val Checked: Int
    @DrawableRes
    @Composable
    get() =
        if (isSystemInDarkTheme()) {
            R.drawable.ic_checkbox_checked
        } else {
            R.drawable.ic_checkbox_checked_light
        }

private val Unchecked: Int
    @DrawableRes
    @Composable
    get() =
        if (isSystemInDarkTheme()) {
            R.drawable.ic_checkbox_unchecked
        } else {
            R.drawable.ic_checkbox_unchecked_light
        }

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun SettingsExchangeRateOptInPreview() =
    ZcashTheme {
        BlankSurface {
            SettingsExchangeRateOptIn(isOptedIn = true, onDismiss = {}, onSaveClick = {})
        }
    }
