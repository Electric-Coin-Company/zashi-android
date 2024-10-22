package co.electriccoin.zcash.ui.screen.integrations.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItem
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItemState
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.component.ZashiVersion
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.integrations.model.IntegrationsState
import co.electriccoin.zcash.ui.screen.settings.SettingsTag
import kotlinx.collections.immutable.persistentListOf

@Composable
fun Integrations(
    state: IntegrationsState,
    topAppBarSubTitleState: TopAppBarSubTitleState
) {
    BlankBgScaffold(
        topBar = {
            IntegrationsTopAppBar(onBack = state.onBack, subTitleState = topAppBarSubTitleState)
        }
    ) { paddingValues ->
        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding() + ZashiDimensions.Spacing.spacing3xl,
                    start = 4.dp,
                    end = 4.dp
                ),
        ) {
            state.items.forEachIndexed { index, item ->
                ZashiSettingsListItem(state = item)
                if (index != state.items.lastIndex) {
                    ZashiHorizontalDivider()
                }
            }

            state.disabledInfo?.let {
                Spacer(modifier = Modifier.height(28.dp))
                DisabledInfo(it)
            }

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingXl))
            Spacer(modifier = Modifier.weight(1f))
            ZashiVersion(modifier = Modifier.align(CenterHorizontally), version = state.version)
        }
    }
}

@Composable
private fun DisabledInfo(it: StringResource) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_advanced_settings_info),
            contentDescription = "",
            colorFilter = ColorFilter.tint(ZashiColors.Utility.WarningYellow.utilityOrange700)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = it.getValue(),
            fontSize = 12.sp,
            color = ZashiColors.Utility.WarningYellow.utilityOrange700,
        )
    }
}

@Composable
private fun IntegrationsTopAppBar(
    onBack: () -> Unit,
    subTitleState: TopAppBarSubTitleState
) {
    ZashiSmallTopAppBar(
        title = stringResource(id = R.string.integrations_title),
        subtitle =
        when (subTitleState) {
            TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
            TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
            TopAppBarSubTitleState.None -> null
        },
        modifier = Modifier.testTag(SettingsTag.SETTINGS_TOP_APP_BAR),
        showTitleLogo = true,
        navigationAction = {
            ZashiTopAppBarBackNavigation(onBack = onBack)
        },
    )
}

@PreviewScreens
@Composable
private fun IntegrationSettings() = ZcashTheme {
    Integrations(
        state =
        IntegrationsState(
            version = stringRes("Version 1.2"),
            onBack = {},
            disabledInfo = stringRes("Disabled info"),
            items = persistentListOf(
                ZashiSettingsListItemState(
                    icon = R.drawable.ic_integrations_coinbase,
                    text = stringRes("Coinbase"),
                    subtitle = stringRes("subtitle"),
                    onClick = {}
                )
            )
        ),
        topAppBarSubTitleState = TopAppBarSubTitleState.None,
    )
}
