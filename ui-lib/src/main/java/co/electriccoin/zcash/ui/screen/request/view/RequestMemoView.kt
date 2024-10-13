package co.electriccoin.zcash.ui.screen.request.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ZashiBadge
import co.electriccoin.zcash.ui.design.component.ZashiBadgeColors
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.screen.request.model.RequestState

@Composable
internal fun RequestMemoView(
    state: RequestState.Memo,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        Image(
            painter = painterResource(id = R.drawable.ic_logo_empty_z),
            colorFilter = ColorFilter.tint(ZashiColors.Surfaces.bgAlt),
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingUpLarge))

        ZashiBadge(
            text = stringResource(id = R.string.request_memo_privacy_level_shielded),
            leadingIconVector = painterResource(id = R.drawable.ic_solid_check),
            colors =
            ZashiBadgeColors(
                border = ZashiColors.Utility.Purple.utilityPurple200,
                text = ZashiColors.Utility.Purple.utilityPurple700,
                container = ZashiColors.Utility.Purple.utilityPurple50,
            )
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        RequestMemoZecAmountView(
            state = state,
            modifier = Modifier.padding(horizontal = ZcashTheme.dimens.screenHorizontalSpacingRegular)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))
    }
}

@Composable
private fun RequestMemoZecAmountView(
    state: RequestState.Memo,
    modifier: Modifier = Modifier
) {
    val zecText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = ZashiColors.Text.textPrimary)) {
            append(state.request.amountState.amount)
        }
        append("\u2009") // Add an extra thin space between the texts
        withStyle(style = SpanStyle(color = ZashiColors.Text.textQuaternary)) {
            append(state.zcashCurrency.localizedName(LocalContext.current))
        }
    }

    AutoSizingText(
        text = zecText,
        style = ZashiTypography.header1.copy(
            fontWeight = FontWeight.SemiBold
        )
    )
}
