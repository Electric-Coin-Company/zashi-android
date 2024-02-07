package co.electriccoin.zcash.ui.screen.balances.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.toZecString
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.BalanceWidget
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.model.changePendingBalance
import co.electriccoin.zcash.ui.common.model.spendableBalance
import co.electriccoin.zcash.ui.common.model.valuePendingBalance
import co.electriccoin.zcash.ui.common.test.CommonTag
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.BodySmall
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.CircularSmallProgressIndicator
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.StyledBalance
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
                .verticalScroll(rememberScrollState())
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
        }

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingHuge))

        Divider(
            color = ZcashTheme.colors.darkDividerColor,
            thickness = ZcashTheme.dimens.divider
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        BalancesOverview(walletSnapshot)

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingHuge))

            Body(
                text = stringResource(id = R.string.balances_coming_soon),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BalancesOverview(walletSnapshot: WalletSnapshot) {
    Column {
        SpendableBalanceRow(walletSnapshot)

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        ChangePendingRow(walletSnapshot)

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        //  aka value pending
        PendingTransactionsRow(walletSnapshot)
    }
}

const val TEXT_PART_WIDTH_RATIO = 0.6f

@Composable
fun SpendableBalanceRow(walletSnapshot: WalletSnapshot) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BodySmall(
            text = stringResource(id = R.string.balances_shielded_spendable).uppercase(),
            modifier = Modifier.fillMaxWidth(TEXT_PART_WIDTH_RATIO)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            StyledBalance(
                balanceString = walletSnapshot.spendableBalance().toZecString(),
                textStyles =
                    Pair(
                        ZcashTheme.extendedTypography.balanceSingleStyles.first,
                        ZcashTheme.extendedTypography.balanceSingleStyles.second
                    ),
                textColor = ZcashTheme.colors.textCommon
            )

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.balance_shield),
                contentDescription = null,
                // The same size as the following progress bars
                modifier = Modifier.width(ZcashTheme.dimens.circularSmallProgressWidth)
            )
        }
    }
}

@Composable
fun ChangePendingRow(walletSnapshot: WalletSnapshot) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BodySmall(
            text = stringResource(id = R.string.balances_change_pending).uppercase(),
            modifier = Modifier.fillMaxWidth(TEXT_PART_WIDTH_RATIO)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            val changePendingHasValue = walletSnapshot.changePendingBalance().value > 0L

            StyledBalance(
                balanceString = walletSnapshot.changePendingBalance().toZecString(),
                textStyles =
                    Pair(
                        ZcashTheme.extendedTypography.balanceSingleStyles.first,
                        ZcashTheme.extendedTypography.balanceSingleStyles.second
                    ),
                textColor = ZcashTheme.colors.textPending
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(Modifier.width(ZcashTheme.dimens.circularSmallProgressWidth)) {
                if (changePendingHasValue) {
                    CircularSmallProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun PendingTransactionsRow(walletSnapshot: WalletSnapshot) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BodySmall(
            text = stringResource(id = R.string.balances_pending_transactions).uppercase(),
            modifier = Modifier.fillMaxWidth(TEXT_PART_WIDTH_RATIO)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            val valuePendingHasValue = walletSnapshot.valuePendingBalance().value > 0L

            StyledBalance(
                balanceString = walletSnapshot.valuePendingBalance().toZecString(),
                textStyles =
                    Pair(
                        ZcashTheme.extendedTypography.balanceSingleStyles.first,
                        ZcashTheme.extendedTypography.balanceSingleStyles.second
                    ),
                textColor = ZcashTheme.colors.textPending
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(Modifier.width(ZcashTheme.dimens.circularSmallProgressWidth)) {
                if (valuePendingHasValue) {
                    CircularSmallProgressIndicator()
                }
            }
        }
    }
}
