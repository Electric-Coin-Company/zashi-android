package co.electriccoin.zcash.ui.screen.integrations

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ZashiCard
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItem
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemDefaults
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.scaffoldScrollPadding
import co.electriccoin.zcash.ui.design.util.stringRes
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
                    .scaffoldScrollPadding(paddingValues),
        ) {
            IntegrationItems(state)

            state.disabledInfo?.let {
                Spacer(modifier = Modifier.height(28.dp))
                DisabledInfo(it)
            }

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingXl))
            Spacer(modifier = Modifier.weight(1f))
            ZashiCard(
                modifier =
                    Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
            ) {
                Image(
                    modifier = Modifier.align(CenterHorizontally),
                    painter = painterResource(R.drawable.ic_integrations_info),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(ZashiColors.Text.textSecondary)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.integrations_info),
                    textAlign = TextAlign.Center,
                    style = ZashiTypography.textMd
                )
            }
        }
    }
}

@Composable
fun IntegrationItems(
    state: IntegrationsState,
    contentPadding: PaddingValues = ZashiListItemDefaults.contentPadding
) {
    state.items.forEachIndexed { index, item ->
        ZashiListItem(
            state = item,
            modifier = Modifier.padding(horizontal = 4.dp),
            leading = {
                ZashiListItemDefaults.LeadingItem(
                    modifier = Modifier.size(40.dp),
                    icon = item.icon,
                    contentDescription = item.title.getValue()
                )
            },
            contentPadding = contentPadding
        )
        if (index != state.items.lastIndex) {
            ZashiHorizontalDivider(
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun DisabledInfo(it: StringResource) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_advanced_settings_info),
            contentDescription = null,
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
private fun IntegrationSettings() =
    ZcashTheme {
        Integrations(
            state =
                IntegrationsState(
                    onBack = {},
                    disabledInfo = stringRes("Disabled info"),
                    items =
                        persistentListOf(
                            ZashiListItemState(
                                icon = R.drawable.ic_integrations_coinbase,
                                title = stringRes("Coinbase"),
                                subtitle = stringRes("subtitle"),
                                onClick = {}
                            ),
                            ZashiListItemState(
                                title = stringRes(R.string.integrations_flexa),
                                subtitle = stringRes(R.string.integrations_flexa),
                                icon = R.drawable.ic_integrations_flexa,
                                onClick = {}
                            ),
                            ZashiListItemState(
                                title = stringRes(R.string.integrations_keystone),
                                subtitle = stringRes(R.string.integrations_keystone_subtitle),
                                icon = R.drawable.ic_integrations_keystone,
                                onClick = {}
                            ),
                        ),
                    onBottomSheetHidden = {}
                ),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }
