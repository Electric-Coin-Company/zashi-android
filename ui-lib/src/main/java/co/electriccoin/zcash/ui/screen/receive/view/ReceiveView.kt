package co.electriccoin.zcash.ui.screen.receive.view

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensionsInternal
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import co.electriccoin.zcash.ui.screen.send.ext.abbreviated
import kotlinx.coroutines.runBlocking

@Preview
@Composable
private fun ReceivePreview() =
    ZcashTheme(forceDarkMode = false) {
        Receive(
            walletAddresses = runBlocking { WalletAddressesFixture.new() },
            snackbarHostState = SnackbarHostState(),
            onSettings = {},
            onAddrCopyToClipboard = {},
            onQrCode = {},
            onRequest = {},
            versionInfo = VersionInfoFixture.new(),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }

@Preview
@Composable
private fun ReceiveDarkPreview() =
    ZcashTheme(forceDarkMode = true) {
        Receive(
            walletAddresses = runBlocking { WalletAddressesFixture.new() },
            snackbarHostState = SnackbarHostState(),
            onSettings = {},
            onAddrCopyToClipboard = {},
            onQrCode = {},
            onRequest = {},
            versionInfo = VersionInfoFixture.new(),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }

@Suppress("LongParameterList")
@Composable
fun Receive(
    walletAddresses: WalletAddresses?,
    snackbarHostState: SnackbarHostState,
    onSettings: () -> Unit,
    onAddrCopyToClipboard: (String) -> Unit,
    onQrCode: (WalletAddress) -> Unit,
    onRequest: (WalletAddress) -> Unit,
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
        if (null == walletAddresses) {
            CircularScreenProgressIndicator()
        } else {
            ReceiveContents(
                walletAddresses = walletAddresses,
                onAddressCopyToClipboard = onAddrCopyToClipboard,
                onQrCode = onQrCode,
                onRequest = onRequest,
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
                modifier = Modifier
                    .padding(horizontal = ZcashTheme.dimens.spacingDefault)
                    // Making the size bigger by 3.dp so the rounded image corners are not stripped out
                    .size(43.dp)
                    .testTag(CommonTag.SETTINGS_TOP_BAR_BUTTON)
            ) {
                Image(
                    painter = painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.ic_hamburger_menu_with_bg),
                    contentDescription = stringResource(id = R.string.settings_menu_content_description),
                    modifier = Modifier.padding(all = 3.dp)
                )
            }
        },
    )
}

@Suppress("LongParameterList")
@Composable
private fun ReceiveContents(
    walletAddresses: WalletAddresses,
    onAddressCopyToClipboard: (String) -> Unit,
    onQrCode: (WalletAddress) -> Unit,
    onRequest: (WalletAddress) -> Unit,
    versionInfo: VersionInfo,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        ShieldedAddressPanel(
            walletAddress = walletAddresses.unified,
            onAddressCopyToClipboard = onAddressCopyToClipboard,
            onQrCode = onQrCode,
            onRequest = onRequest,
        )


        if (versionInfo.isTestnet) {
            Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

            SaplingAddressPanel(
                walletAddress = walletAddresses.sapling,
                onAddressCopyToClipboard = onAddressCopyToClipboard,
                onQrImage = onQrCode,
                onRequest = onRequest,
            )
        }

        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        TransparentAddressPanel(
            walletAddress = walletAddresses.transparent,
            onAddressCopyToClipboard = onAddressCopyToClipboard,
            onQrImage = onQrCode,
            onRequest = onRequest,
        )
    }
}

@Composable
private fun ShieldedAddressPanel(
    walletAddress: WalletAddress,
    onAddressCopyToClipboard: (String) -> Unit,
    onQrCode: (WalletAddress) -> Unit,
    onRequest: (WalletAddress) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
        modifier
            .wrapContentHeight()
            .padding(
                horizontal = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                vertical = ZcashTheme.dimens.spacingDefault,
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(all = ZcashTheme.dimens.spacingDefault)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_zec_round_full),
                contentDescription = null
            )

            Spacer(Modifier.width(ZcashTheme.dimens.spacingDefault))

            Column {
                Text(text = stringResource(id = R.string.receive_wallet_address_shielded))
                Text(text = walletAddress.abbreviated())
            }

            Spacer(Modifier.width(ZcashTheme.dimens.spacingDefault))

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.ic_check_shielded_solid),
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    ZashiColors.Utility.Purple.utilityPurple50,
                    RoundedCornerShape(ZashiDimensionsInternal.Radius.radiusXl)
                )
                .padding(all = ZcashTheme.dimens.spacingDefault)
        ) {
            ReceiveIconButton(
                containerColor = ZashiColors.Utility.Purple.utilityPurple100,
                contentColor = ZashiColors.Utility.Purple.utilityPurple800,
                iconPainter = painterResource(id = R.drawable.ic_copy_shielded),
                onClick = { onAddressCopyToClipboard(walletAddress.address) },
                text = stringResource(id = R.string.receive_copy),
            )

            Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingDefault))

            IconButton(
                onClick = { onQrCode(walletAddress) },
                colors = IconButtonColors(
                    containerColor = ZashiColors.Utility.Purple.utilityPurple50,
                    contentColor = ZashiColors.Utility.Purple.utilityPurple50,
                    disabledContentColor = Color.Unspecified,
                    disabledContainerColor = Color.Unspecified,
                ),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_qr_code_shielded),
                    contentDescription = stringResource(id = R.string.receive_qr_code)
                )
                Text(text = stringResource(id = R.string.receive_copy))
            }
            IconButton(
                onClick = { onRequest(walletAddress) },
                colors = IconButtonColors(
                    containerColor = ZashiColors.Utility.Purple.utilityPurple50,
                    contentColor = ZashiColors.Utility.Purple.utilityPurple50,
                    disabledContentColor = Color.Unspecified,
                    disabledContainerColor = Color.Unspecified,
                ),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_request_shielded),
                    contentDescription = stringResource(id = R.string.receive_request)
                )
                Text(text = stringResource(id = R.string.receive_request))
            }
        }
    }
}

@Composable
private fun SaplingAddressPanel(
    walletAddress: WalletAddress,
    onAddressCopyToClipboard: (String) -> Unit,
    onQrImage: (WalletAddress) -> Unit,
    onRequest: (WalletAddress) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
        modifier
            .padding(
                horizontal = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                vertical = ZcashTheme.dimens.spacingDefault,
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {}
}

@Composable
private fun TransparentAddressPanel(
    walletAddress: WalletAddress,
    onAddressCopyToClipboard: (String) -> Unit,
    onQrImage: (WalletAddress) -> Unit,
    onRequest: (WalletAddress) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
        modifier
            .padding(
                horizontal = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                vertical = ZcashTheme.dimens.spacingDefault,
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {}
}

@Composable
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
        modifier = modifier
            .clip(RoundedCornerShape(ZashiDimensionsInternal.Radius.radiusXl))
            .clickable { onClick() }
            .background(containerColor, RoundedCornerShape(ZashiDimensionsInternal.Radius.radiusXl))
            .padding(ZcashTheme.dimens.spacingSmall)

    ) {
        Icon(
            painter = iconPainter,
            contentDescription = text,
            tint = contentColor
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        Text(
            text = text,
            color = contentColor
        )
    }
}
