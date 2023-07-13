package co.electriccoin.zcash.ui.screen.send.nighthawk.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import co.electriccoin.zcash.ui.design.component.BodyMedium
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TitleLarge
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.send.nighthawk.model.SendConfirmationState
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Preview
@Composable
fun SendConfirmationPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            SendConfirmation(sendConfirmationState = SendConfirmationState.Success(id = 10), onCancel = {}, onTryAgain = {}, onDone = {}, onMoreDetails = {})
        }
    }
}

@Composable
fun SendConfirmation(
    sendConfirmationState: SendConfirmationState,
    onCancel: () ->  Unit,
    onTryAgain: () ->  Unit,
    onDone: () ->  Unit,
    onMoreDetails: (Long) ->  Unit,
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(dimensionResource(id = R.dimen.screen_standard_margin))
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(sendConfirmationState.animRes))

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.back_icon_size)))
        Image(painter = painterResource(id = R.drawable.ic_nighthawk_logo), contentDescription = "logo", contentScale = ContentScale.Inside, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
        TitleLarge(text = stringResource(id = R.string.ns_nighthawk), textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
        BodyMedium(text = stringResource(id = sendConfirmationState.titleResId), textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally), color = ZcashTheme.colors.surfaceEnd)
        Spacer(modifier = Modifier.weight(1f))

        LottieAnimation(
            composition = composition,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(250.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        when (sendConfirmationState) {
            SendConfirmationState.Failed -> {
                PrimaryButton(
                    onClick = onTryAgain,
                    text = stringResource(id = R.string.ns_try_again).uppercase(),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .sizeIn(minWidth = dimensionResource(id = R.dimen.button_min_width), minHeight = dimensionResource(id = R.dimen.button_height)),
                )

                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    BodyMedium(text = stringResource(id = R.string.ns_cancel).uppercase(), color = ZcashTheme.colors.onBackgroundHeader)
                }
            }
            SendConfirmationState.Sending -> {
                PrimaryButton(
                    onClick = onCancel,
                    text = stringResource(id = R.string.ns_cancel).uppercase(),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .sizeIn(minWidth = dimensionResource(id = R.dimen.button_min_width), minHeight = dimensionResource(id = R.dimen.button_height)),
                )
            }
            is SendConfirmationState.Success -> {
                PrimaryButton(
                    onClick = onDone,
                    text = stringResource(id = R.string.ns_done).uppercase(),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .sizeIn(minWidth = dimensionResource(id = R.dimen.button_min_width), minHeight = dimensionResource(id = R.dimen.button_height)),
                )

                TextButton(
                    onClick = { onMoreDetails(sendConfirmationState.id) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    BodyMedium(text = stringResource(id = R.string.ns_more_details).uppercase(), color = ZcashTheme.colors.onBackgroundHeader)
                }
            }
        }
    }
}
