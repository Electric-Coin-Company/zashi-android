package co.electriccoin.zcash.ui.screen.advancedsettings.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItem
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.util.orDark
import co.electriccoin.zcash.ui.screen.advancedsettings.AdvancedSettingsState
import co.electriccoin.zcash.ui.screen.advancedsettings.AdvancedSettingsTag

// TODO [#1271]: Add AdvancedSettingsView Tests
// TODO [#1271]: https://github.com/Electric-Coin-Company/zashi-android/issues/1271
@Suppress("LongMethod")
@Composable
fun AdvancedSettings(
    state: AdvancedSettingsState,
    topAppBarSubTitleState: TopAppBarSubTitleState,
) {
    BlankBgScaffold(
        topBar = {
            AdvancedSettingsTopAppBar(
                onBack = state.onBack,
                subTitleState = topAppBarSubTitleState,
            )
        }
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                        start = 4.dp,
                        end = 4.dp
                    ),
        ) {
            ZashiSettingsListItem(
                text = stringResource(id = R.string.advanced_settings_recovery),
                icon = R.drawable.ic_advanced_settings_recovery,
                onClick = state.onRecoveryPhraseClick
            )
            ZashiHorizontalDivider()
            ZashiSettingsListItem(
                text = stringResource(id = R.string.advanced_settings_export),
                icon = R.drawable.ic_advanced_settings_export,
                onClick = state.onExportPrivateDataClick
            )
            ZashiHorizontalDivider()
            ZashiSettingsListItem(
                text = stringResource(id = R.string.advanced_settings_choose_server),
                icon =
                    R.drawable.ic_advanced_settings_choose_server orDark
                        R.drawable.ic_advanced_settings_choose_server,
                onClick = state.onChooseServerClick
            )
            ZashiHorizontalDivider()
            ZashiSettingsListItem(
                text = stringResource(id = R.string.advanced_settings_currency_conversion),
                icon =
                    R.drawable.ic_advanced_settings_currency_conversion orDark
                        R.drawable.ic_advanced_settings_currency_conversion,
                onClick = state.onCurrencyConversionClick
            )
            Spacer(modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_advanced_settings_info),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(ZashiColors.Text.textTertiary)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(id = R.string.advanced_settings_info),
                    fontSize = 12.sp,
                    color = ZashiColors.Text.textTertiary,
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            ZashiButton(
                modifier =
                    Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth(),
                text = stringResource(R.string.advanced_settings_delete_button),
                colors = ZashiButtonDefaults.destructive1Colors(),
                onClick = state.onDeleteZashiClick
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun AdvancedSettingsTopAppBar(
    onBack: () -> Unit,
    subTitleState: TopAppBarSubTitleState
) {
    ZashiSmallTopAppBar(
        title = stringResource(id = R.string.advanced_settings_title),
        subtitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        modifier = Modifier.testTag(AdvancedSettingsTag.ADVANCED_SETTINGS_TOP_APP_BAR),
        showTitleLogo = true,
        navigationAction = {
            ZashiTopAppBarBackNavigation(onBack = onBack)
        },
    )
}

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun AdvancedSettingsPreview() =
    ZcashTheme {
        AdvancedSettings(
            state =
                AdvancedSettingsState(
                    onBack = {},
                    onRecoveryPhraseClick = {},
                    onExportPrivateDataClick = {},
                    onChooseServerClick = {},
                    onCurrencyConversionClick = {},
                    onDeleteZashiClick = {},
                ),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }
