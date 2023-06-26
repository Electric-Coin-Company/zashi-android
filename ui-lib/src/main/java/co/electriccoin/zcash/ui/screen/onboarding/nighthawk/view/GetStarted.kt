package co.electriccoin.zcash.ui.screen.onboarding.nighthawk.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BodyMedium
import co.electriccoin.zcash.ui.design.component.BodySmall
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.Reference
import co.electriccoin.zcash.ui.design.component.TertiaryButton
import co.electriccoin.zcash.ui.design.component.TitleLarge
import co.electriccoin.zcash.ui.design.component.TitleMedium
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview
@Composable
fun ComposablePreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            GetStarted(
                onCreateWallet = {},
                onRestore = {},
                onReference = {}
            )
        }
    }
}

@Composable
fun GetStarted(
    onCreateWallet: () -> Unit,
    onRestore: () -> Unit,
    onReference: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.fillMaxSize(0.1f))
        Image(painter = painterResource(id = R.drawable.ic_nighthawk_logo), contentDescription = "logo", contentScale = ContentScale.Inside)
        Spacer(Modifier.height(dimensionResource(id = R.dimen.top_margin_back_btn)))
        TitleLarge(text = stringResource(id = R.string.ns_nighthawk), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.offset)))
        TitleMedium(text = stringResource(id = R.string.ns_get_started), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.text_margin)))
        BodyMedium(text = stringResource(id = R.string.ns_landing_text), modifier = Modifier.fillMaxWidth(0.7f), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(64.dp))
        BodySmall(text = stringResource(id = R.string.ns_landing_footer), modifier = Modifier.fillMaxWidth(0.7f), textAlign = TextAlign.Center)
        Reference(text = stringResource(id = R.string.ns_terms_conditions), style = TextStyle(fontSize = TextUnit(12f, TextUnitType.Sp)), onClick = onReference)
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            onClick = onCreateWallet,
            text = stringResource(id = R.string.ns_create_wallet).uppercase(),
            modifier = Modifier.sizeIn(minWidth = dimensionResource(id = R.dimen.button_min_width), minHeight = dimensionResource(id = R.dimen.button_height))
        )
        TertiaryButton(
            onClick = onRestore,
            text = stringResource(id = R.string.ns_restore_from_backup).uppercase()
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.screen_bottom_margin)))
    }
}
