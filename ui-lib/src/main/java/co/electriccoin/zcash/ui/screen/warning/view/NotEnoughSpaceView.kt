package co.electriccoin.zcash.ui.screen.warning.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.component.Small
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview("NotEnoughSpace")
@Composable
private fun NotEnoughSpacePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            NotEnoughSpaceView(
                storageSpaceRequiredGigabytes = 1,
                spaceRequiredToContinueMegabytes = 300
            )
        }
    }
}

// TODO [#883]: NotEnoughSpace screen has dark theme hardcoded
// TODO [#883]: https://github.com/zcash/secant-android-wallet/issues/883

@Composable
fun NotEnoughSpaceView(storageSpaceRequiredGigabytes: Int, spaceRequiredToContinueMegabytes: Int) {
    @Suppress("MagicNumber")
    val backgroundColor = Color(0xFF1A233A)
    Column(
        Modifier
            .background(backgroundColor)
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painterResource(id = R.drawable.not_enough_space), "", Modifier.fillMaxWidth())
        Spacer(Modifier.height(32.dp))
        Header(text = stringResource(id = R.string.not_enough_space_title), color = Color.White)
        Spacer(Modifier.height(32.dp))
        Body(
            text = stringResource(id = R.string.not_enough_space_description, storageSpaceRequiredGigabytes),
            textAlign = TextAlign.Center,
            color = Color.White
        )
        Spacer(Modifier.height(64.dp))
        Small(
            text = stringResource(id = R.string.space_required_to_continue, spaceRequiredToContinueMegabytes),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
