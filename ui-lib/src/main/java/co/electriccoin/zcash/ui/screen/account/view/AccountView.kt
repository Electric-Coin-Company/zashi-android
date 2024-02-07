package co.electriccoin.zcash.ui.screen.account.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import cash.z.ecc.android.sdk.Synchronizer
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.BalanceWidget
import co.electriccoin.zcash.ui.common.DisableScreenTimeout
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.test.CommonTag
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.account.AccountTag

@Preview("Account")
@Composable
private fun ComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Account(
                walletSnapshot = WalletSnapshotFixture.new(),
                isKeepScreenOnWhileSyncing = false,
                goHistory = {},
                goBalances = {},
                goSettings = {},
            )
        }
    }
}

@Composable
fun Account(
    walletSnapshot: WalletSnapshot,
    isKeepScreenOnWhileSyncing: Boolean?,
    goBalances: () -> Unit,
    goHistory: () -> Unit,
    goSettings: () -> Unit,
) {
    Scaffold(topBar = {
        AccountTopAppBar(onSettings = goSettings)
    }) { paddingValues ->
        AccountMainContent(
            walletSnapshot = walletSnapshot,
            isKeepScreenOnWhileSyncing = isKeepScreenOnWhileSyncing,
            goHistory = goHistory,
            goBalances = goBalances,
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

@Composable
private fun AccountTopAppBar(onSettings: () -> Unit) {
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
private fun AccountMainContent(
    walletSnapshot: WalletSnapshot,
    isKeepScreenOnWhileSyncing: Boolean?,
    goBalances: () -> Unit,
    goHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        BalancesStatus(walletSnapshot, goBalances)

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        PrimaryButton(onClick = goHistory, text = stringResource(R.string.account_button_history))

        if (isKeepScreenOnWhileSyncing == true && walletSnapshot.status == Synchronizer.Status.SYNCING) {
            DisableScreenTimeout()
        }
    }
}

@Composable
@Suppress("LongMethod")
private fun BalancesStatus(
    walletSnapshot: WalletSnapshot,
    goBalances: () -> Unit
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .testTag(AccountTag.BALANCE_VIEWS),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BalanceWidget(
            walletSnapshot = walletSnapshot,
            isReferenceToBalances = true,
            onReferenceClick = goBalances
        )
    }
}
