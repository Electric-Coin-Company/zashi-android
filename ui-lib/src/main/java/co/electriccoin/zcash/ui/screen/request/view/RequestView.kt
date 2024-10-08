@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.request.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.ext.toZec
import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.ZashiBottomBar
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.request.model.Request
import co.electriccoin.zcash.ui.screen.request.model.RequestState
import kotlinx.coroutines.runBlocking

@Composable
@PreviewScreens
private fun RequestLoadingPreview() =
    ZcashTheme(forceDarkMode = true) {
        RequestView(
            state = RequestState.Loading,
            snackbarHostState = SnackbarHostState(),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }

@Composable
@PreviewScreens
private fun RequestPreview() =
    ZcashTheme(forceDarkMode = false) {
        RequestView(
            state =
                RequestState.Prepared(
                    walletAddress = runBlocking { WalletAddressFixture.unified() },
                    onQrCodeShare = {},
                    onRequest = {},
                    onAmount = {},
                    onBack = {},
                ),
            snackbarHostState = SnackbarHostState(),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }

@Composable
internal fun RequestView(
    state: RequestState,
    snackbarHostState: SnackbarHostState,
    topAppBarSubTitleState: TopAppBarSubTitleState,
) {
    when (state) {
        RequestState.Loading -> {
            CircularScreenProgressIndicator()
        }
        is RequestState.Prepared -> {
            BlankBgScaffold(
                topBar = {
                    RequestTopAppBar(
                        onBack = state.onBack,
                        subTitleState = topAppBarSubTitleState,
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    RequestBottomBar(state = state)
                }
            ) { paddingValues ->
                RequestContents(
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
private fun RequestTopAppBar(
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
                    contentDescription = stringResource(id = R.string.request_back_content_description),
                    modifier =
                        Modifier
                            .padding(all = 3.dp)
                )
            }
        },
    )
}

@Composable
private fun RequestBottomBar(
    state: RequestState.Prepared,
) {
    ZashiBottomBar {
        ZashiButton(
            text = stringResource(id = R.string.request_share_btn),
            leadingIcon = painterResource(R.drawable.ic_share),
            onClick = {
                state.onRequest(
                    Request(
                        amount = Zatoshi(1),
                        memo = "Test memo",
                        recipientAddress =
                        runBlocking {
                            WalletAddress.Unified.new("u1kpy0mhprcx64400thhj9xfp862j2dhrnl7nx37c8y8pn8l58n7t2pj3vy58zg37lr4zkfwp8h868ra8wjvmrpeuqff8r6h3lzdyvdv7ly04dwkxu88mu7ze49xx7we08suux6350m2z9eljtt5a75dscc56vckhn9u0uwvdry00mehs82wjfml4fmd28e64n5ruqltyn0e6nqr726vt")
                        }
                    )
                )
            },
            modifier =
                Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
        )
    }
}

@Composable
private fun RequestContents(
    state: RequestState.Prepared,
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
    }
}
