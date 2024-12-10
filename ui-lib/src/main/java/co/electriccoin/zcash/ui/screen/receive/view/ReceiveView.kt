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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
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
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.fixture.ZashiMainTopAppBarStateFixture
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressState
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveState

@Composable
internal fun ReceiveView(
    state: ReceiveState,
    zashiMainTopAppBarState: ZashiMainTopAppBarState,
) {
    when {
        state.items.isNullOrEmpty() && state.isLoading -> {
            CircularScreenProgressIndicator()
        }

        else -> {
            BlankBgScaffold(
                topBar = {
                    ZashiMainTopAppBar(state = zashiMainTopAppBarState, showHideBalances = false)
                },
            ) { paddingValues ->
                ReceiveContents(
                    items = state.items.orEmpty(),
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

@Suppress("UnstableCollections")
@Composable
private fun ReceiveContents(
    items: List<ReceiveAddressState>,
    modifier: Modifier = Modifier,
) {
    var expandedIndex by rememberSaveable { mutableIntStateOf(0) }

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

        items.forEachIndexed { index, state ->
            if (index != 0) {
                Spacer(Modifier.height(8.dp))
            }

            AddressPanel(
                state = state,
                expanded = index == expandedIndex,
                onExpand = { expandedIndex = index },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AddressPanel(
    state: ReceiveAddressState,
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
                    if (state.isShielded) {
                        ZashiColors.Utility.Purple.utilityPurple50
                    } else {
                        ZashiColors.Utility.Gray.utilityGray50
                    },
                    RoundedCornerShape(ZashiDimensions.Radius.radius3xl)
                )
                .clip(RoundedCornerShape(ZashiDimensions.Radius.radius3xl))
                .clickable { onExpand() }
                .padding(all = ZcashTheme.dimens.spacingLarge)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = state.icon),
                contentDescription = null
            )

            Spacer(Modifier.width(ZcashTheme.dimens.spacingDefault))

            Column {
                Text(
                    text = state.title.getValue(),
                    color = ZashiColors.Text.textPrimary,
                    style = ZashiTypography.textMd,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(ZcashTheme.dimens.spacingTiny))

                Text(
                    text = state.subtitle.getValue(),
                    color = ZashiColors.Text.textTertiary,
                    style = ZashiTypography.textSm
                )
            }

            Spacer(Modifier.width(ZcashTheme.dimens.spacingSmall))

            Spacer(modifier = Modifier.weight(1f))

            if (state.isShielded) {
                Image(
                    painter = painterResource(id = R.drawable.ic_check_shielded_solid),
                    contentDescription = null
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
                    containerColor = ZashiColors.Utility.Purple.utilityPurple100,
                    contentColor = ZashiColors.Utility.Purple.utilityPurple800,
                    iconPainter = painterResource(id = R.drawable.ic_copy_shielded),
                    onClick = state.onCopyClicked,
                    text = stringResource(id = R.string.receive_copy),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingSmall))

                ReceiveIconButton(
                    containerColor = ZashiColors.Utility.Purple.utilityPurple100,
                    contentColor = ZashiColors.Utility.Purple.utilityPurple800,
                    iconPainter = painterResource(id = R.drawable.ic_qr_code_shielded),
                    onClick = state.onQrClicked,
                    text = stringResource(id = R.string.receive_qr_code),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingSmall))

                ReceiveIconButton(
                    containerColor = ZashiColors.Utility.Purple.utilityPurple100,
                    contentColor = ZashiColors.Utility.Purple.utilityPurple800,
                    iconPainter = painterResource(id = R.drawable.ic_request_shielded),
                    onClick = state.onRequestClicked,
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

@Composable
@PreviewScreens
private fun LoadingPreview() =
    ZcashTheme(forceDarkMode = true) {
        ReceiveView(
            state = ReceiveState(items = null, isLoading = true),
            zashiMainTopAppBarState = ZashiMainTopAppBarStateFixture.new()
        )
    }

@PreviewScreens
@Composable
private fun ZashiPreview() =
    ZcashTheme {
        ReceiveView(
            state =
                ReceiveState(
                    items =
                        listOf(
                            ReceiveAddressState(
                                icon = R.drawable.ic_zec_round_full,
                                title = stringRes("Zashi"),
                                subtitle = stringRes("subtitle"),
                                isShielded = true,
                                onCopyClicked = {},
                                onQrClicked = { },
                                onRequestClicked = {},
                            ),
                            ReceiveAddressState(
                                icon = R.drawable.ic_zec_round_stroke,
                                title = stringRes("Zashi"),
                                subtitle = stringRes("subtitle"),
                                isShielded = false,
                                onCopyClicked = {},
                                onQrClicked = { },
                                onRequestClicked = { },
                            )
                        ),
                    isLoading = false
                ),
            zashiMainTopAppBarState = ZashiMainTopAppBarStateFixture.new()
        )
    }

@PreviewScreens
@Composable
private fun KeystonePreview() =
    ZcashTheme {
        ReceiveView(
            state =
                ReceiveState(
                    items =
                        listOf(
                            ReceiveAddressState(
                                icon = co.electriccoin.zcash.ui.design.R.drawable.ic_item_keystone,
                                title = stringRes("Keystone Address"),
                                subtitle = stringRes("subtitle"),
                                isShielded = true,
                                onCopyClicked = {},
                                onQrClicked = {},
                                onRequestClicked = {},
                            ),
                        ),
                    isLoading = false
                ),
            zashiMainTopAppBarState = ZashiMainTopAppBarStateFixture.new()
        )
    }
