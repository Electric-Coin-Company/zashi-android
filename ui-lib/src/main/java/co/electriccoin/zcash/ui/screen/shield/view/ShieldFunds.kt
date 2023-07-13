package co.electriccoin.zcash.ui.screen.shield.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BodySmall
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TitleLarge
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.shield.model.ShieldingProcessState
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty

@Preview
@Composable
fun FundsAvailablePreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            ShieldFunds(onBack = {}, shieldingProcessState = ShieldingProcessState.SUCCESS)
        }
    }
}

@Composable
fun ShieldFunds(onBack: () -> Unit, shieldingProcessState: ShieldingProcessState) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(dimensionResource(id = R.dimen.screen_standard_margin))
        .verticalScroll(rememberScrollState())
    ) {
        val dynamicProperties = rememberLottieDynamicProperties(
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR,
                value = MaterialTheme.colorScheme.primary.toArgb(),
                keyPath = arrayOf(
                    "**"
                )
            )
        )
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(shieldingProcessState.animRes))
        IconButton(onClick = onBack, modifier = Modifier.size(dimensionResource(id = R.dimen.back_icon_size))) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.receive_back_content_description))
        }
        Image(painter = painterResource(id = R.drawable.ic_nighthawk_logo), contentDescription = "logo", contentScale = ContentScale.Inside, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
        TitleLarge(text = stringResource(id = R.string.ns_nighthawk), textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
        BodySmall(text = stringResource(id = shieldingProcessState.statusRes), textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.weight(1f))

        LottieAnimation(
            composition = composition,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(250.dp),
            dynamicProperties = if (shieldingProcessState == ShieldingProcessState.CREATING) dynamicProperties else null
        )

        Spacer(modifier = Modifier.weight(1f))

        when (shieldingProcessState) {
            ShieldingProcessState.CREATING -> {}
            ShieldingProcessState.SUCCESS -> {
                PrimaryButton(
                    onClick = onBack,
                    text = stringResource(id = R.string.ns_done).uppercase(),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .sizeIn(minWidth = dimensionResource(id = R.dimen.button_min_width), minHeight = dimensionResource(id = R.dimen.button_height)),
                )
            }
            ShieldingProcessState.FAILURE -> {
                PrimaryButton(
                    onClick = onBack,
                    text = stringResource(id = R.string.restore_back_content_description).uppercase(),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .sizeIn(minWidth = dimensionResource(id = R.dimen.button_min_width), minHeight = dimensionResource(id = R.dimen.button_height)),
                )
            }
        }
    }
}
