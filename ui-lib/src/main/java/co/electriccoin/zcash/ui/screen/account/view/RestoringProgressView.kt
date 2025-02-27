@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.account.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.common.compose.SynchronizationStatus
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.balances.BalancesTag
import co.electriccoin.zcash.ui.screen.balances.model.StatusAction

internal fun LazyListScope.createRestoringProgressView(
    onStatusClick: (StatusAction) -> Unit,
    walletRestoringState: WalletRestoringState,
    walletSnapshot: WalletSnapshot,
) {
    if (walletRestoringState == WalletRestoringState.RESTORING) {
        item {
            Column(
                modifier =
                    Modifier
                        .fillParentMaxWidth()
                        .background(color = ZcashTheme.colors.historySyncingColor)
            ) {
                Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

                // Do not calculate and use the app update information here, as the sync bar won't be displayed after
                // the wallet is fully restored
                SynchronizationStatus(
                    onStatusClick = onStatusClick,
                    testTag = BalancesTag.STATUS,
                    walletSnapshot = walletSnapshot,
                    modifier =
                        Modifier
                            .padding(horizontal = ZcashTheme.dimens.spacingDefault)
                            .animateContentSize()
                )

                Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
