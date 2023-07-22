package co.electriccoin.zcash.ui.screen.topup.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.WalletAddresses
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.AlertDialog
import co.electriccoin.zcash.ui.common.SIDE_SHIFT_AFFILIATE_LINK
import co.electriccoin.zcash.ui.common.STEALTH_HEALTH_AFFILIATE_LINK
import co.electriccoin.zcash.ui.common.SettingsListItem
import co.electriccoin.zcash.ui.design.component.BodyMedium
import co.electriccoin.zcash.ui.design.component.TitleLarge
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview
@Composable
fun TopUpPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            TopUp(onBack = {}, onLaunchUrl = {}, walletAddress = null)
        }
    }
}

@Composable
fun TopUp(walletAddress: WalletAddresses?, onBack: () -> Unit, onLaunchUrl: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.screen_standard_margin))
    ) {
        val clipboardManager = LocalClipboardManager.current
        var showSideShiftDialog by remember { mutableStateOf(false) }
        var showStealthHealthDialog by remember { mutableStateOf(false) }
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(dimensionResource(id = R.dimen.back_icon_size))
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = stringResource(R.string.receive_back_content_description)
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_nighthawk_logo),
            contentDescription = "logo", contentScale = ContentScale.Inside,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
        TitleLarge(text = stringResource(id = R.string.ns_nighthawk), textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
        BodyMedium(text = stringResource(id = R.string.ns_send_and_receive_zcash), textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally), color = ZcashTheme.colors.surfaceEnd)
        Spacer(modifier = Modifier.height(40.dp))
        BodyMedium(text = stringResource(id = R.string.ns_receive_money_securely), color = ZcashTheme.colors.surfaceEnd)
        Spacer(modifier = Modifier.height(13.dp))

        SettingsListItem(
            iconRes = R.drawable.ic_icon_side_shift,
            title = stringResource(id = R.string.ns_swap_sideshift),
            desc = stringResource(id = R.string.ns_swap_sideshift_text),
            modifier = Modifier
                .heightIn(min = dimensionResource(id = R.dimen.setting_list_item_min_height))
                .clickable {
                    walletAddress?.sapling?.address?.let {
                        clipboardManager.setText(AnnotatedString(it))
                    }
                    showSideShiftDialog = true
                }
        )
        Spacer(modifier = Modifier.height(10.dp))
        SettingsListItem(
            iconRes = R.drawable.ic_icon_side_shift,
            title = stringResource(id = R.string.ns_swap_stealthex),
            desc = stringResource(id = R.string.ns_swap_stealthex_text),
            modifier = Modifier
                .heightIn(min = dimensionResource(id = R.dimen.setting_list_item_min_height))
                .clickable {
                    walletAddress?.transparent?.address?.let {
                        clipboardManager.setText(AnnotatedString(it))
                    }
                    showStealthHealthDialog = true
                }
        )

        if (showSideShiftDialog) {
            AlertDialog(
                title = stringResource(id = R.string.fund_wallet_sideshift_title),
                desc = stringResource(id = R.string.fund_wallet_sideshift_description),
                confirmText = stringResource(id = R.string.open_browser),
                dismissText = stringResource(id = R.string.ns_cancel),
                onConfirm = {
                    showSideShiftDialog = false
                    onLaunchUrl(SIDE_SHIFT_AFFILIATE_LINK)
                },
                onDismiss = {
                    showSideShiftDialog = false
                }
            )
        }

        if (showStealthHealthDialog) {
            AlertDialog(
                title = stringResource(id = R.string.fund_wallet_stealthex_title),
                desc = stringResource(id = R.string.fund_wallet_stealthex_description),
                confirmText = stringResource(id = R.string.open_browser),
                dismissText = stringResource(id = R.string.ns_cancel),
                onConfirm = {
                    showStealthHealthDialog = false
                    onLaunchUrl(STEALTH_HEALTH_AFFILIATE_LINK)
                },
                onDismiss = {
                    showStealthHealthDialog = false
                }
            )
        }
    }
}
