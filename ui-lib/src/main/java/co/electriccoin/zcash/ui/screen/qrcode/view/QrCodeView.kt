package co.electriccoin.zcash.ui.screen.qrcode.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import cash.z.ecc.android.sdk.model.WalletAddress
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensionsInternal
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.screen.qrcode.model.QrCodeState
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressType
import kotlinx.coroutines.runBlocking

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

@Preview
@Composable
private fun QrCodePreview() =
    ZcashTheme(forceDarkMode = false) {
        QrCodeView(
            state = QrCodeState.Prepared(
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
            BlankBgScaffold(
                topBar = {
                    QrCodeTopAppBar(
                        onBack = state.onBack,
                        subTitleState = topAppBarSubTitleState,
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
            ) { paddingValues ->
                QrCodeContents(
                    walletAddresses = state.walletAddress,
                    onAddressCopy = state.onAddressCopy,
                    onQrCodeShare = state.onQrCodeShare,
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
                modifier = Modifier
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
                    modifier = Modifier
                        .padding(all = 3.dp)
                )
            }
        },
    )
}

@Composable
@Suppress("LongParameterList")
private fun QrCodeContents(
    walletAddresses: WalletAddress,
    onAddressCopy: (String) -> Unit,
    onQrCodeShare: (String) -> Unit,
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

        //todo
    }
}
