package co.electriccoin.zcash.ui.screen.balances.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.test.CommonTag
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.account.AccountTag

@Preview("Balances")
@Composable
private fun ComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Balances(
                goSettings = {},
            )
        }
    }
}

@Composable
fun Balances(
    goSettings: () -> Unit,
) {
    Scaffold(topBar = {
        BalancesTopAppBar(onSettings = goSettings)
    }) { paddingValues ->
        BalancesMainContent(
            modifier =
                Modifier.padding(
                    top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingDefault,
                    bottom = paddingValues.calculateBottomPadding() + ZcashTheme.dimens.spacingHuge,
                    start = ZcashTheme.dimens.screenHorizontalSpacing,
                    end = ZcashTheme.dimens.screenHorizontalSpacing
                )
        )
    }
}

@Composable
private fun BalancesTopAppBar(onSettings: () -> Unit) {
    SmallTopAppBar(
        showTitleLogo = true,
        hamburgerMenuActions = {
            IconButton(
                onClick = onSettings,
                modifier = Modifier.testTag(CommonTag.SETTINGS_TOP_BAR_BUTTON)
            ) {
                Icon(
                    painter = painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.hamburger_menu_icon),
                    contentDescription = stringResource(id = R.string.settings_menu_content_description)
                )
            }
        }
    )
}

@Composable
private fun BalancesMainContent(
    modifier: Modifier = Modifier
) {
    Box(
        Modifier
            .fillMaxSize()
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Body(stringResource(id = R.string.not_implemented_yet))

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))
    }
}
