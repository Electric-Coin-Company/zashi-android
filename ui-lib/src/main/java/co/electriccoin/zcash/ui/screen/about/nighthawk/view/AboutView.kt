package co.electriccoin.zcash.ui.screen.about.nighthawk.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BodyMedium
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.Reference
import co.electriccoin.zcash.ui.design.component.TitleMedium
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Composable
@Preview
fun AboutViewPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            AboutView(onBack = {}, onViewSource = {}, onTermAndCondition = {}, onViewLicence = {})
        }
    }
}

@Composable
fun AboutView(onBack: () -> Unit, onViewSource: () -> Unit, onTermAndCondition: () -> Unit, onViewLicence: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.screen_standard_margin))
            .verticalScroll(rememberScrollState())
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(dimensionResource(id = R.dimen.back_icon_size))) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.receive_back_content_description))
        }
        Spacer(modifier = Modifier.height(40.dp))
        TitleMedium(text = stringResource(id = R.string.ns_about), color = colorResource(id = co.electriccoin.zcash.ui.design.R.color.ns_parmaviolet))
        Spacer(modifier = Modifier.height(24.dp))
        BodyMedium(text = stringResource(id = R.string.ns_about_message))
        Spacer(modifier = Modifier.height(24.dp))
        Reference(text = stringResource(id = R.string.ns_view_source), style = TextStyle(fontSize = TextUnit(12f, TextUnitType.Sp)), onClick = onViewSource)
        Spacer(modifier = Modifier.height(10.dp))
        Reference(text = stringResource(id = R.string.ns_terms_conditions), style = TextStyle(fontSize = TextUnit(12f, TextUnitType.Sp)), onClick = onTermAndCondition)
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            onClick = onViewLicence,
            text = stringResource(id = R.string.ns_view_licences).uppercase(),
            outerPaddingValues = PaddingValues(top = ZcashTheme.dimens.spacingSmall),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
