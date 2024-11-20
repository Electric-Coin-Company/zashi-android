package co.electriccoin.zcash.ui.screen.receive.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.fixture.WalletAddressesFixture
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.WalletAddresses
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.ZashiMainTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiMainTopAppBarState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.fixture.ZashiMainTopAppBarStateFixture
import co.electriccoin.zcash.ui.screen.receive.ext.toReceiveAddressType
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressType
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveState
import co.electriccoin.zcash.ui.screen.send.ext.abbreviated
import kotlinx.coroutines.runBlocking

@Composable
@PreviewScreens
private fun ReceiveLoadingPreview() =
    ZcashTheme(forceDarkMode = true) {
        ReceiveView(
            state = ReceiveState.Loading,
            snackbarHostState = SnackbarHostState(),
            zashiMainTopAppBarState = ZashiMainTopAppBarStateFixture.new()
        )
    }

@Preview
@Composable
private fun ReceivePreview() =
    ZcashTheme(forceDarkMode = false) {
        ReceiveView(
            state =
                ReceiveState.Prepared(
                    walletAddresses = runBlocking { WalletAddressesFixture.new() },
                    isTestnet = false,
                    onAddressCopy = {},
                    onQrCode = {},
                    onSettings = {},
                    onRequest = {},
                ),
            snackbarHostState = SnackbarHostState(),
            zashiMainTopAppBarState = ZashiMainTopAppBarStateFixture.new()
        )
    }

@Composable
internal fun ReceiveView(
    state: ReceiveState,
    snackbarHostState: SnackbarHostState,
    zashiMainTopAppBarState: ZashiMainTopAppBarState,
) {
    when (state) {
        ReceiveState.Loading -> {
            CircularScreenProgressIndicator()
        }
        is ReceiveState.Prepared -> {
            BlankBgScaffold(
                topBar = {
                    ZashiMainTopAppBar(zashiMainTopAppBarState)
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
            ) { paddingValues ->
                ReceiveContents(
                    walletAddresses = state.walletAddresses,
                    onAddressCopyToClipboard = state.onAddressCopy,
                    onQrCode = state.onQrCode,
                    onRequest = state.onRequest,
                    isTestnet = state.isTestnet,
                    modifier =
                        Modifier.padding(
                            top = paddingValues.calculateTopPadding()
                            // We intentionally do not set the rest paddings, those are set by the underlying composable
                        ),
                )
            }
        }
    }
}

@Composable
@Suppress("LongParameterList")
private fun ReceiveContents(
    walletAddresses: WalletAddresses,
    onAddressCopyToClipboard: (String) -> Unit,
    onQrCode: (ReceiveAddressType) -> Unit,
    onRequest: (ReceiveAddressType) -> Unit,
    isTestnet: Boolean,
    modifier: Modifier = Modifier,
) {
    var expandedAddressPanel by rememberSaveable { mutableStateOf(ReceiveAddressType.Unified) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(all = ZcashTheme.dimens.spacingSmall),
    ) {
        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        Text(
            text = stringResource(id = R.string.receive_header),
            color = ZashiColors.Text.textPrimary,
            style = ZashiTypography.header5,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = ZcashTheme.dimens.spacingDefault)
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingSmall))

        Text(
            text = stringResource(id = R.string.receive_prioritize_shielded),
            color = ZashiColors.Text.textSecondary,
            style = ZashiTypography.textMd,
            modifier = Modifier.padding(horizontal = ZcashTheme.dimens.spacingDefault)
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingLarge))

        UnifiedAddressPanel(
            walletAddress = walletAddresses.unified,
            onAddressCopyToClipboard = onAddressCopyToClipboard,
            onQrCode = onQrCode,
            onRequest = onRequest,
            expanded = expandedAddressPanel == ReceiveAddressType.Unified,
            onExpand = { expandedAddressPanel = ReceiveAddressType.Unified }
        )

        if (isTestnet) {
            Spacer(Modifier.height(ZcashTheme.dimens.spacingSmall))

            SaplingAddressPanel(
                walletAddress = walletAddresses.sapling,
                onAddressCopyToClipboard = onAddressCopyToClipboard,
                onQrCode = onQrCode,
                onRequest = onRequest,
                expanded = expandedAddressPanel == ReceiveAddressType.Sapling,
                onExpand = { expandedAddressPanel = ReceiveAddressType.Sapling }
            )
        }

        Spacer(Modifier.height(ZcashTheme.dimens.spacingSmall))

        TransparentAddressPanel(
            walletAddress = walletAddresses.transparent,
            onAddressCopyToClipboard = onAddressCopyToClipboard,
            onQrCode = onQrCode,
            onRequest = onRequest,
            expanded = expandedAddressPanel == ReceiveAddressType.Transparent,
            onExpand = { expandedAddressPanel = ReceiveAddressType.Transparent }
        )
    }
}

@Composable
@Suppress("LongParameterList", "LongMethod")
private fun UnifiedAddressPanel(
    walletAddress: WalletAddress,
    onAddressCopyToClipboard: (String) -> Unit,
    onQrCode: (ReceiveAddressType) -> Unit,
    onRequest: (ReceiveAddressType) -> Unit,
    expanded: Boolean,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .wrapContentHeight()
                .background(
                    ZashiColors.Utility.Purple.utilityPurple50,
                    RoundedCornerShape(ZashiDimensions.Radius.radius3xl)
                )
                .clip(RoundedCornerShape(ZashiDimensions.Radius.radius3xl))
                .clickable { onExpand() }
                .padding(all = ZcashTheme.dimens.spacingLarge)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.ic_zec_round_full),
                contentDescription = null
            )

            Spacer(Modifier.width(ZcashTheme.dimens.spacingDefault))

            Column {
                Text(
                    text = stringResource(id = R.string.receive_wallet_address_shielded),
                    color = ZashiColors.Text.textPrimary,
                    style = ZashiTypography.textMd,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(ZcashTheme.dimens.spacingTiny))

                Text(
                    text = walletAddress.abbreviated(),
                    color = ZashiColors.Text.textTertiary,
                    style = ZashiTypography.textSm
                )
            }

            Spacer(Modifier.width(ZcashTheme.dimens.spacingSmall))

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.ic_check_shielded_solid),
                contentDescription = null
            )
        }

        AnimatedVisibility(visible = expanded) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = ZcashTheme.dimens.spacingDefault)
            ) {
                ReceiveIconButton(
                    containerColor = ZashiColors.Utility.Purple.utilityPurple100,
                    contentColor = ZashiColors.Utility.Purple.utilityPurple800,
                    iconPainter = painterResource(id = R.drawable.ic_copy_shielded),
                    onClick = { onAddressCopyToClipboard(walletAddress.address) },
                    text = stringResource(id = R.string.receive_copy),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingSmall))

                ReceiveIconButton(
                    containerColor = ZashiColors.Utility.Purple.utilityPurple100,
                    contentColor = ZashiColors.Utility.Purple.utilityPurple800,
                    iconPainter = painterResource(id = R.drawable.ic_qr_code_shielded),
                    onClick = { onQrCode(walletAddress.toReceiveAddressType()) },
                    text = stringResource(id = R.string.receive_qr_code),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingSmall))

                ReceiveIconButton(
                    containerColor = ZashiColors.Utility.Purple.utilityPurple100,
                    contentColor = ZashiColors.Utility.Purple.utilityPurple800,
                    iconPainter = painterResource(id = R.drawable.ic_request_shielded),
                    onClick = { onRequest(walletAddress.toReceiveAddressType()) },
                    text = stringResource(id = R.string.receive_request),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
@Suppress("LongParameterList", "LongMethod")
private fun SaplingAddressPanel(
    walletAddress: WalletAddress,
    onAddressCopyToClipboard: (String) -> Unit,
    onQrCode: (ReceiveAddressType) -> Unit,
    onRequest: (ReceiveAddressType) -> Unit,
    expanded: Boolean,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .wrapContentHeight()
                .background(
                    ZashiColors.Utility.Gray.utilityGray50,
                    RoundedCornerShape(ZashiDimensions.Radius.radius3xl)
                )
                .clip(RoundedCornerShape(ZashiDimensions.Radius.radius3xl))
                .clickable { onExpand() }
                .padding(all = ZcashTheme.dimens.spacingLarge),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.ic_zec_round_stroke),
                contentDescription = null
            )

            Spacer(Modifier.width(ZcashTheme.dimens.spacingDefault))

            Column {
                Text(
                    text = stringResource(id = R.string.receive_wallet_address_sapling),
                    color = ZashiColors.Text.textPrimary,
                    style = ZashiTypography.textMd,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(ZcashTheme.dimens.spacingTiny))

                Text(
                    text = walletAddress.abbreviated(),
                    color = ZashiColors.Text.textTertiary,
                    style = ZashiTypography.textSm
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = ZcashTheme.dimens.spacingDefault)
            ) {
                ReceiveIconButton(
                    containerColor = ZashiColors.Surfaces.bgTertiary,
                    contentColor = ZashiColors.Text.textPrimary,
                    iconPainter = painterResource(id = R.drawable.ic_copy_other),
                    onClick = { onAddressCopyToClipboard(walletAddress.address) },
                    text = stringResource(id = R.string.receive_copy),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingSmall))

                ReceiveIconButton(
                    containerColor = ZashiColors.Surfaces.bgTertiary,
                    contentColor = ZashiColors.Text.textPrimary,
                    iconPainter = painterResource(id = R.drawable.ic_qr_code_other),
                    onClick = { onQrCode(walletAddress.toReceiveAddressType()) },
                    text = stringResource(id = R.string.receive_qr_code),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingSmall))

                ReceiveIconButton(
                    containerColor = ZashiColors.Surfaces.bgTertiary,
                    contentColor = ZashiColors.Text.textPrimary,
                    iconPainter = painterResource(id = R.drawable.ic_request_other),
                    onClick = { onRequest(walletAddress.toReceiveAddressType()) },
                    text = stringResource(id = R.string.receive_request),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
@Suppress("LongParameterList", "LongMethod")
private fun TransparentAddressPanel(
    walletAddress: WalletAddress,
    onAddressCopyToClipboard: (String) -> Unit,
    onQrCode: (ReceiveAddressType) -> Unit,
    onRequest: (ReceiveAddressType) -> Unit,
    expanded: Boolean,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .wrapContentHeight()
                .background(
                    ZashiColors.Utility.Gray.utilityGray50,
                    RoundedCornerShape(ZashiDimensions.Radius.radius3xl)
                )
                .clip(RoundedCornerShape(ZashiDimensions.Radius.radius3xl))
                .clickable { onExpand() }
                .padding(all = ZcashTheme.dimens.spacingLarge),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.ic_zec_round_stroke),
                contentDescription = null
            )

            Spacer(Modifier.width(ZcashTheme.dimens.spacingDefault))

            Column {
                Text(
                    text = stringResource(id = R.string.receive_wallet_address_transparent),
                    color = ZashiColors.Text.textPrimary,
                    style = ZashiTypography.textMd,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(ZcashTheme.dimens.spacingTiny))

                Text(
                    text = walletAddress.abbreviated(),
                    color = ZashiColors.Text.textTertiary,
                    style = ZashiTypography.textSm
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = ZcashTheme.dimens.spacingDefault)
            ) {
                ReceiveIconButton(
                    containerColor = ZashiColors.Surfaces.bgTertiary,
                    contentColor = ZashiColors.Text.textPrimary,
                    iconPainter = painterResource(id = R.drawable.ic_copy_other),
                    onClick = { onAddressCopyToClipboard(walletAddress.address) },
                    text = stringResource(id = R.string.receive_copy),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingSmall))

                ReceiveIconButton(
                    containerColor = ZashiColors.Surfaces.bgTertiary,
                    contentColor = ZashiColors.Text.textPrimary,
                    iconPainter = painterResource(id = R.drawable.ic_qr_code_other),
                    onClick = { onQrCode(walletAddress.toReceiveAddressType()) },
                    text = stringResource(id = R.string.receive_qr_code),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingSmall))

                ReceiveIconButton(
                    containerColor = ZashiColors.Surfaces.bgTertiary,
                    contentColor = ZashiColors.Text.textPrimary,
                    iconPainter = painterResource(id = R.drawable.ic_request_other),
                    onClick = { onRequest(walletAddress.toReceiveAddressType()) },
                    text = stringResource(id = R.string.receive_request),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
@Suppress("LongParameterList")
private fun ReceiveIconButton(
    containerColor: Color,
    contentColor: Color,
    iconPainter: Painter,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .background(containerColor, RoundedCornerShape(ZashiDimensions.Radius.radiusXl))
                .clip(RoundedCornerShape(ZashiDimensions.Radius.radiusXl))
                .clickable { onClick() }
                .padding(ZcashTheme.dimens.spacingMid)
    ) {
        Icon(
            painter = iconPainter,
            contentDescription = text,
            tint = contentColor
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingTiny))

        Text(
            text = text,
            color = contentColor,
            style = ZashiTypography.textSm,
            fontWeight = FontWeight.Medium
        )
    }
}
