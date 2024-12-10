@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.qrcode.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import cash.z.ecc.android.sdk.model.WalletAddress
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.ZashiBadge
import co.electriccoin.zcash.ui.design.component.ZashiBadgeColors
import co.electriccoin.zcash.ui.design.component.ZashiBottomBar
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.AndroidQrCodeImageGenerator
import co.electriccoin.zcash.ui.design.util.JvmQrCodeGenerator
import co.electriccoin.zcash.ui.screen.qrcode.model.QrCodeState
import co.electriccoin.zcash.ui.screen.qrcode.model.QrCodeType
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

@Composable
@PreviewScreens
private fun QrCodeLoadingPreview() =
    ZcashTheme(forceDarkMode = true) {
        QrCodeView(
            state = QrCodeState.Loading,
            snackbarHostState = SnackbarHostState(),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }

@Composable
@PreviewScreens
private fun ZashiPreview() =
    ZcashTheme(forceDarkMode = false) {
        QrCodeView(
            state =
                QrCodeState.Prepared(
                    qrCodeType = QrCodeType.ZASHI,
                    walletAddress = runBlocking { WalletAddressFixture.unified() },
                    onAddressCopy = {},
                    onQrCodeShare = {},
                    onBack = {},
                ),
            snackbarHostState = SnackbarHostState(),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }

@Composable
@PreviewScreens
private fun KeystonePreview() =
    ZcashTheme(forceDarkMode = false) {
        QrCodeView(
            state =
                QrCodeState.Prepared(
                    qrCodeType = QrCodeType.KEYSTONE,
                    walletAddress = runBlocking { WalletAddressFixture.unified() },
                    onAddressCopy = {},
                    onQrCodeShare = {},
                    onBack = {},
                ),
            snackbarHostState = SnackbarHostState(),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }

@Composable
internal fun QrCodeView(
    state: QrCodeState,
    snackbarHostState: SnackbarHostState,
    topAppBarSubTitleState: TopAppBarSubTitleState,
) {
    when (state) {
        QrCodeState.Loading -> {
            CircularScreenProgressIndicator()
        }
        is QrCodeState.Prepared -> {
            val sizePixels = with(LocalDensity.current) { DEFAULT_QR_CODE_SIZE.toPx() }.roundToInt()
            val qrCodeImage =
                remember {
                    qrCodeForAddress(
                        address = state.walletAddress.address,
                        size = sizePixels
                    )
                }

            BlankBgScaffold(
                topBar = {
                    QrCodeTopAppBar(
                        onBack = state.onBack,
                        subTitleState = topAppBarSubTitleState,
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    QrCodeBottomBar(
                        state = state,
                        qrCodeImage = qrCodeImage
                    )
                }
            ) { paddingValues ->
                QrCodeContents(
                    qrCodeType = state.qrCodeType,
                    walletAddress = state.walletAddress,
                    onAddressCopy = state.onAddressCopy,
                    onQrCodeShare = state.onQrCodeShare,
                    modifier =
                        Modifier.padding(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding()
                        ),
                )
            }
        }
    }
}

@Composable
private fun QrCodeTopAppBar(
    onBack: () -> Unit,
    subTitleState: TopAppBarSubTitleState,
) {
    ZashiSmallTopAppBar(
        subtitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        title = null,
        navigationAction = {
            IconButton(
                onClick = onBack,
                modifier =
                    Modifier
                        .padding(horizontal = ZcashTheme.dimens.spacingDefault)
                        // Making the size bigger by 3.dp so the rounded image corners are not stripped out
                        .size(43.dp),
            ) {
                Image(
                    painter =
                        painterResource(
                            id = co.electriccoin.zcash.ui.design.R.drawable.ic_close_full
                        ),
                    contentDescription = stringResource(id = R.string.qr_code_close_content_description),
                    modifier =
                        Modifier
                            .padding(all = 3.dp)
                )
            }
        },
    )
}

@Composable
private fun QrCodeBottomBar(
    state: QrCodeState.Prepared,
    qrCodeImage: ImageBitmap,
) {
    ZashiBottomBar {
        ZashiButton(
            text = stringResource(id = R.string.qr_code_share_btn),
            icon = R.drawable.ic_share,
            onClick = { state.onQrCodeShare(qrCodeImage) },
            modifier =
                Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingTiny))

        ZashiButton(
            text = stringResource(id = R.string.qr_code_copy_btn),
            icon = R.drawable.ic_copy,
            onClick = { state.onAddressCopy(state.walletAddress.address) },
            colors = ZashiButtonDefaults.secondaryColors(),
            modifier =
                Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
        )
    }
}

@Composable
private fun QrCodeContents(
    qrCodeType: QrCodeType,
    walletAddress: WalletAddress,
    onAddressCopy: (String) -> Unit,
    onQrCodeShare: (ImageBitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ZcashTheme.dimens.screenHorizontalSpacingRegular),
    ) {
        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        when (walletAddress) {
            // We use the same design for the Sapling address for the Testnet app variant
            is WalletAddress.Unified, is WalletAddress.Sapling -> {
                UnifiedQrCodePanel(qrCodeType, walletAddress, onAddressCopy, onQrCodeShare)
            }
            is WalletAddress.Transparent -> {
                TransparentQrCodePanel(qrCodeType, walletAddress, onAddressCopy, onQrCodeShare)
            }
            else -> {
                error("Unsupported address type: $walletAddress")
            }
        }
    }
}

@Composable
@Suppress("LongMethod")
fun UnifiedQrCodePanel(
    qrCodeType: QrCodeType,
    walletAddress: WalletAddress,
    onAddressCopy: (String) -> Unit,
    onQrCodeShare: (ImageBitmap) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedAddress by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .padding(vertical = ZcashTheme.dimens.spacingDefault),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QrCode(
            walletAddress = walletAddress,
            onQrImageShare = onQrCodeShare,
            modifier =
                Modifier
                    .padding(horizontal = 24.dp),
            qrCodeType = qrCodeType,
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingUpLarge))

        ZashiBadge(
            text = stringResource(id = R.string.qr_code_privacy_level_shielded),
            leadingIconVector = painterResource(id = R.drawable.ic_solid_check),
            colors =
                ZashiBadgeColors(
                    border = ZashiColors.Utility.Purple.utilityPurple200,
                    text = ZashiColors.Utility.Purple.utilityPurple700,
                    container = ZashiColors.Utility.Purple.utilityPurple50,
                )
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Text(
            text =
                when (walletAddress) {
                    is WalletAddress.Unified ->
                        when (qrCodeType) {
                            QrCodeType.ZASHI -> stringResource(R.string.qr_code_wallet_address_shielded)
                            QrCodeType.KEYSTONE -> stringResource(R.string.qr_code_wallet_address_shielded_keystone)
                        }
                    is WalletAddress.Sapling ->
                        when (qrCodeType) {
                            QrCodeType.ZASHI -> stringResource(id = R.string.qr_code_wallet_address_sapling)
                            QrCodeType.KEYSTONE -> stringResource(id = R.string.qr_code_wallet_address_sapling_keystone)
                        }
                    else -> error("Unsupported address type: $walletAddress")
                },
            color = ZashiColors.Text.textPrimary,
            style = ZashiTypography.textXl,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        @OptIn(ExperimentalFoundationApi::class)
        Text(
            text = walletAddress.address,
            color = ZashiColors.Text.textTertiary,
            style = ZashiTypography.textSm,
            textAlign = TextAlign.Center,
            maxLines =
                if (expandedAddress) {
                    Int.MAX_VALUE
                } else {
                    2
                },
            overflow = TextOverflow.Ellipsis,
            modifier =
                Modifier
                    .animateContentSize()
                    .combinedClickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { expandedAddress = !expandedAddress },
                        onLongClick = { onAddressCopy(walletAddress.address) }
                    )
        )
    }
}

@Composable
@Suppress("LongMethod")
fun TransparentQrCodePanel(
    qrCodeType: QrCodeType,
    walletAddress: WalletAddress,
    onAddressCopy: (String) -> Unit,
    onQrCodeShare: (ImageBitmap) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedAddress by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .padding(vertical = ZcashTheme.dimens.spacingDefault),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QrCode(
            walletAddress = walletAddress,
            onQrImageShare = onQrCodeShare,
            modifier =
                Modifier
                    .padding(horizontal = 24.dp),
            qrCodeType = qrCodeType,
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingUpLarge))

        ZashiBadge(
            text = stringResource(id = R.string.qr_code_privacy_level_transparent),
            leadingIconVector = painterResource(id = R.drawable.ic_alert_circle),
            colors =
                ZashiBadgeColors(
                    border = ZashiColors.Utility.WarningYellow.utilityOrange200,
                    text = ZashiColors.Utility.WarningYellow.utilityOrange700,
                    container = ZashiColors.Utility.WarningYellow.utilityOrange50,
                )
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Text(
            text = stringResource(id = R.string.qr_code_wallet_address_transparent),
            color = ZashiColors.Text.textPrimary,
            style = ZashiTypography.textXl,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        @OptIn(ExperimentalFoundationApi::class)
        Text(
            text = walletAddress.address,
            color = ZashiColors.Text.textTertiary,
            style = ZashiTypography.textSm,
            textAlign = TextAlign.Center,
            maxLines =
                if (expandedAddress) {
                    Int.MAX_VALUE
                } else {
                    2
                },
            overflow = TextOverflow.Ellipsis,
            modifier =
                Modifier
                    .animateContentSize()
                    .combinedClickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { expandedAddress = !expandedAddress },
                        onLongClick = { onAddressCopy(walletAddress.address) }
                    )
        )
    }
}

@Composable
private fun ColumnScope.QrCode(
    qrCodeType: QrCodeType,
    walletAddress: WalletAddress,
    onQrImageShare: (ImageBitmap) -> Unit,
    modifier: Modifier = Modifier
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
            stringResource(
                when (walletAddress) {
                    is WalletAddress.Unified -> R.string.qr_code_unified_content_description
                    is WalletAddress.Sapling -> R.string.qr_code_sapling_content_description
                    is WalletAddress.Transparent -> R.string.qr_code_transparent_content_description
                    else -> error("Unsupported address type: $walletAddress")
                }
            ),
        modifier =
            modifier
                .align(Alignment.CenterHorizontally)
                .border(
                    border =
                        BorderStroke(
                            width = 1.dp,
                            color = ZashiColors.Surfaces.strokePrimary
                        ),
                    shape = RoundedCornerShape(ZashiDimensions.Radius.radius4xl)
                )
                .background(
                    if (isSystemInDarkTheme()) {
                        ZashiColors.Surfaces.bgAlt
                    } else {
                        ZashiColors.Surfaces.bgPrimary
                    },
                    RoundedCornerShape(ZashiDimensions.Radius.radius4xl)
                )
                .padding(all = 12.dp),
        qrCodeType = qrCodeType
    )
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
    qrCodeType: QrCodeType,
    contentDescription: String,
    qrCodeImage: ImageBitmap,
    onQrImageBitmapShare: (ImageBitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { onQrImageBitmapShare(qrCodeImage) },
                )
                .then(modifier)
    ) {
        Image(
            bitmap = qrCodeImage,
            contentDescription = contentDescription,
        )

        Image(
            modifier = Modifier.size(64.dp),
            painter =
                when (qrCodeType) {
                    QrCodeType.ZASHI -> painterResource(id = R.drawable.logo_zec_fill_stroke)
                    QrCodeType.KEYSTONE ->
                        painterResource(
                            id =
                                co.electriccoin.zcash.ui.design.R.drawable
                                    .ic_item_keystone_qr
                        )
                },
            contentDescription = contentDescription,
        )
    }
}

private val DEFAULT_QR_CODE_SIZE = 320.dp
