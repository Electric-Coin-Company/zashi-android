@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.qrcode

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import cash.z.ecc.android.sdk.model.WalletAddress
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.OldZashiBottomBar
import co.electriccoin.zcash.ui.design.component.ZashiBadge
import co.electriccoin.zcash.ui.design.component.ZashiBadgeColors
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiQr
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.runBlocking

@Composable
@PreviewScreens
private fun QrCodeLoadingPreview() =
    ZcashTheme(forceDarkMode = true) {
        QrCodeView(
            state = QrCodeState.Loading,
            snackbarHostState = SnackbarHostState(),
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
        )
    }

@Composable
internal fun QrCodeView(
    state: QrCodeState,
    snackbarHostState: SnackbarHostState,
) {
    when (state) {
        QrCodeState.Loading -> {
            CircularScreenProgressIndicator()
        }
        is QrCodeState.Prepared -> {
            BlankBgScaffold(
                topBar = {
                    QrCodeTopAppBar(
                        onBack = state.onBack,
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    QrCodeBottomBar(
                        state = state,
                    )
                }
            ) { paddingValues ->
                QrCodeContents(
                    state = state,
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
) {
    ZashiSmallTopAppBar(
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
) {
    OldZashiBottomBar {
        ZashiButton(
            text = stringResource(id = R.string.qr_code_share_btn),
            icon = R.drawable.ic_share,
            onClick = { state.onQrCodeShare(state.walletAddress.address) },
            modifier =
                Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingTiny))

        ZashiButton(
            text = stringResource(id = R.string.qr_code_copy_btn),
            icon = R.drawable.ic_qr_copy,
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
    state: QrCodeState.Prepared,
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

        when (state.walletAddress) {
            // We use the same design for the Sapling address for the Testnet app variant
            is WalletAddress.Unified, is WalletAddress.Sapling -> {
                UnifiedQrCodePanel(state)
            }
            is WalletAddress.Transparent -> {
                TransparentQrCodePanel(state)
            }
            else -> {
                error("Unsupported address type: ${state.walletAddress}")
            }
        }
    }
}

@Composable
@Suppress("LongMethod")
fun UnifiedQrCodePanel(
    state: QrCodeState.Prepared,
    modifier: Modifier = Modifier
) {
    var expandedAddress by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .padding(vertical = ZcashTheme.dimens.spacingDefault),
        horizontalAlignment = CenterHorizontally
    ) {
        QrCode(
            state = state,
            modifier =
                Modifier
                    .padding(horizontal = 24.dp),
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
                when (state.walletAddress) {
                    is WalletAddress.Unified ->
                        when (state.qrCodeType) {
                            QrCodeType.ZASHI -> stringResource(R.string.qr_code_wallet_address_shielded)
                            QrCodeType.KEYSTONE -> stringResource(R.string.qr_code_wallet_address_shielded_keystone)
                        }
                    is WalletAddress.Sapling ->
                        when (state.qrCodeType) {
                            QrCodeType.ZASHI -> stringResource(id = R.string.qr_code_wallet_address_sapling)
                            QrCodeType.KEYSTONE -> stringResource(id = R.string.qr_code_wallet_address_sapling_keystone)
                        }
                    else -> error("Unsupported address type: ${state.walletAddress}")
                },
            color = ZashiColors.Text.textPrimary,
            style = ZashiTypography.textXl,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        @OptIn(ExperimentalFoundationApi::class)
        Text(
            text = state.walletAddress.address,
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
                        onLongClick = { state.onAddressCopy(state.walletAddress.address) }
                    )
        )
    }
}

@Composable
@Suppress("LongMethod")
fun TransparentQrCodePanel(
    state: QrCodeState.Prepared,
    modifier: Modifier = Modifier
) {
    var expandedAddress by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .padding(vertical = ZcashTheme.dimens.spacingDefault),
        horizontalAlignment = CenterHorizontally
    ) {
        QrCode(
            state = state,
            modifier =
                Modifier
                    .padding(horizontal = 24.dp),
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
            text = state.walletAddress.address,
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
                        onLongClick = { state.onAddressCopy(state.walletAddress.address) }
                    )
        )
    }
}

@Composable
private fun ColumnScope.QrCode(
    state: QrCodeState.Prepared,
    modifier: Modifier = Modifier
) {
    ZashiQr(
        state =
            state.toQrState(
                contentDescription =
                    stringRes(
                        when (state.walletAddress) {
                            is WalletAddress.Unified -> R.string.qr_code_unified_content_description
                            is WalletAddress.Sapling -> R.string.qr_code_sapling_content_description
                            is WalletAddress.Transparent -> R.string.qr_code_transparent_content_description
                            else -> error("Unsupported address type: ${state.walletAddress}")
                        }
                    ),
                centerImageResId =
                    when (state.qrCodeType) {
                        QrCodeType.ZASHI ->
                            if (state.walletAddress is WalletAddress.Transparent) {
                                R.drawable.ic_zec_qr_transparent
                            } else {
                                R.drawable.ic_zec_qr_shielded
                            }
                        QrCodeType.KEYSTONE -> co.electriccoin.zcash.ui.design.R.drawable.ic_item_keystone_qr
                    }
            ),
        modifier = modifier.align(CenterHorizontally),
    )
}
