package co.electriccoin.zcash.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BodySmall
import co.electriccoin.zcash.ui.design.component.TitleMedium
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview
@Composable
fun SettingListItemPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            SettingsListItem(
                iconRes = R.drawable.ic_icon_transparent,
                title = "Send Money",
                desc = "Allow someone to scan the code to send the money",
                modifier = Modifier.heightIn(min = 50.dp)
            )
        }

    }
}

@Composable
fun SettingsListItem(@DrawableRes iconRes: Int, title: String, desc: String, modifier: Modifier = Modifier, rotateByDegree: Float = 0f, showDivider: Boolean = true) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier)
        ) {
            Icon(painter = painterResource(id = iconRes), contentDescription = "null", modifier = Modifier.size(24.dp).rotate(rotateByDegree))
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                TitleMedium(text = title)
                Spacer(modifier = Modifier.height(5.dp))
                BodySmall(text = desc)
            }
        }
        if (showDivider) {
            Divider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = colorResource(id = co.electriccoin.zcash.ui.design.R.color.ns_navy)
            )
        }
    }
}