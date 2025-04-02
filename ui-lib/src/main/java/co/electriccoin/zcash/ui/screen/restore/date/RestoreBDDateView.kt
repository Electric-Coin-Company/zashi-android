@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.restore.date

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarTags
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiIconButton
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.component.ZashiYearMonthWheelDatePicker
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.orDark
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun RestoreBDDateView(state: RestoreBDDateState) {
    BlankBgScaffold(
        topBar = { AppBar(state) },
        bottomBar = {},
        content = { padding ->
            Content(
                state = state,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .scaffoldPadding(padding)
            )
        }
    )
}

@Composable
private fun Content(
    state: RestoreBDDateState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.restore_bd_date_subtitle),
            style = ZashiTypography.header6,
            color = ZashiColors.Text.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.restore_bd_date_message),
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textPrimary
        )
        Spacer(Modifier.height(24.dp))

        ZashiYearMonthWheelDatePicker(
            onSelectionChange = {},
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(24.dp))

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painterResource(R.drawable.ic_info),
                contentDescription = "",
                colorFilter = ColorFilter.tint(color = ZashiColors.Utility.Indigo.utilityIndigo700)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                modifier = Modifier.padding(top = 2.dp),
                text = stringResource(R.string.restore_bd_date_note),
                style = ZashiTypography.textXs,
                fontWeight = FontWeight.Medium,
                color = ZashiColors.Utility.Indigo.utilityIndigo700
            )
        }

        Spacer(Modifier.height(24.dp))

        ZashiButton(
            state.next,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun AppBar(state: RestoreBDDateState) {
    ZashiSmallTopAppBar(
        title = stringResource(R.string.restore_title),
        navigationAction = {
            ZashiTopAppBarBackNavigation(
                onBack = state.onBack,
                modifier = Modifier.testTag(ZashiTopAppBarTags.BACK)
            )
        },
        regularActions = {
            ZashiIconButton(state.dialogButton, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(20.dp))
        },
        colors =
            ZcashTheme.colors.topAppBarColors orDark
                ZcashTheme.colors.topAppBarColors.copyColors(
                    containerColor = Color.Transparent
                ),
    )
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        RestoreBDDateView(
            state =
                RestoreBDDateState(
                    next = ButtonState(stringRes("Estimate")) {},
                    dialogButton = IconButtonState(R.drawable.ic_restore_dialog) {},
                    onBack = {}
                )
        )
    }
