package co.electriccoin.zcash.ui.screen.warning.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.test.CommonTag
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GridBgScaffold
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview
@Composable
private fun NotEnoughSpacePreview() {
    ZcashTheme(forceDarkMode = false) {
        NotEnoughSpaceView(
            onSettings = {},
            onSystemSettings = {},
            snackbarHostState = SnackbarHostState(),
            spaceAvailableMegabytes = 300,
            storageSpaceRequiredGigabytes = 1,
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }
}

@Preview
@Composable
private fun NotEnoughSpaceDarkPreview() {
    ZcashTheme(forceDarkMode = true) {
        NotEnoughSpaceView(
            onSettings = {},
            onSystemSettings = {},
            snackbarHostState = SnackbarHostState(),
            spaceAvailableMegabytes = 300,
            storageSpaceRequiredGigabytes = 1,
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }
}

@Composable
@Suppress("LongParameterList")
fun NotEnoughSpaceView(
    onSettings: () -> Unit,
    onSystemSettings: () -> Unit,
    spaceAvailableMegabytes: Int,
    storageSpaceRequiredGigabytes: Int,
    topAppBarSubTitleState: TopAppBarSubTitleState,
    snackbarHostState: SnackbarHostState,
) {
    GridBgScaffold(
        topBar = {
            NotEnoughSpaceTopAppBar(
                onSettings = onSettings,
                subTitleState = topAppBarSubTitleState,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        NotEnoughSpaceMainContent(
            onSystemSettings = onSystemSettings,
            spaceRequiredToContinueMegabytes = spaceAvailableMegabytes,
            storageSpaceRequiredGigabytes = storageSpaceRequiredGigabytes,
            modifier =
                Modifier
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                        start = ZcashTheme.dimens.screenHorizontalSpacingBig,
                        end = ZcashTheme.dimens.screenHorizontalSpacingBig
                    )
        )
    }
}

@Composable
private fun NotEnoughSpaceTopAppBar(
    onSettings: () -> Unit,
    subTitleState: TopAppBarSubTitleState
) {
    SmallTopAppBar(
        subTitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        titleText = stringResource(id = R.string.not_enough_space_title).uppercase(),
        hamburgerMenuActions = {
            IconButton(
                onClick = onSettings,
                modifier = Modifier.testTag(CommonTag.SETTINGS_TOP_BAR_BUTTON)
            ) {
                Icon(
                    painter = painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.hamburger_menu_icon),
                    contentDescription = stringResource(id = R.string.settings_menu_content_description)
                )
            }
        }
    )
}

@Composable
private fun NotEnoughSpaceMainContent(
    onSystemSettings: () -> Unit,
    spaceRequiredToContinueMegabytes: Int,
    storageSpaceRequiredGigabytes: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier =
            modifier.then(
                Modifier
                    .fillMaxHeight()
                    .verticalScroll(
                        rememberScrollState()
                    )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingBig))

        Image(
            painter = painterResource(id = R.drawable.ic_zashi_logo_sign_warn),
            colorFilter = ColorFilter.tint(color = ZcashTheme.colors.secondaryColor),
            contentDescription = null,
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingBig))

        Header(
            text =
                stringResource(
                    id = R.string.not_enough_space_description_1,
                    stringResource(id = R.string.app_name),
                    storageSpaceRequiredGigabytes,
                    spaceRequiredToContinueMegabytes
                ),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingLarge))

        Body(
            text =
                stringResource(
                    id = R.string.not_enough_space_description_2,
                    stringResource(id = R.string.app_name)
                ),
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        PrimaryButton(
            onClick = onSystemSettings,
            text = stringResource(R.string.not_enough_space_system_settings_btn),
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingHuge))
    }
}
