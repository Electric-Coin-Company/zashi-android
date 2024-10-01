package co.electriccoin.zcash.ui.screen.receive.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.fixture.WalletAddressesFixture
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.WalletAddresses
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.common.test.CommonTag
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.PagerTabs
import co.electriccoin.zcash.ui.design.component.Reference
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import co.electriccoin.zcash.ui.screen.receive.util.AndroidQrCodeImageGenerator
import co.electriccoin.zcash.ui.screen.receive.util.JvmQrCodeGenerator
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

@Preview
@Composable
private fun ReceivePreview() =
    ZcashTheme(forceDarkMode = false) {
        Receive(
            walletAddress = runBlocking { WalletAddressesFixture.new() },
            snackbarHostState = SnackbarHostState(),
            onSettings = {},
            onAddrCopyToClipboard = {},
            onQrImageShare = {},
            versionInfo = VersionInfoFixture.new(),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }

@Preview
@Composable
private fun ReceiveDarkPreview() =
    ZcashTheme(forceDarkMode = true) {
        Receive(
            walletAddress = runBlocking { WalletAddressesFixture.new() },
            snackbarHostState = SnackbarHostState(),
            onSettings = {},
            onAddrCopyToClipboard = {},
            onQrImageShare = {},
            versionInfo = VersionInfoFixture.new(),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }

@Suppress("LongParameterList")
@Composable
fun Receive(
    walletAddress: WalletAddresses?,
    snackbarHostState: SnackbarHostState,
    onSettings: () -> Unit,
    onAddrCopyToClipboard: (String) -> Unit,
    onQrImageShare: (ImageBitmap) -> Unit,
    topAppBarSubTitleState: TopAppBarSubTitleState,
    versionInfo: VersionInfo,
) {
    BlankBgScaffold(
        topBar = {
            ReceiveTopAppBar(
                onSettings = onSettings,
                subTitleState = topAppBarSubTitleState,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        if (null == walletAddress) {
            CircularScreenProgressIndicator()
        } else {
            ReceiveContents(
                walletAddresses = walletAddress,
                onAddressCopyToClipboard = onAddrCopyToClipboard,
                onQrImageShare = onQrImageShare,
                versionInfo = versionInfo,
                modifier =
                    Modifier.padding(
                        top = paddingValues.calculateTopPadding()
                        // We intentionally do not set the rest paddings, those are set by the underlying composable
                    ),
            )
        }
    }
}

@Composable
private fun ReceiveTopAppBar(
    onSettings: () -> Unit,
    subTitleState: TopAppBarSubTitleState,
) {
    SmallTopAppBar(
        subTitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        titleText = stringResource(id = R.string.receive_title),
        hamburgerMenuActions = {
            IconButton(
                onClick = onSettings,
                modifier = Modifier.testTag(CommonTag.SETTINGS_TOP_BAR_BUTTON)
            ) {
                Image(
                    painter = painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.ic_hamburger_menu),
                    contentDescription = stringResource(id = R.string.settings_menu_content_description)
                )
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Suppress("LongParameterList")
@Composable
private fun ReceiveContents(
    walletAddresses: WalletAddresses,
    onAddressCopyToClipboard: (String) -> Unit,
    onQrImageShare: (ImageBitmap) -> Unit,
    versionInfo: VersionInfo,
    modifier: Modifier = Modifier,
) {
    val state by remember {
        derivedStateOf {
            listOfNotNull(
                walletAddresses.unified,
                walletAddresses.transparent,
            )
        }
    }

    val pagerState = rememberPagerState { state.size }

    Column(
        modifier = modifier,
    ) {
        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        PagerTabs(
            modifier =
                Modifier
                    .padding(horizontal = ZcashTheme.dimens.screenHorizontalSpacingRegular)
                    .fillMaxWidth(),
            pagerState = pagerState,
            tabs =
                state.mapNotNull {
                    stringResource(
                        when (it) {
                            is WalletAddress.Unified -> R.string.receive_wallet_address_unified
                            is WalletAddress.Sapling -> R.string.receive_wallet_address_sapling
                            is WalletAddress.Transparent -> R.string.receive_wallet_address_transparent
                            else -> return@mapNotNull null
                        }
                    )
                }.toPersistentList(),
        )
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            userScrollEnabled = false
        ) { index ->
            AddressPage(
                walletAddresses = walletAddresses,
                modifier = Modifier.fillMaxSize(),
                walletAddress = state[index],
                versionInfo = versionInfo,
                onAddressCopyToClipboard = onAddressCopyToClipboard,
                onQrImageShare = onQrImageShare,
            )
        }
    }
}

@Suppress("LongMethod", "LongParameterList")
@Composable
private fun AddressPage(
    walletAddresses: WalletAddresses,
    walletAddress: WalletAddress,
    versionInfo: VersionInfo,
    onAddressCopyToClipboard: (String) -> Unit,
    onQrImageShare: (ImageBitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                    vertical = ZcashTheme.dimens.spacingDefault,
                ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QrCode(walletAddress, onAddressCopyToClipboard, onQrImageShare)

        if (versionInfo.isTestnet && walletAddress is WalletAddress.Unified) {
            QrCode(walletAddresses.sapling, onAddressCopyToClipboard, onQrImageShare)
        }
    }
}

@Composable
@Suppress("LongMethod")
private fun ColumnScope.QrCode(
    walletAddress: WalletAddress,
    onAddressCopyToClipboard: (String) -> Unit,
    onQrImageShare: (ImageBitmap) -> Unit,
) {
    val sizePixels = with(LocalDensity.current) { DEFAULT_QR_CODE_SIZE.toPx() }.roundToInt()
    val qrCodeImage =
        remember {
            qrCodeForAddress(
                address = walletAddress.address,
                size = sizePixels
            )
        }

    QrCode(
        qrCodeImage = qrCodeImage,
        onQrImageBitmapShare = onQrImageShare,
        contentDescription =
            when (walletAddress) {
                is WalletAddress.Unified -> stringResource(R.string.receive_unified_content_description)
                is WalletAddress.Sapling -> stringResource(R.string.receive_sapling_content_description)
                is WalletAddress.Transparent -> stringResource(R.string.receive_transparent_content_description)
                else -> ""
            },
        modifier =
            Modifier
                .align(Alignment.CenterHorizontally),
    )

    Text(
        text = walletAddress.address,
        style = ZcashTheme.extendedTypography.addressStyle,
        color = ZcashTheme.colors.textDescription,
        textAlign = TextAlign.Center,
        modifier =
            Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(ZcashTheme.dimens.regularRippleEffectCorner))
                .clickable { onAddressCopyToClipboard(walletAddress.address) }
                .padding(
                    horizontal = ZcashTheme.dimens.spacingLarge,
                    vertical = ZcashTheme.dimens.spacingSmall
                )
                .fillMaxWidth(),
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Reference(
            text = stringResource(id = R.string.receive_copy),
            onClick = { onAddressCopyToClipboard(walletAddress.address) },
            textAlign = TextAlign.Center,
            imageVector = ImageVector.vectorResource(R.drawable.copy),
            imageContentDescription = null,
            modifier =
                Modifier
                    .wrapContentSize()
                    .padding(all = ZcashTheme.dimens.spacingDefault),
        )
        Reference(
            text = stringResource(id = R.string.receive_share),
            onClick = { onQrImageShare(qrCodeImage) },
            textAlign = TextAlign.Center,
            imageVector = ImageVector.vectorResource(R.drawable.share),
            imageContentDescription = null,
            modifier =
                Modifier
                    .wrapContentSize()
                    .padding(all = ZcashTheme.dimens.spacingDefault),
        )
    }
}

private fun qrCodeForAddress(
    address: String,
    size: Int,
): ImageBitmap {
    // In the future, use actual/expect to switch QR code generator implementations for multiplatform

    // Note that our implementation has an extra array copy to BooleanArray, which is a cross-platform
    // representation.  This should have minimal performance impact since the QR code is relatively
    // small and we only generate QR codes infrequently.

    val qrCodePixelArray = JvmQrCodeGenerator.generate(address, size)

    return AndroidQrCodeImageGenerator.generate(qrCodePixelArray, size)
}

@Composable
private fun QrCode(
    contentDescription: String,
    qrCodeImage: ImageBitmap,
    onQrImageBitmapShare: (ImageBitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    Image(
        bitmap = qrCodeImage,
        contentDescription = contentDescription,
        modifier =
            Modifier
                .clickable { onQrImageBitmapShare(qrCodeImage) }
                .then(modifier)
    )
}

private val DEFAULT_QR_CODE_SIZE = 320.dp
