package co.electriccoin.zcash.ui.screen.contact.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTextField
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.contact.ContactTag
import co.electriccoin.zcash.ui.screen.contact.model.ContactState

@Composable
fun ContactView(
    state: ContactState,
    topAppBarSubTitleState: TopAppBarSubTitleState
) {
    BlankBgScaffold(
        topBar = {
            ContactTopAppBar(onBack = state.onBack, subTitleState = topAppBarSubTitleState, state = state)
        }
    ) { paddingValues ->
        if (state.isLoading) {
            CircularScreenProgressIndicator()
        } else {
            ContactViewInternal(
                state = state,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            top = paddingValues.calculateTopPadding() + 24.dp,
                            bottom = paddingValues.calculateBottomPadding() + 24.dp,
                            start = 20.dp,
                            end = 20.dp,
                        )
            )
        }
    }
}

@Composable
private fun ContactViewInternal(
    state: ContactState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = stringResource(id = R.string.contact_address_label),
            style = ZashiTypography.textSm,
            fontWeight = FontWeight.Medium,
            color = ZashiColors.Inputs.Filled.label
        )
        Spacer(modifier = Modifier.height(6.dp))
        ZashiTextField(
            modifier = Modifier.fillMaxWidth(),
            state = state.walletAddress,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.contact_address_hint),
                    style = ZashiTypography.textMd,
                    color = ZashiColors.Inputs.Default.text
                )
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(id = R.string.contact_name_label),
            style = ZashiTypography.textSm,
            fontWeight = FontWeight.Medium,
            color = ZashiColors.Inputs.Filled.label
        )
        Spacer(modifier = Modifier.height(6.dp))
        ZashiTextField(
            modifier = Modifier.fillMaxWidth(),
            state = state.contactName,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.contact_name_hint),
                    style = ZashiTypography.textMd,
                    color = ZashiColors.Inputs.Default.text
                )
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        ZashiButton(
            state = state.positiveButton,
            modifier = Modifier.fillMaxWidth()
        )

        state.negativeButton?.let {
            ZashiButton(
                state = it,
                modifier = Modifier.fillMaxWidth(),
                colors = ZashiButtonDefaults.destructive1Colors()
            )
        }
    }
}

@Composable
private fun ContactTopAppBar(
    onBack: () -> Unit,
    subTitleState: TopAppBarSubTitleState,
    state: ContactState
) {
    ZashiSmallTopAppBar(
        title = state.title.getValue(),
        subtitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        modifier = Modifier.testTag(ContactTag.TOP_APP_BAR),
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
        ContactView(
            state =
                ContactState(
                    isLoading = false,
                    onBack = {},
                    title = stringRes("Title"),
                    walletAddress = TextFieldState(stringRes("Address")) {},
                    contactName = TextFieldState(stringRes("Name")) {},
                    positiveButton =
                        ButtonState(
                            text = stringRes("Positive"),
                        ),
                    negativeButton =
                        ButtonState(
                            text = stringRes("Negative"),
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
        ContactView(
            state =
                ContactState(
                    isLoading = true,
                    onBack = {},
                    title = stringRes("Title"),
                    walletAddress = TextFieldState(stringRes("Address")) {},
                    contactName = TextFieldState(stringRes("Name")) {},
                    positiveButton =
                        ButtonState(
                            text = stringRes("Add New Contact"),
                        ),
                    negativeButton =
                        ButtonState(
                            text = stringRes("Add New Contact"),
                        )
                ),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }
}
