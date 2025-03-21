package co.electriccoin.zcash.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.appbar.ZashiMainTopAppBarState
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarWithAccountSelection
import co.electriccoin.zcash.ui.design.component.BigIconButtonState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ZashiBigIconButton
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.fixture.BalanceStateFixture
import co.electriccoin.zcash.ui.fixture.ZashiMainTopAppBarStateFixture
import co.electriccoin.zcash.ui.screen.balances.BalanceState
import co.electriccoin.zcash.ui.screen.balances.BalanceWidget
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.TransactionHistoryWidgetState
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.TransactionHistoryWidgetStateFixture
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.createTransactionHistoryWidgets
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun HomeView(
    appBarState: ZashiMainTopAppBarState?,
    balanceState: BalanceState,
    transactionWidgetState: TransactionHistoryWidgetState,
    state: HomeState
) {
    BlankBgScaffold(
        topBar = { ZashiTopAppBarWithAccountSelection(appBarState) }
    ) { paddingValues ->
        Content(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding() + 24.dp),
            paddingValues = paddingValues,
            transactionHistoryWidgetState = transactionWidgetState,
            balanceState = balanceState,
            state = state
        )
    }
}

@Composable
private fun Content(
    transactionHistoryWidgetState: TransactionHistoryWidgetState,
    paddingValues: PaddingValues,
    balanceState: BalanceState,
    state: HomeState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))
            BalanceWidget(
                modifier =
                    Modifier
                        .padding(
                            start = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                            end = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                        ),
                balanceState = balanceState,
            )
            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))
            NavButtons(paddingValues, state)
            Spacer(Modifier.height(16.dp))
            Message(state.message)
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
            ) {
                createTransactionHistoryWidgets(
                    state = transactionHistoryWidgetState
                )
            }
        }
    }
}

@Composable
private fun Message(state: HomeMessageState?) {
    val cutoutHeight = 16.dp
    var normalizedState: HomeMessageState? by remember { mutableStateOf(state) }
    var isVisible by remember { mutableStateOf(state != null) }
    val bottomCornerSize by animateDpAsState(
        if (isVisible) cutoutHeight else 0.dp,
        animationSpec = tween(350)
    )

    Box(
        modifier = Modifier
            .background(Color.Gray)
            .clickable(onClick = { normalizedState?.onClick?.invoke() })
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cutoutHeight)
                .zIndex(2f)
                .bottomOnlyShadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                    backgroundColor = ZashiColors.Surfaces.bgPrimary
                )
            ,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cutoutHeight)
                .zIndex(1f)
                .align(Alignment.BottomCenter)
                .topOnlyShadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(topStart = bottomCornerSize, topEnd = bottomCornerSize),
                    backgroundColor = ZashiColors.Surfaces.bgPrimary
                ),
        )
    }

    LaunchedEffect(state) {
        if (state != null) {
            normalizedState = state
            isVisible = true
        } else {
            isVisible = false
            delay(350)
            normalizedState = null
        }
    }
}

private fun Modifier.bottomOnlyShadow(
    elevation: Dp,
    shape: Shape,
    backgroundColor: Color,
    clip: Boolean = elevation > 0.dp,
    ambientColor: Color = DefaultShadowColor,
    spotColor: Color = DefaultShadowColor,
): Modifier = this
    .drawWithCache {
        //  bottom shadow offset in Px based on elevation
        val bottomOffsetPx = elevation.toPx()
        // Adjust the size to extend the bottom by the bottom shadow offset
        val adjustedSize = Size(size.width, size.height + bottomOffsetPx)
        val outline = shape.createOutline(adjustedSize, layoutDirection, this)
        val path = Path().apply { addOutline(outline) }
        onDrawWithContent {
            clipPath(path, ClipOp.Intersect) {
                this@onDrawWithContent.drawContent()
            }
        }
    }
    .shadow(elevation, shape, clip, ambientColor, spotColor)
    .background(
        backgroundColor,
        shape
    )

private fun Modifier.topOnlyShadow(
    elevation: Dp,
    shape: Shape,
    backgroundColor: Color,
    clip: Boolean = elevation > 0.dp,
    ambientColor: Color = DefaultShadowColor,
    spotColor: Color = DefaultShadowColor,
): Modifier = this
    .drawWithCache {
        // Adjust the size to extend the bottom by the bottom shadow offset
        val adjustedSize = Size(size.width, size.height)
        val outline = shape.createOutline(adjustedSize, layoutDirection, this)
        val path = Path().apply { addOutline(outline) }
        onDrawWithContent {
            clipPath(path, ClipOp.Intersect) {
                this@onDrawWithContent.drawContent()
            }
        }
    }
    .shadow(elevation, shape, clip, ambientColor, spotColor)
    .background(
        backgroundColor,
        shape
    )

@Composable
private fun NavButtons(
    paddingValues: PaddingValues,
    state: HomeState
) {
    Row(
        modifier = Modifier.scaffoldPadding(paddingValues, top = 0.dp, bottom = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ZashiBigIconButton(
            modifier =
                Modifier
                    .weight(1f)
                    .testTag(HomeTags.RECEIVE),
            state = state.firstButton,
        )
        ZashiBigIconButton(
            modifier =
                Modifier
                    .weight(1f)
                    .testTag(HomeTags.SEND),
            state = state.secondButton,
        )
        ZashiBigIconButton(
            modifier = Modifier.weight(1f),
            state = state.thirdButton,
        )
        ZashiBigIconButton(
            modifier = Modifier.weight(1f),
            state = state.fourthButton,
        )
    }
}

@PreviewScreens
@Composable
private fun Preview() {
    ZcashTheme {
        var isHomeMessageStateVisible by remember { mutableStateOf(true) }
        val scope = rememberCoroutineScope()
        HomeView(
            appBarState = ZashiMainTopAppBarStateFixture.new(),
            balanceState = BalanceStateFixture.new(),
            transactionWidgetState = TransactionHistoryWidgetStateFixture.new(),
            state =
                HomeState(
                    firstButton =
                        BigIconButtonState(
                            text = stringRes("Text"),
                            icon = R.drawable.ic_warning,
                            onClick = {}
                        ),
                    secondButton =
                        BigIconButtonState(
                            text = stringRes("Text"),
                            icon = R.drawable.ic_warning,
                            onClick = {}
                        ),
                    thirdButton =
                        BigIconButtonState(
                            text = stringRes("Text"),
                            icon = R.drawable.ic_warning,
                            onClick = {}
                        ),
                    fourthButton =
                        BigIconButtonState(
                            text = stringRes("Text"),
                            icon = R.drawable.ic_warning,
                            onClick = {}
                        ),
                    message = HomeMessageState(
                        text = "Test string",
                        onClick = {
                            isHomeMessageStateVisible = !isHomeMessageStateVisible
                            if (!isHomeMessageStateVisible) {
                                scope.launch {
                                    delay(1000)
                                    isHomeMessageStateVisible = true
                                }
                            }
                        }
                    ).takeIf { isHomeMessageStateVisible }
                )
        )
    }
}
