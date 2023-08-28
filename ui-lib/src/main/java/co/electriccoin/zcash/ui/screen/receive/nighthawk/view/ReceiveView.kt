package co.electriccoin.zcash.ui.screen.receive.nighthawk.view

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.SettingsListItem
import co.electriccoin.zcash.ui.design.component.BodyMedium
import co.electriccoin.zcash.ui.design.component.TitleLarge
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview
@Composable
fun ReceiveViewPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            ReceiveView(
                onBack = {},
                onTopUpWallet = {},
                onCopyPrivateAddress = {},
                onShowQrCode = {},
                onCopyTransparentAddress = {}
            )
        }
    }
}

@Composable
fun ReceiveView(
    onBack: () -> Unit,
    onShowQrCode: () -> Unit,
    onCopyPrivateAddress: () -> Unit,
    onTopUpWallet: () -> Unit,
    onCopyTransparentAddress: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.screen_standard_margin))
    ) {
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
        TitleLarge(
            text = stringResource(id = R.string.ns_nighthawk),
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
        BodyMedium(
            text = stringResource(id = R.string.ns_send_and_receive_zcash),
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = ZcashTheme.colors.surfaceEnd
        )
        Spacer(modifier = Modifier.height(40.dp))
        BodyMedium(
            text = stringResource(id = R.string.ns_receive_money_securely),
            color = ZcashTheme.colors.surfaceEnd
        )
        Spacer(modifier = Modifier.height(13.dp))
        SettingsListItem(
            iconRes = R.drawable.ic_icon_scan_qr,
            title = stringResource(id = R.string.ns_show_qr_code),
            desc = stringResource(id = R.string.ns_show_qr_code_text),
            modifier = Modifier
                .heightIn(min = dimensionResource(id = R.dimen.setting_list_item_min_height))
                .clickable { onShowQrCode() }
        )
        Spacer(modifier = Modifier.height(10.dp))
        SettingsListItem(
            iconRes = R.drawable.ic_content_copy,
            title = stringResource(id = R.string.ns_copy_private_address),
            desc = stringResource(id = R.string.ns_copy_private_address_text),
            modifier = Modifier
                .heightIn(min = dimensionResource(id = R.dimen.setting_list_item_min_height))
                .clickable { onCopyPrivateAddress() }
        )
        Spacer(modifier = Modifier.height(10.dp))
        SettingsListItem(
            iconRes = R.drawable.ic_icon_top_up,
            title = stringResource(id = R.string.ns_top_up_your_wallet),
            desc = stringResource(id = R.string.ns_top_up_text),
            modifier = Modifier
                .heightIn(min = dimensionResource(id = R.dimen.setting_list_item_min_height))
                .clickable { onTopUpWallet() }
        )
        Spacer(modifier = Modifier.height(26.dp))
        BodyMedium(
            text = stringResource(id = R.string.ns_receive_money_publicly),
            color = ZcashTheme.colors.surfaceEnd
        )
        Spacer(modifier = Modifier.height(15.dp))
        SettingsListItem(
            iconRes = R.drawable.ic_icon_transparent,
            title = stringResource(id = R.string.ns_non_private_address),
            desc = stringResource(id = R.string.ns_receive_money_publicly_text),
            modifier = Modifier
                .heightIn(min = dimensionResource(id = R.dimen.setting_list_item_min_height))
                .clickable { onCopyTransparentAddress() }
        )
    }
}