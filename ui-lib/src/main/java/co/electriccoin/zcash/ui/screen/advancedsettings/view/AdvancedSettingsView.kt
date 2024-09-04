package co.electriccoin.zcash.ui.screen.advancedsettings.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.TopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.ZcashTheme.dimens
import co.electriccoin.zcash.ui.screen.advancedsettings.AdvancedSettingsTag

// TODO [#1271]: Add AdvancedSettingsView Tests
// TODO [#1271]: https://github.com/Electric-Coin-Company/zashi-android/issues/1271

@Preview("Advanced Settings")
@Composable
private fun PreviewAdvancedSettings() {
    ZcashTheme(forceDarkMode = false) {
        AdvancedSettings(
            onBack = {},
            onDeleteWallet = {},
            onExportPrivateData = {},
            onChooseServer = {},
            onSeedRecovery = {},
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            onCurrencyConversion = {}
        )
    }
}

@Composable
@Suppress("LongParameterList")
fun AdvancedSettings(
    onBack: () -> Unit,
    onDeleteWallet: () -> Unit,
    onExportPrivateData: () -> Unit,
    onChooseServer: () -> Unit,
    onSeedRecovery: () -> Unit,
    onCurrencyConversion: () -> Unit,
    topAppBarSubTitleState: TopAppBarSubTitleState,
) {
    BlankBgScaffold(
        topBar = {
            AdvancedSettingsTopAppBar(
                onBack = onBack,
                subTitleState = topAppBarSubTitleState,
            )
        }
    ) { paddingValues ->
        AdvancedSettingsMainContent(
            modifier =
                Modifier
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        top = paddingValues.calculateTopPadding() + dimens.spacingHuge,
                        bottom = paddingValues.calculateBottomPadding(),
                        start = dimens.screenHorizontalSpacingBig,
                        end = dimens.screenHorizontalSpacingBig
                    ),
            onDeleteWallet = onDeleteWallet,
            onExportPrivateData = onExportPrivateData,
            onSeedRecovery = onSeedRecovery,
            onChooseServer = onChooseServer,
            onCurrencyConversion = onCurrencyConversion
        )
    }
}

@Composable
private fun AdvancedSettingsTopAppBar(
    onBack: () -> Unit,
    subTitleState: TopAppBarSubTitleState
) {
    SmallTopAppBar(
        subTitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        modifier = Modifier.testTag(AdvancedSettingsTag.ADVANCED_SETTINGS_TOP_APP_BAR),
        showTitleLogo = true,
        navigationAction = {
            TopAppBarBackNavigation(
                backText = stringResource(id = R.string.back_navigation).uppercase(),
                backContentDescriptionText = stringResource(R.string.back_navigation_content_description),
                onBack = onBack
            )
        }
    )
}

@Suppress("LongParameterList")
@Composable
private fun AdvancedSettingsMainContent(
    onDeleteWallet: () -> Unit,
    onExportPrivateData: () -> Unit,
    onChooseServer: () -> Unit,
    onCurrencyConversion: () -> Unit,
    onSeedRecovery: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        Modifier
            .fillMaxSize()
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            onClick = onSeedRecovery,
            text = stringResource(R.string.advanced_settings_backup_wallet),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimens.spacingDefault))

        PrimaryButton(
            onClick = onExportPrivateData,
            text = stringResource(R.string.advanced_settings_export_private_data),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimens.spacingDefault))

        PrimaryButton(
            onClick = onChooseServer,
            text = stringResource(R.string.advanced_settings_choose_server),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimens.spacingDefault))

        PrimaryButton(
            onClick = onCurrencyConversion,
            text = stringResource(R.string.advanced_settings_currency_conversion),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        Spacer(modifier = Modifier.height(dimens.spacingDefault))

        PrimaryButton(
            onClick = onDeleteWallet,
            text =
                stringResource(
                    R.string.advanced_settings_delete_wallet,
                    stringResource(id = R.string.app_name)
                ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimens.spacingDefault))

        Text(
            text = stringResource(id = R.string.advanced_settings_delete_wallet_footnote),
            style = ZcashTheme.extendedTypography.footnote,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(dimens.spacingHuge))
    }
}
