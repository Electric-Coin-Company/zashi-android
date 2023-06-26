package co.electriccoin.zcash.ui.screen.transfer.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun WalletPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            TransferMainView(onSendMoney = {}, onReceiveMoney = {}, onTopUp = {})
        }
    }
}

@Composable
fun TransferMainView(onSendMoney: () -> Unit, onReceiveMoney: () -> Unit, onTopUp: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(dimensionResource(id = R.dimen.screen_standard_margin))
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.back_icon_size)))
        Image(
            painter = painterResource(id = R.drawable.ic_nighthawk_logo),
            contentDescription = "logo", contentScale = ContentScale.Inside,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
        TitleLarge(text = stringResource(id = R.string.ns_nighthawk), textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(40.dp))
        BodyMedium(text = stringResource(id = R.string.ns_send_and_receive_zcash), color = ZcashTheme.colors.surfaceEnd)
        Spacer(modifier = Modifier.height(13.dp))
        SettingsListItem(
            iconRes = R.drawable.ic_arrow_back_black_24dp,
            title = stringResource(id = R.string.ns_send_money),
            desc = stringResource(id = R.string.ns_send_money_text),
            modifier = Modifier.heightIn(min = dimensionResource(id = R.dimen.setting_list_item_min_height))
                .clickable {
                    onSendMoney.invoke()
                },
            rotateByDegree = 180f
        )
        Spacer(modifier = Modifier.height(10.dp))
        SettingsListItem(
            iconRes = R.drawable.ic_arrow_back_black_24dp,
            title = stringResource(id = R.string.ns_receive_money),
            desc = stringResource(id = R.string.ns_receive_money_text),
            modifier = Modifier.heightIn(min = dimensionResource(id = R.dimen.setting_list_item_min_height))
                .clickable {
                    onReceiveMoney.invoke()
                }
        )
        Spacer(modifier = Modifier.height(10.dp))
        SettingsListItem(
            iconRes = R.drawable.ic_icon_top_up,
            title = stringResource(id = R.string.ns_top_up),
            desc = stringResource(id = R.string.ns_top_up_text),
            modifier = Modifier.heightIn(min = dimensionResource(id = R.dimen.setting_list_item_min_height))
                .clickable {
                    onTopUp.invoke()
                }
        )
    }
}
