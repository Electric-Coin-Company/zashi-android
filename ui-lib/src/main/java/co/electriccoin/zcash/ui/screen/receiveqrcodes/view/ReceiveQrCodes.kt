package co.electriccoin.zcash.ui.screen.receiveqrcodes.view

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import cash.z.ecc.android.sdk.fixture.WalletAddressesFixture
import cash.z.ecc.android.sdk.model.WalletAddresses
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.BrightenScreen
import co.electriccoin.zcash.ui.common.DisableScreenTimeout
import co.electriccoin.zcash.ui.design.component.BodyMedium
import co.electriccoin.zcash.ui.design.component.BodySmall
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TitleLarge
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.receive.util.AndroidQrCodeImageGenerator
import co.electriccoin.zcash.ui.screen.receive.util.JvmQrCodeGenerator
import co.electriccoin.zcash.ui.screen.receiveqrcodes.QRAddressPagerItem
import co.electriccoin.zcash.ui.screen.wallet.view.PageIndicator
import kotlinx.coroutines.runBlocking
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
@Preview
fun ReceiveQrCodesPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            ReceiveQrCodes(walletAddresses = runBlocking { WalletAddressesFixture.new() }, onBack = {}, onSeeMoreTopUpOption = {})
        }
    }
}

@Composable
@Preview
fun QrAddressCardPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            QrAddressCardUi(
                QRAddressPagerItem.UNIFIED(
                    addressType = stringResource(id = R.string.ns_unified_address),
                    address = "unikasjdkjdjkjsakdjjkajsdkasdkjasdkjsadkjsakjd,aksdjkjdasjkjsjkasjksa",
                    btnText = stringResource(id = R.string.ns_copy)
                ),
                onCopyAddress = {}
            )
        }
    }
}

@Composable
@Preview
fun TopUpCardPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            TopUpCardUi(
                QRAddressPagerItem.TOP_UP(
                    titleText = stringResource(id = R.string.ns_top_up_your_wallet),
                    bodyText = stringResource(id = R.string.ns_top_up_your_wallet_msg),
                    btnText = stringResource(id = R.string.ns_see_more)
                ),
                onSeeMore = {}
            )
        }
    }
}

private val DEFAULT_QR_CODE_SIZE = 180.dp
private const val QR_CARD_HEIGHT_PER = 0.8f

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReceiveQrCodes(
    walletAddresses: WalletAddresses,
    onBack: () -> Unit,
    onSeeMoreTopUpOption: () -> Unit
) {
    Twig.info { "WalletAddresses $walletAddresses" }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.screen_standard_margin))
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(dimensionResource(id = R.dimen.back_icon_size))
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = stringResource(R.string.receive_back_content_description)
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_nighthawk_logo),
            contentDescription = "logo", contentScale = ContentScale.Inside,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
        TitleLarge(text = stringResource(id = R.string.ns_nighthawk), textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(27.dp))

        val state = rememberPagerState(initialPage = 1)
        val totalCards = 4
        HorizontalPager(pageCount = totalCards, state = state, pageSpacing = 16.dp, contentPadding = PaddingValues(horizontal = 55.dp)) { page ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(QR_CARD_HEIGHT_PER)
                    .align(Alignment.CenterHorizontally)
                    .graphicsLayer {
                        val pageOffset = ((state.currentPage - page) + state.currentPageOffsetFraction).absoluteValue
                        // We animate the alpha, between 50% and 100%
                        val animOffset = lerp(
                            start = 0.8f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                        scaleY = animOffset
                        alpha = animOffset
                    },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = co.electriccoin.zcash.ui.design.R.color.ns_navy))
            ) {
                val qrAddressPagerItem = getQRAddressPagerItem(page = page, walletAddresses = walletAddresses)
                if (qrAddressPagerItem is QRAddressPagerItem.TOP_UP) {
                    TopUpCardUi(topUp = qrAddressPagerItem, onSeeMore = onSeeMoreTopUpOption)
                } else {
                    QrAddressCardUi(
                        qrAddressPagerItem = qrAddressPagerItem,
                        onCopyAddress = {
                            clipboardManager.setText(AnnotatedString(it))
                            Toast.makeText(context, context.getString(R.string.ns_copied), Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(27.dp))
        PageIndicator(pageCount = totalCards, pagerState = state)
    }
}

@Composable
fun QrAddressCardUi(qrAddressPagerItem: QRAddressPagerItem, onCopyAddress: (String) -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        QrCode(data = qrAddressPagerItem.title, DEFAULT_QR_CODE_SIZE, Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(18.dp))
        BodyMedium(text = qrAddressPagerItem.title, color = colorResource(id = co.electriccoin.zcash.ui.design.R.color.ns_parmaviolet))
        Spacer(modifier = Modifier.height(10.dp))
        BodySmall(text = qrAddressPagerItem.body)
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            onClick = { onCopyAddress(qrAddressPagerItem.body) },
            text = qrAddressPagerItem.buttonText.uppercase(),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .widthIn(min = 111.dp)
        )
    }
}

@Composable
fun TopUpCardUi(topUp: QRAddressPagerItem.TOP_UP, onSeeMore: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_icon_top_up), contentDescription = null, tint = colorResource(id = co.electriccoin.zcash.ui.design.R.color.ns_parmaviolet))
        Spacer(modifier = Modifier.height(21.dp))
        BodyMedium(text = topUp.title, color = colorResource(id = co.electriccoin.zcash.ui.design.R.color.ns_parmaviolet))
        Spacer(modifier = Modifier.height(10.dp))
        BodySmall(text = topUp.body)
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(onClick = onSeeMore, text = topUp.buttonText.uppercase(), modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .widthIn(min = 111.dp))
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

@Composable
private fun getQRAddressPagerItem(page: Int, walletAddresses: WalletAddresses): QRAddressPagerItem {
    return when (page) {
        1 -> QRAddressPagerItem.UNIFIED(
            addressType = stringResource(id = R.string.ns_unified_address),
            address = walletAddresses.unified.address,
            btnText = stringResource(id = R.string.ns_copy)
        )

        2 -> QRAddressPagerItem.SHIELDED(
            addressType = stringResource(id = R.string.ns_shielded_address),
            address = walletAddresses.sapling.address,
            btnText = stringResource(id = R.string.ns_copy)
        )

        3 -> QRAddressPagerItem.TRANSPARENT(
            addressType = stringResource(id = R.string.ns_transparent_address),
            address = walletAddresses.transparent.address,
            btnText = stringResource(id = R.string.ns_copy)
        )

        else -> QRAddressPagerItem.TOP_UP(
            titleText = stringResource(id = R.string.ns_top_up_your_wallet),
            bodyText = stringResource(id = R.string.ns_top_up_your_wallet_msg),
            btnText = stringResource(id = R.string.ns_see_more)
        )
    }
}
