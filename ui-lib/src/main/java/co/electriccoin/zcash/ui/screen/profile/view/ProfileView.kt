package co.electriccoin.zcash.ui.screen.profile.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cash.z.ecc.sdk.fixture.WalletAddressFixture
import cash.z.ecc.sdk.model.WalletAddress
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TertiaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.profile.util.AndroidQrCodeImageGenerator
import co.electriccoin.zcash.ui.screen.profile.util.JvmQrCodeGenerator
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

@Preview
@Composable
fun ComposablePreview() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            Profile(
                walletAddress = runBlocking { WalletAddressFixture.unified() },
                onBack = {},
                onAddressDetails = {},
                onAddressBook = {},
                onSettings = {},
                onCoinholderVote = {},
                onSupport = {}
            )
        }
    }
}

@Composable
@Suppress("LongParameterList")
fun Profile(
    walletAddress: WalletAddress,
    onBack: () -> Unit,
    onAddressDetails: () -> Unit,
    onAddressBook: () -> Unit,
    onSettings: () -> Unit,
    onCoinholderVote: () -> Unit,
    onSupport: () -> Unit
) {
    Column {
        ProfileTopAppBar(onBack = onBack)
        ProfileContents(
            walletAddress = walletAddress,
            onAddressDetails = onAddressDetails,
            onAddressBook = onAddressBook,
            onSettings = onSettings,
            onCoinholderVote = onCoinholderVote,
            onSupport = onSupport
        )
    }
}

@Composable
private fun ProfileTopAppBar(onBack: () -> Unit) {
    SmallTopAppBar(
        title = { Text(text = stringResource(id = R.string.profile_title)) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.profile_back_content_description),
                )
            }
        }
    )
}

private val DEFAULT_QR_CODE_SIZE = 320.dp

@Composable
@Suppress("LongParameterList")
private fun ProfileContents(
    walletAddress: WalletAddress,
    onAddressDetails: () -> Unit,
    onAddressBook: () -> Unit,
    onSettings: () -> Unit,
    onCoinholderVote: () -> Unit,
    onSupport: () -> Unit,
    isAddressBookEnabled: Boolean = false
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        QrCode(data = walletAddress.address, DEFAULT_QR_CODE_SIZE, Modifier.align(Alignment.CenterHorizontally))
        Body(text = stringResource(id = R.string.wallet_address_unified), Modifier.align(Alignment.CenterHorizontally))
        // TODO [#163]: Ellipsize center of the string
        Text(
            text = walletAddress.address,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            overflow = TextOverflow.Ellipsis
        )
        PrimaryButton(onClick = onAddressDetails, text = stringResource(id = R.string.profile_see_address_details))
        if (isAddressBookEnabled) {
            TertiaryButton(onClick = onAddressBook, text = stringResource(id = R.string.profile_address_book))
        }
        TertiaryButton(onClick = onSettings, text = stringResource(id = R.string.profile_settings))
        Divider()
        TertiaryButton(onClick = onCoinholderVote, text = stringResource(id = R.string.profile_coinholder_vote))
        TertiaryButton(onClick = onSupport, text = stringResource(id = R.string.profile_support))
    }
}

@Composable
private fun QrCode(data: String, size: Dp, modifier: Modifier) {
    val sizePixels = with(LocalDensity.current) { size.toPx() }.roundToInt()

    // In the future, use actual/expect to switch QR code generator implementations for multiplatform

    // Note that our implementation has an extra array copy to BooleanArray, which is a cross-platform
    // representation.  This should have minimal performance impact since the QR code is relatively
    // small and we only generate QR codes infrequently.

    val qrCodePixelArray = JvmQrCodeGenerator.generate(data, sizePixels)
    val qrCodeImage = AndroidQrCodeImageGenerator.generate(qrCodePixelArray, sizePixels)

    Image(
        bitmap = qrCodeImage,
        contentDescription = stringResource(R.string.profile_qr_code_content_description),
        modifier = modifier
    )
}
