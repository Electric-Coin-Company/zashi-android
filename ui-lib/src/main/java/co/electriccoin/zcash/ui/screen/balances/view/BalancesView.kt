package co.electriccoin.zcash.ui.screen.balances.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.BalanceWidget
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.test.CommonTag
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.account.model.WalletDisplayValues

@Preview("Balances")
@Composable
private fun ComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Balances(
                walletSnapshot = WalletSnapshotFixture.new(),
                onSettings = {},
            )
        }
    }
}

// TODO [#1127]: Implement Balances screen
// TODO [#1127]: https://github.com/Electric-Coin-Company/zashi-android/issues/1127

@Composable
fun Balances(
    walletSnapshot: WalletSnapshot?,
    onSettings: () -> Unit
) {
    Scaffold(topBar = {
        BalancesTopAppBar(onSettings = onSettings)
    }) { paddingValues ->
        if (null == walletSnapshot) {
            CircularScreenProgressIndicator()
        } else {
            BalancesMainContent(
                walletSnapshot = walletSnapshot,
                modifier =
                    Modifier.padding(
                        top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingDefault,
                        bottom = paddingValues.calculateBottomPadding() + ZcashTheme.dimens.spacingHuge,
                        start = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                        end = ZcashTheme.dimens.screenHorizontalSpacingRegular
                    )
            )
        }
    }
}

@Composable
private fun BalancesTopAppBar(onSettings: () -> Unit) {
    SmallTopAppBar(
        showTitleLogo = false,
        titleText = stringResource(id = R.string.balances_title),
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
    walletSnapshot: WalletSnapshot,
    modifier: Modifier = Modifier
) {
    val walletDisplayValues =
        WalletDisplayValues.getNextValues(
            LocalContext.current,
            walletSnapshot
        )

    Column(
        modifier =
            Modifier
                .fillMaxHeight()
                // .verticalScroll(rememberScrollState()) Uncomment this once the whole screen UI is implemented
                .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        if (walletDisplayValues.zecAmountText.isNotEmpty()) {
            BalanceWidget(
                walletSnapshot = walletSnapshot,
                isReferenceToBalances = false,
                onReferenceClick = {}
            )
            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Body(
                text = stringResource(id = R.string.balances_coming_soon),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))
        }
    }
}
