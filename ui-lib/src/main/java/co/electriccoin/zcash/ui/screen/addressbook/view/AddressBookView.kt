package co.electriccoin.zcash.ui.screen.addressbook.view

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.ZashiBottomBar
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.component.listitem.BaseListItem
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemDefaults
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookTag
import co.electriccoin.zcash.ui.screen.addressbook.model.AddressBookItem
import co.electriccoin.zcash.ui.screen.addressbook.model.AddressBookState

@Composable
fun AddressBookView(
    state: AddressBookState,
    topAppBarSubTitleState: TopAppBarSubTitleState
) {
    BlankBgScaffold(
        topBar = {
            AddressBookTopAppBar(onBack = state.onBack, subTitleState = topAppBarSubTitleState)
        }
    ) { paddingValues ->
        if (state.contacts.isEmpty() && state.isLoading) {
            CircularScreenProgressIndicator()
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding =
                    PaddingValues(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding() + ZashiDimensions.Spacing.spacing3xl,
                        start = 4.dp,
                        end = 4.dp
                    )
                ) {
                    itemsIndexed(
                        contentType = { _, item -> item.contentType },
                        items = state.contacts,
                    ) { index, item ->
                        when (item) {
                            is AddressBookItem.Contact -> {
                                ZashiContactListItem(state = item)
                                if (index != state.contacts.lastIndex &&
                                    state.contacts[index + 1] is AddressBookItem.Contact
                                ) {
                                    ZashiHorizontalDivider()
                                }
                            }

                            is AddressBookItem.Title -> {
                                if (index == 0) {
                                    Spacer(Modifier.height(16.dp))
                                } else {
                                    Spacer(Modifier.height(20.dp))
                                }
                                ZashiTitleItem(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    state = item
                                )
                                Spacer(Modifier.height(4.dp))
                            }

                            AddressBookItem.Empty -> {
                                Spacer(modifier = Modifier.height(68.dp))
                                Empty(
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp)
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                ZashiBottomBar {
                    AddContactButton(
                        state = state,
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = ZashiDimensions.Spacing.spacing3xl)
                    )
                }
            }
        }
    }
}

@Composable
private fun ZashiTitleItem(
    state: AddressBookItem.Title,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = state.title.getValue(),
        style = ZashiTypography.textSm,
        fontWeight = FontWeight.Medium,
        color = ZashiColors.Text.textTertiary,
    )
}

@Composable
private fun ZashiContactListItem(state: AddressBookItem.Contact) {
    BaseListItem(
        leading = { modifier ->
            ContactItemLeading(modifier = modifier, state = state)
        },
        content = { modifier ->
            ContactItemContent(modifier = modifier, state = state)
        },
        trailing = { modifier ->
            ZashiListItemDefaults.TrailingItem(
                modifier = modifier,
                isEnabled = true,
                contentDescription = state.name.getValue()
            )
        },
        onClick = state.onClick,
        contentPadding =
        PaddingValues(
            start = 20.dp,
            top = 12.dp,
            end = 20.dp,
            bottom =
            if (state.isShielded) {
                8.dp
            } else {
                12.dp
            }
        )
    )
}

@Composable
private fun ContactItemLeading(
    state: AddressBookItem.Contact,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier.size(40.dp)
    ) {
        Text(
            modifier =
            Modifier
                .background(ZashiColors.Avatars.avatarBg, CircleShape)
                .size(40.dp)
                .padding(top = 11.dp)
                .align(Alignment.Center),
            text = state.initials.getValue(),
            style = ZashiTypography.textSm,
            color = ZashiColors.Avatars.avatarTextFg,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
        )
        if (state.isShielded) {
            Image(
                modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .size(24.dp),
                painter = painterResource(id = R.drawable.ic_address_book_shielded),
                contentDescription = ""
            )
        }
    }
}

@Composable
private fun ContactItemContent(
    state: AddressBookItem.Contact,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = state.name.getValue(),
            style = ZashiTypography.textMd,
            fontWeight = FontWeight.SemiBold,
            color = ZashiColors.Text.textPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = state.address.getValue(),
            style = ZashiTypography.textXs,
            color = ZashiColors.Text.textTertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun Empty(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.dashedBorder(
            strokeWidth = 2.5.dp,
            color = ZashiColors.Surfaces.strokeSecondary,
            cornerRadiusDp = 16.dp,
            density = LocalDensity.current
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(painter = painterResource(id = R.drawable.ic_address_book_empty), contentDescription = "")
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.address_book_empty),
                color = ZashiColors.Text.textPrimary,
                style = ZashiTypography.header6,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

fun Modifier.dashedBorder(
    strokeWidth: Dp,
    color: Color,
    cornerRadiusDp: Dp,
    density: Density
) = composed(
    factory = {
        val strokeWidthPx = density.run { strokeWidth.toPx() }
        val cornerRadiusPx = density.run { cornerRadiusDp.toPx() }

        this.then(
            Modifier.drawWithCache {
                onDrawBehind {
                    val stroke = Stroke(
                        width = strokeWidthPx,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 25f), 0f)
                    )

                    drawRoundRect(
                        color = color,
                        style = stroke,
                        cornerRadius = CornerRadius(cornerRadiusPx)
                    )
                }
            }
        )
    }
)

@Composable
private fun AddContactButton(
    state: AddressBookState,
    modifier: Modifier = Modifier
) {
    val transitionState = remember { MutableTransitionState(false) }

    ZashiButton(
        modifier = modifier,
        state =
        ButtonState(
            onClick = {
                if (transitionState.targetState && transitionState.currentState) {
                    transitionState.targetState = false
                } else if (!transitionState.targetState && !transitionState.currentState) {
                    transitionState.targetState = true
                }
            },
            text = stringRes(R.string.address_book_add)
        )
    ) { scope ->
        Image(
            painter = painterResource(id = R.drawable.ic_address_book_plus),
            colorFilter = ColorFilter.tint(ZashiColors.Btns.Primary.btnPrimaryFg),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(8.dp))
        scope.Text()
        Spacer(modifier = Modifier.width(6.dp))
        scope.Loading()
    }

    if (transitionState.currentState || transitionState.targetState || !transitionState.isIdle) {
        val offset = with(LocalDensity.current) { 302.dp.toPx() }.toInt()
        AddressBookPopup(
            offset = IntOffset(0, offset),
            transitionState = transitionState,
            onDismissRequest = {
                transitionState.targetState = false
            },
            state = state
        )
    }
}

@Composable
private fun AddressBookTopAppBar(
    onBack: () -> Unit,
    subTitleState: TopAppBarSubTitleState,
) {
    ZashiSmallTopAppBar(
        title = stringResource(id = R.string.address_book_title),
        subtitle =
        when (subTitleState) {
            TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
            TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
            TopAppBarSubTitleState.None -> null
        },
        modifier = Modifier.testTag(AddressBookTag.TOP_APP_BAR),
        showTitleLogo = true,
        navigationAction = {
            ZashiTopAppBarBackNavigation(onBack = onBack)
        },
    )
}

@PreviewScreens
@Composable
private fun DataPreview() {
    ZcashTheme {
        AddressBookView(
            state =
            AddressBookState(
                isLoading = false,
                onBack = {},
                contacts =
                listOf(
                    AddressBookItem.Title(stringRes("Title")),
                    AddressBookItem.Contact(
                        name = stringRes("Name Surname"),
                        address = stringRes("3iY5ZSkRnevzSMu4hosasdasdasdasd12312312dasd9hw2"),
                        initials = stringRes("NS"),
                        isShielded = false,
                        onClick = {}
                    ),
                    AddressBookItem.Contact(
                        name = stringRes("Name Surname"),
                        address = stringRes("3iY5ZSkRnevzSMu4hosasdasdasdasd12312312dasd9hw2"),
                        initials = stringRes("NS"),
                        isShielded = false,
                        onClick = {}
                    ),
                    AddressBookItem.Title(stringRes("Title")),
                    AddressBookItem.Contact(
                        name = stringRes("Name Surname"),
                        address = stringRes("3iY5ZSkRnevzSMu4hosasdasdasdasd12312312dasd9hw2"),
                        initials = stringRes("NS"),
                        isShielded = false,
                        onClick = {}
                    ),
                    AddressBookItem.Contact(
                        name = stringRes("Name Surname"),
                        address = stringRes("3iY5ZSkRnevzSMu4hosasdasdasdasd12312312dasd9hw2"),
                        initials = stringRes("NS"),
                        isShielded = false,
                        onClick = {}
                    )
                ),
                scanButton =
                ButtonState(
                    text = stringRes("Scan"),
                ),
                manualButton =
                ButtonState(
                    text = stringRes("Manual"),
                )
            ),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }
}

@PreviewScreens
@Composable
private fun LoadingPreview() {
    ZcashTheme {
        AddressBookView(
            state =
            AddressBookState(
                isLoading = true,
                onBack = {},
                contacts = emptyList(),
                scanButton =
                ButtonState(
                    text = stringRes("Scan"),
                ),
                manualButton =
                ButtonState(
                    text = stringRes("Manual"),
                )
            ),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }
}

@PreviewScreens
@Composable
private fun EmptyPreview() {
    ZcashTheme {
        AddressBookView(
            state =
            AddressBookState(
                isLoading = false,
                onBack = {},
                contacts = listOf(
                    AddressBookItem.Title(stringRes("Title")),
                    AddressBookItem.Contact(
                        name = stringRes("Name Surname"),
                        address = stringRes("3iY5ZSkRnevzSMu4hosasdasdasdasd12312312dasd9hw2"),
                        initials = stringRes("NS"),
                        isShielded = false,
                        onClick = {}
                    ),
                    AddressBookItem.Empty
                ),
                scanButton =
                ButtonState(
                    text = stringRes("Scan"),
                ),
                manualButton =
                ButtonState(
                    text = stringRes("Manual"),
                )
            ),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }
}
