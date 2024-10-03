package co.electriccoin.zcash.ui.screen.addressbook.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.ZashiBottomBar
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItem
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListTrailingItem
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookTag
import co.electriccoin.zcash.ui.screen.addressbook.model.AddressBookContactState
import co.electriccoin.zcash.ui.screen.addressbook.model.AddressBookState

@Suppress("LongMethod")
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
        when {
            state.contacts.isEmpty() && state.isLoading -> {
                CircularScreenProgressIndicator()
            }

            state.contacts.isEmpty() && !state.isLoading -> {
                Empty(
                    state = state,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                )
            }

            else -> {
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
                                bottom = paddingValues.calculateBottomPadding(),
                                start = 4.dp,
                                end = 4.dp
                            )
                    ) {
                        itemsIndexed(state.contacts) { index, item ->
                            ContactItem(state = item)
                            if (index != state.contacts.lastIndex) {
                                ZashiHorizontalDivider()
                            }
                        }
                    }

                    ZashiBottomBar {
                        AddContactButton(
                            state.addButton,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactItem(state: AddressBookContactState) {
    ZashiSettingsListItem(
        leading = { modifier ->
            ContactItemLeading(modifier = modifier, state = state)
        },
        content = { modifier ->
            ContactItemContent(modifier = modifier, state = state)
        },
        trailing = { modifier ->
            ZashiSettingsListTrailingItem(
                modifier = modifier,
                isEnabled = true,
                contentDescription = state.name.getValue()
            )
        },
        onClick = state.onClick,
        contentPadding = PaddingValues(top = 12.dp, bottom = if (state.isShielded) 8.dp else 12.dp)
    )
}

@Composable
private fun ContactItemLeading(
    state: AddressBookContactState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier.size(height = 50.dp, width = 54.dp)
    ) {
        Text(
            modifier =
                Modifier
                    .background(ZashiColors.Avatars.avatarBg, CircleShape)
                    .size(40.dp)
                    .padding(top = 10.dp)
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
    state: AddressBookContactState,
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
    state: AddressBookState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(painter = painterResource(id = R.drawable.ic_address_book_empty), contentDescription = "")
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(id = R.string.address_book_empty),
                fontWeight = FontWeight.SemiBold,
                color = ZashiColors.Text.textPrimary,
                style = ZashiTypography.header6
            )
        }

        AddContactButton(
            state.addButton,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(20.dp)
        )
    }
}

@Composable
private fun AddContactButton(
    state: ButtonState,
    modifier: Modifier = Modifier
) {
    ZashiButton(
        modifier = modifier,
        state = state
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
                    version = stringRes("Version 1.2"),
                    onBack = {},
                    contacts =
                        (1..10).map {
                            AddressBookContactState(
                                name = stringRes("Name Surname"),
                                address = stringRes("3iY5ZSkRnevzSMu4hosasdasdasdasd12312312dasd9hw2"),
                                initials = stringRes("NS"),
                                isShielded = it % 2 == 0,
                                onClick = {}
                            )
                        },
                    addButton =
                        ButtonState(
                            text = stringRes("Add New Contact"),
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
                    version = stringRes("Version 1.2"),
                    onBack = {},
                    contacts = emptyList(),
                    addButton =
                        ButtonState(
                            text = stringRes("Add New Contact"),
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
                    version = stringRes("Version 1.2"),
                    onBack = {},
                    contacts = emptyList(),
                    addButton =
                        ButtonState(
                            text = stringRes("Add New Contact"),
                        )
                ),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }
}
