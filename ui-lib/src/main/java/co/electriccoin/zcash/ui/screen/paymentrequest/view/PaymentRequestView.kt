@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.paymentrequest.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.fixture.WalletFixture
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SerializableAddress
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.ZashiBottomBar
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.paymentrequest.model.PaymentRequestArguments
import co.electriccoin.zcash.ui.screen.paymentrequest.model.PaymentRequestState

@Composable
@PreviewScreens
private fun PaymentRequestLoadingPreview() =
    ZcashTheme(forceDarkMode = true) {
        PaymentRequestView(
            state = PaymentRequestState.Loading,
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }

@Composable
@PreviewScreens
private fun PaymentRequestPreview() =
    ZcashTheme(forceDarkMode = false) {
        PaymentRequestView(
            state =
                PaymentRequestState.Prepared(
                    onClose = {},
                    onSend = {},
                    monetarySeparators = MonetarySeparators.current(),
                    arguments = PaymentRequestArguments(
                        SerializableAddress(
                            WalletFixture.Alice.getAddresses(ZcashNetwork.Mainnet).unified,
                            AddressType.Unified
                        ),
                        10000000,
                        "For the coffee",
                        byteArrayOf(),
                        zip321Uri = "zcash:t1duiEGg7b39nfQee3XaTY4f5McqfyJKhBi?amount=1&memo=VGhpcyBpcyBhIHNpbXBsZSBt",
                    ),
                    exchangeRateState = ExchangeRateState.Data(onRefresh = {})
                ),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }

@Composable
internal fun PaymentRequestView(
    state: PaymentRequestState,
    topAppBarSubTitleState: TopAppBarSubTitleState,
) {
    when (state) {
        PaymentRequestState.Loading -> {
            CircularScreenProgressIndicator()
        }
        is PaymentRequestState.Prepared -> {
            BlankBgScaffold(
                topBar = {
                    PaymentRequestTopAppBar(
                        onClose = state.onClose,
                        subTitleState = topAppBarSubTitleState,
                    )
                },
                bottomBar = {
                    PaymentRequestBottomBar(state = state)
                }
            ) { paddingValues ->
                PaymentRequestContents(
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
private fun PaymentRequestTopAppBar(
    onClose: () -> Unit,
    subTitleState: TopAppBarSubTitleState,
) {
    ZashiSmallTopAppBar(
        subtitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        title = stringResource(id = R.string.payment_request_title),
        navigationAction = {
            IconButton(
                onClick = onClose,
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
                    contentDescription = stringResource(id = R.string.payment_request_close_content_description),
                    modifier =
                    Modifier
                        .padding(all = 3.dp)
                )
            }
        },
    )
}

@Composable
private fun PaymentRequestBottomBar(
    state: PaymentRequestState.Prepared,
    modifier: Modifier = Modifier,
) {
    ZashiBottomBar(modifier = modifier.fillMaxWidth()) {
        ZashiButton(
            text = stringResource(id = R.string.payment_request_send_btn),
            onClick = { state.onSend(state.arguments.zip321Uri) },
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun PaymentRequestContents(
    state: PaymentRequestState.Prepared,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = state.arguments.zip321Uri)
        Text(text = state.arguments.toString())
    }
}
