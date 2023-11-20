package co.electriccoin.zcash.ui.screen.receive.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
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
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.component.PrimaryButton
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
                onBack = {},
                onAddressDetails = {},
            )
        }
    }
}

@Composable
@Suppress("LongParameterList")
fun Receive(
    walletAddress: WalletAddress,
    onBack: () -> Unit,
    onAddressDetails: () -> Unit,
) {
    Column {
        ReceiveTopAppBar(onBack = onBack)
        ReceiveContents(
            walletAddress = walletAddress,
            onAddressDetails = onAddressDetails,
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(all = ZcashTheme.dimens.spacingDefault)
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ReceiveTopAppBar(onBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.receive_title)) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.receive_back_content_description)
                )
            }
        }
    )
}

private val DEFAULT_QR_CODE_SIZE = 320.dp

@Composable
@Suppress("LongParameterList")
private fun ReceiveContents(
    walletAddress: WalletAddress,
    onAddressDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QrCode(data = walletAddress.address, DEFAULT_QR_CODE_SIZE, Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        Header(
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
            modifier = Modifier
                .fillMaxHeight()
                .weight(MINIMAL_WEIGHT)
        )

        PrimaryButton(
            onClick = onAddressDetails,
            text = stringResource(id = R.string.receive_see_address_details),
            outerPaddingValues = PaddingValues(
                bottom = ZcashTheme.dimens.spacingHuge
            )
        )
    }
}

@Composable
private fun QrCode(data: String, size: Dp, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        BrightenScreen()
        DisableScreenTimeout()
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
