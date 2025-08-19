package co.electriccoin.zcash.ui.screen.transactiondetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.appbar.ZashiMainTopAppBarState
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ButtonStyle
import co.electriccoin.zcash.ui.design.component.GradientBgScaffold
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiBottomBar
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiIconButton
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.TickerLocation.HIDDEN
import co.electriccoin.zcash.ui.design.util.asScaffoldPaddingValues
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.orDark
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.fixture.ZashiMainTopAppBarStateFixture
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ReceiveShielded
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ReceiveShieldedState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ReceiveTransparent
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ReceiveTransparentState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.SendShielded
import co.electriccoin.zcash.ui.screen.transactiondetail.info.SendShieldedState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.SendSwap
import co.electriccoin.zcash.ui.screen.transactiondetail.info.SendSwapState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.SendTransparent
import co.electriccoin.zcash.ui.screen.transactiondetail.info.SendTransparentState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.Shielding
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ShieldingState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.TransactionDetailInfoState

@Composable
fun TransactionDetailView(
    state: TransactionDetailState,
    mainAppBarState: ZashiMainTopAppBarState?,
) {
    GradientBgScaffold(
        startColor = ZashiColors.Surfaces.bgPrimary orDark ZashiColors.Surfaces.bgAdjust,
        endColor = ZashiColors.Surfaces.bgPrimary,
        topBar = {
            TransactionDetailTopAppBar(
                onBack = state.onBack,
                state = state,
                mainAppBarState = mainAppBarState,
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TransactionDetailHeader(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .scaffoldPadding(
                            paddingValues = paddingValues,
                            bottom = 0.dp,
                            start = 0.dp,
                            end = 0.dp
                        ),
                iconState = getHeaderIconState(state.info),
                state = state.header
            )
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .scaffoldPadding(
                            paddingValues,
                            top = 24.dp
                        ),
            ) {
                when (state.info) {
                    is ReceiveShieldedState ->
                        ReceiveShielded(
                            modifier = Modifier.fillMaxWidth(),
                            state = state.info
                        )

                    is ReceiveTransparentState ->
                        ReceiveTransparent(
                            modifier = Modifier.fillMaxWidth(),
                            state = state.info
                        )

                    is SendShieldedState ->
                        SendShielded(
                            modifier = Modifier.fillMaxWidth(),
                            state = state.info
                        )

                    is SendTransparentState ->
                        SendTransparent(
                            modifier = Modifier.fillMaxWidth(),
                            state = state.info
                        )

                    is ShieldingState ->
                        Shielding(
                            modifier = Modifier.fillMaxWidth(),
                            state = state.info
                        )

                    is SendSwapState ->
                        SendSwap(
                            modifier = Modifier.fillMaxWidth(),
                            state = state.info
                        )
                }
            }
            BottomBar(
                scrollState = scrollState,
                paddingValues = paddingValues,
                state = state
            )
        }
    }
}

@Composable
private fun BottomBar(
    scrollState: ScrollState,
    paddingValues: PaddingValues,
    state: TransactionDetailState
) {
    ZashiBottomBar(
        isElevated = scrollState.value > 0,
        contentPadding = paddingValues.asScaffoldPaddingValues(top = 0.dp, bottom = 0.dp)
    ) {
        if (state.errorFooter != null) {
            Image(
                modifier =
                    Modifier
                        .size(16.dp)
                        .align(Alignment.CenterHorizontally),
                painter = painterResource(co.electriccoin.zcash.ui.design.R.drawable.ic_info),
                contentDescription = null,
                colorFilter = ColorFilter.tint(ZashiColors.Text.textError)
            )
            Spacer(8.dp)
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = state.errorFooter.title.getValue(),
                style = ZashiTypography.textSm,
                fontWeight = FontWeight.Medium,
                color = ZashiColors.Text.textError,
                textAlign = TextAlign.Center
            )
            Spacer(4.dp)
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = state.errorFooter.subtitle.getValue(),
                style = ZashiTypography.textSm,
                color = ZashiColors.Text.textError,
                textAlign = TextAlign.Center
            )
            Spacer(32.dp)
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            state.secondaryButton?.let {
                ZashiButton(
                    modifier = Modifier.weight(1f),
                    state = it,
                    defaultPrimaryColors = ZashiButtonDefaults.tertiaryColors()
                )
            }
            if (state.secondaryButton != null && state.primaryButton != null) {
                Spacer(Modifier.width(12.dp))
            }
            state.primaryButton?.let {
                ZashiButton(
                    modifier = Modifier.weight(1f),
                    state = it
                )
            }
        }
    }
}

fun getHeaderIconState(info: TransactionDetailInfoState): TransactionDetailIconHeaderState =
    TransactionDetailIconHeaderState(
        when (info) {
            is ReceiveShieldedState ->
                listOf(
                    R.drawable.ic_transaction_detail_z,
                    R.drawable.ic_transaction_detail_receive
                )

            is ReceiveTransparentState ->
                listOf(
                    R.drawable.ic_transaction_detail_z,
                    R.drawable.ic_transaction_detail_receive
                )

            is SendShieldedState ->
                listOf(
                    R.drawable.ic_transaction_detail_z,
                    R.drawable.ic_transaction_detail_send
                )

            is SendSwapState ->
                listOf(
                    R.drawable.ic_transaction_detail_z,
                    R.drawable.ic_transaction_detail_send
                )

            is SendTransparentState ->
                listOf(
                    R.drawable.ic_transaction_detail_z,
                    R.drawable.ic_transaction_detail_send
                )

            is ShieldingState ->
                listOf(
                    R.drawable.ic_transaction_detail_z,
                    R.drawable.ic_transaction_detail_private,
                    R.drawable.ic_transaction_detail_shield
                )
        }
    )

@Composable
private fun TransactionDetailTopAppBar(
    onBack: () -> Unit,
    state: TransactionDetailState,
    mainAppBarState: ZashiMainTopAppBarState?,
) {
    ZashiSmallTopAppBar(
        navigationAction = {
            ZashiTopAppBarBackNavigation(onBack = onBack)
        },
        regularActions = {
            mainAppBarState?.balanceVisibilityButton?.let {
                ZashiIconButton(it, modifier = Modifier.size(40.dp))
                Spacer(Modifier.width(4.dp))
            }
            ZashiIconButton(state.bookmarkButton, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(20.dp))
        },
        colors =
            ZcashTheme.colors.topAppBarColors orDark
                ZcashTheme.colors.topAppBarColors.copyColors(
                    containerColor = Color.Transparent
                ),
    )
}

@PreviewScreens
@Composable
private fun SendShieldPreview() =
    ZcashTheme {
        TransactionDetailView(
            state =
                TransactionDetailState(
                    onBack = {},
                    header =
                        TransactionDetailHeaderState(
                            title = stringRes("Sent"),
                            amount = stringRes(Zatoshi(1000000), HIDDEN),
                        ),
                    info = SendShieldStateFixture.new(),
                    primaryButton = ButtonState(stringRes("Primary"), ButtonStyle.DESTRUCTIVE1),
                    secondaryButton = null,
                    bookmarkButton = IconButtonState(R.drawable.ic_transaction_detail_no_bookmark) {},
                    errorFooter =
                        ErrorFooter(
                            stringRes("Title"),
                            stringRes("Subtitle"),
                        )
                ),
            mainAppBarState = ZashiMainTopAppBarStateFixture.new(),
        )
    }

@PreviewScreens
@Composable
private fun SendTransparentPreview() =
    ZcashTheme {
        TransactionDetailView(
            state =
                TransactionDetailState(
                    onBack = {},
                    header =
                        TransactionDetailHeaderState(
                            title = stringRes("Sent"),
                            amount = stringRes(Zatoshi(1000000), HIDDEN),
                        ),
                    info = SendTransparentStateFixture.new(),
                    primaryButton = ButtonState(stringRes("Primary")),
                    secondaryButton = ButtonState(stringRes("Secondary")),
                    bookmarkButton = IconButtonState(R.drawable.ic_transaction_detail_no_bookmark) {},
                    errorFooter = null
                ),
            mainAppBarState = ZashiMainTopAppBarStateFixture.new(),
        )
    }

@PreviewScreens
@Composable
private fun ReceiveShieldPreview() =
    ZcashTheme {
        TransactionDetailView(
            state =
                TransactionDetailState(
                    onBack = {},
                    header =
                        TransactionDetailHeaderState(
                            title = stringRes("Sent"),
                            amount = stringRes(Zatoshi(1000000), HIDDEN),
                        ),
                    info = ReceiveShieldedStateFixture.new(),
                    primaryButton = ButtonState(stringRes("Primary")),
                    secondaryButton = ButtonState(stringRes("Secondary")),
                    bookmarkButton = IconButtonState(R.drawable.ic_transaction_detail_no_bookmark) {},
                    errorFooter = null
                ),
            mainAppBarState = ZashiMainTopAppBarStateFixture.new(),
        )
    }

@PreviewScreens
@Composable
private fun ReceiveTransparentPreview() =
    ZcashTheme {
        TransactionDetailView(
            state =
                TransactionDetailState(
                    onBack = {},
                    header =
                        TransactionDetailHeaderState(
                            title = stringRes("Sent"),
                            amount = stringRes(Zatoshi(1000000), HIDDEN),
                        ),
                    info = ReceiveTransparentStateFixture.new(),
                    primaryButton = ButtonState(stringRes("Primary")),
                    secondaryButton = ButtonState(stringRes("Secondary")),
                    bookmarkButton = IconButtonState(R.drawable.ic_transaction_detail_no_bookmark) {},
                    errorFooter = null
                ),
            mainAppBarState = ZashiMainTopAppBarStateFixture.new(),
        )
    }

@PreviewScreens
@Composable
private fun ShieldingPreview() =
    ZcashTheme {
        TransactionDetailView(
            state =
                TransactionDetailState(
                    onBack = {},
                    header =
                        TransactionDetailHeaderState(
                            title = stringRes("Sent"),
                            amount = stringRes(Zatoshi(1000000), HIDDEN),
                        ),
                    info = ShieldingStateFixture.new(),
                    primaryButton = ButtonState(stringRes("Primary")),
                    secondaryButton = ButtonState(stringRes("Secondary")),
                    bookmarkButton = IconButtonState(R.drawable.ic_transaction_detail_no_bookmark) {},
                    errorFooter = null
                ),
            mainAppBarState = ZashiMainTopAppBarStateFixture.new(),
        )
    }
