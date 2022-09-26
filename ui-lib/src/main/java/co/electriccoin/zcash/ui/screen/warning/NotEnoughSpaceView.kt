package co.electriccoin.zcash.ui.screen.warning

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Composable
fun NotEnoughSpaceView() {
    Column(Modifier
        .fillMaxSize()
        .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Image(painterResource(id = R.drawable.onboarding_1_shielded), "", Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(8.dp))
        Header(stringResource(id = R.string.not_enough_space_title))
        Body(stringResource(id = R.string.not_enough_space_description))
    }
}

@Preview
@Composable
private fun NotEnoughSpacePreview() {
    ZcashTheme {
        GradientSurface {
            NotEnoughSpaceView()
        }
    }
}