package co.electriccoin.zcash.ui.screen.pin.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.WALLET_PASSWORD_LENGTH
import co.electriccoin.zcash.ui.design.component.BodyMedium
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.send.nighthawk.model.NumberPadValueTypes
import co.electriccoin.zcash.ui.screen.send.nighthawk.model.passwordPadKeys

@Preview
@Composable
fun EnterPinCommonUIPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            EnterPinCommonUI(incorrectPassword = false, enteredPassword = "11", message = "Enter Six Digit Pin", onKeyPressed = {})
        }
    }
}

@Composable
fun EnterPinCommonUI(incorrectPassword: Boolean, enteredPassword: String, message: String, onKeyPressed: (NumberPadValueTypes) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Icon(painter = painterResource(id = R.drawable.ic_lock), contentDescription = null, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(dimensionResource(id = R.dimen.pageMargin)))

        Row(
            modifier = Modifier
                .fillMaxWidth(.75f)
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(WALLET_PASSWORD_LENGTH) { currentItemPos ->
                if (incorrectPassword) {
                    Icon(painter = painterResource(id = R.drawable.ic_icon_wrong_password), contentDescription = null, tint = ZcashTheme.colors.onBackgroundHeader)
                } else {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .then(
                                if (currentItemPos < enteredPassword.length) {
                                    Modifier.background(color = ZcashTheme.colors.onBackgroundHeader, shape = CircleShape)
                                } else {
                                    Modifier
                                        .background(color = Color.Transparent, shape = CircleShape)
                                        .border(width = 2.dp, color = colorResource(id = co.electriccoin.zcash.ui.design.R.color.zcashGray), shape = CircleShape)
                                }
                            )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
        BodyMedium(text = message, textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.weight(1f))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(passwordPadKeys) {
                TextButton(
                    onClick = { onKeyPressed(it) },
                    shape = CircleShape
                ) {
                    Text(text = it.keyValue, fontSize = 30.sp, color = Color.White)
                }
            }
        }
    }
}
