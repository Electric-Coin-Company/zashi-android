package co.electriccoin.zcash.ui.screen.receive.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import cash.z.ecc.android.sdk.model.WalletAddress
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.BrightenScreen
import co.electriccoin.zcash.ui.common.DisableScreenTimeout
import co.electriccoin.zcash.ui.common.test.CommonTag
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.receive.util.AndroidQrCodeImageGenerator
import co.electriccoin.zcash.ui.screen.receive.util.JvmQrCodeGenerator
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

@Preview("Receive")
@Composable
private fun ComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Receive(
                walletAddress = runBlocking { WalletAddressFixture.unified() },
                onSettings = {},
                onAddressDetails = {},
                onAdjustBrightness = {},
            )
        }
    }
}

@Composable
fun Receive(
    walletAddress: WalletAddress,
    onSettings: () -> Unit,
    onAddressDetails: () -> Unit,
    onAdjustBrightness: (Boolean) -> Unit,
) {
    val (brightness, setBrightness) = rememberSaveable { mutableStateOf(false) }

    // Rework this into Scaffold
    Column {
        ReceiveTopAppBar(
            adjustBrightness = brightness,
            onSettings = onSettings,
            onBrightness = {
                onAdjustBrightness(!brightness)
                setBrightness(!brightness)
            }
        )
        ReceiveContents(
            walletAddress = walletAddress,
            onAddressDetails = onAddressDetails,
            adjustBrightness = brightness,
            modifier =
                Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        top = ZcashTheme.dimens.spacingDefault,
                        start = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                        end = ZcashTheme.dimens.screenHorizontalSpacingRegular
                    )
        )
    }
}

@Composable
private fun ReceiveTopAppBar(
    adjustBrightness: Boolean,
    onSettings: () -> Unit,
    onBrightness: () -> Unit
) {
    SmallTopAppBar(
        titleText = stringResource(id = R.string.receive_title),
        regularActions = {
            IconButton(
                onClick = onBrightness
            ) {
                Icon(
                    imageVector =
                        if (adjustBrightness) {
                            Icons.Default.BrightnessLow
                        } else {
                            Icons.Default.BrightnessHigh
                        },
                    contentDescription = stringResource(R.string.receive_brightness_content_description)
                )
            }
        },
        hamburgerMenuActions = {
            IconButton(
                onClick = onSettings,
                modifier = Modifier.testTag(CommonTag.SETTINGS_TOP_BAR_BUTTON)
            ) {
                Icon(
                    painter = painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.hamburger_menu_icon),
                    contentDescription = stringResource(id = R.string.settings_menu_content_description)
                )
            }
        }
    )
}

private val DEFAULT_QR_CODE_SIZE = 320.dp

@Composable
private fun ReceiveContents(
    walletAddress: WalletAddress,
    onAddressDetails: () -> Unit,
    adjustBrightness: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QrCode(
            data = walletAddress.address,
            size = DEFAULT_QR_CODE_SIZE,
            adjustBrightness = adjustBrightness,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        Body(
            text = stringResource(id = R.string.wallet_address_unified),
            Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        // TODO [#163]: Ellipsize center of the string
        // TODO [#163]: https://github.com/Electric-Coin-Company/zashi-android/issues/163
        Text(
            text = walletAddress.address,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        PrimaryButton(
            onClick = onAddressDetails,
            text = stringResource(id = R.string.receive_see_address_details)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingHuge))
    }
}

@Composable
private fun QrCode(
    data: String,
    size: Dp,
    modifier: Modifier = Modifier,
    adjustBrightness: Boolean = false,
) {
    Column(modifier = modifier) {
        if (adjustBrightness) {
            BrightenScreen()
            DisableScreenTimeout()
        }

        val sizePixels = with(LocalDensity.current) { size.toPx() }.roundToInt()

        // In the future, use actual/expect to switch QR code generator implementations for multiplatform

        // Note that our implementation has an extra array copy to BooleanArray, which is a cross-platform
        // representation.  This should have minimal performance impact since the QR code is relatively
        // small and we only generate QR codes infrequently.

        val qrCodePixelArray = JvmQrCodeGenerator.generate(data, sizePixels)
        val qrCodeImage = AndroidQrCodeImageGenerator.generate(qrCodePixelArray, sizePixels)

        Image(
            bitmap = qrCodeImage,
            contentDescription = stringResource(R.string.receive_qr_code_content_description)
        )
    }
}
