@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.request.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.ZashiBottomBar
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.screen.request.model.Request
import co.electriccoin.zcash.ui.screen.request.model.RequestState

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
                RequestState.Amount(
                    onAmount = {},
                    onBack = {},
                    onDone = {},
                    request = Request(
                        amount = Zatoshi(0),
                        memo = "",
                    )
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
        title = stringResource(id = R.string.request_title),
        navigationAction = {
            ZashiTopAppBarBackNavigation(onBack = onBack)
        },
    )
}

@Composable
private fun RequestBottomBar(
    state: RequestState.Prepared,
    modifier: Modifier = Modifier,
) {
    val btnModifier = modifier
        .padding(horizontal = 24.dp)
        .fillMaxWidth()

    ZashiBottomBar {
        when (state) {
            is RequestState.Amount -> {
                ZashiButton(
                    text = stringResource(id = R.string.request_amount_btn),
                    onClick = { state.onDone() },
                    modifier = btnModifier
                )
            }
            is RequestState.Memo -> {
                ZashiButton(
                    text = stringResource(id = R.string.request_memo_btn),
                    onClick = { state.onDone() },
                    modifier = btnModifier
                )
            }
            is RequestState.QrCode -> {
                ZashiButton(
                    text = stringResource(id = R.string.request_share_btn),
                    leadingIcon = painterResource(R.drawable.ic_share),
                    onClick = { state.onDone() },
                    modifier = btnModifier
                )
            }
        }
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
        when (state) {
            is RequestState.Amount -> {
                RequestAmountView(state = state)
            }
            is RequestState.Memo -> {
                RequestMemoView(state = state)
            }
            is RequestState.QrCode -> {
                RequestQrCodeView(state = state)
            }
        }
    }
}

@Composable
private fun RequestAmountView(
    state: RequestState.Amount,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.wrapContentWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_alert_outline),
                colorFilter = ColorFilter.tint(ZashiColors.Utility.WarningYellow.utilityOrange700),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = stringResource(id = R.string.request_amount_invalid),
                color = ZashiColors.Utility.WarningYellow.utilityOrange700,
                style = ZashiTypography.textSm,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.wrapContentWidth()
            )
        }
    }
}

@Composable
private fun RequestQrCodeView(
    state: RequestState.QrCode,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        Text(text = "QrCode")
    }
}

@Composable
private fun RequestMemoView(
    state: RequestState.Memo,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        Text(text = "Memo")
    }
}
