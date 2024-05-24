package co.electriccoin.zcash.ui.common.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.sdk.extension.toPercentageWithDecimal
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.design.component.BodySmall
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.SmallLinearProgressIndicator
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.balances.model.StatusAction
import co.electriccoin.zcash.ui.screen.balances.model.WalletDisplayValues

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun BalanceWidgetPreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface(
            modifier = Modifier.fillMaxWidth()
        ) {
            SynchronizationStatus(
                isUpdateAvailable = false,
                onStatusClick = {},
                walletSnapshot = WalletSnapshotFixture.new(),
            )
        }
    }
}

@Composable
fun SynchronizationStatus(
    isUpdateAvailable: Boolean,
    onStatusClick: (StatusAction) -> Unit,
    walletSnapshot: WalletSnapshot,
    modifier: Modifier = Modifier,
    testTag: String? = null,
) {
    val walletDisplayValues =
        WalletDisplayValues.getNextValues(
            context = LocalContext.current,
            walletSnapshot = walletSnapshot,
            isUpdateAvailable = isUpdateAvailable,
        )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (walletDisplayValues.statusText.isNotEmpty()) {
            BodySmall(
                text = walletDisplayValues.statusText,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(ZcashTheme.dimens.regularRippleEffectCorner))
                        .clickable { onStatusClick(walletDisplayValues.statusAction) }
                        .padding(all = ZcashTheme.dimens.spacingSmall)
                        .then(testTag?.let { Modifier.testTag(testTag) } ?: Modifier),
            )
        }

        BodySmall(
            text =
                stringResource(
                    id = R.string.balances_status_syncing_percentage,
                    walletSnapshot.progress.toPercentageWithDecimal()
                ),
            textFontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        SmallLinearProgressIndicator(
            progress = walletSnapshot.progress.decimal,
            modifier =
                Modifier.padding(
                    horizontal = ZcashTheme.dimens.spacingDefault
                )
        )
    }
}

@Composable
fun StatusDialog(
    statusAction: StatusAction.Detailed,
    onDone: () -> Unit
) {
    AppAlertDialog(
        title = stringResource(id = R.string.balances_status_error_dialog_title),
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(text = statusAction.details)
            }
        },
        confirmButtonText = stringResource(id = R.string.balances_status_dialog_button),
        onConfirmButtonClick = onDone
    )
}
