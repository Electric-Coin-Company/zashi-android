package co.electriccoin.zcash.ui.screen.warning.view

import androidx.compose.foundation.Image
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

@Composable
fun NotEnoughSpaceView(storageSpaceRequiredGigabytes: String, spaceRequiredToContinue: String) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painterResource(id = R.drawable.onboarding_1_shielded), "", Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Header(stringResource(id = R.string.not_enough_space_title))
        Spacer(Modifier.height(16.dp))
        Body(
            text = stringResource(id = R.string.not_enough_space_description, storageSpaceRequiredGigabytes),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(64.dp))
        Small(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.space_required_to_continue, spaceRequiredToContinue),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun NotEnoughSpacePreview() {
    ZcashTheme {
        GradientSurface {
            NotEnoughSpaceView(
                storageSpaceRequiredGigabytes = "1",
                spaceRequiredToContinue = "300"
            )
        }
    }
}
