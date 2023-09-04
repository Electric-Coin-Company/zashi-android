package co.electriccoin.zcash.ui.screen.settings.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.ZcashTheme.dimens

@Preview("Settings")
@Composable
private fun PreviewSettings() {
    ZcashTheme(darkTheme = false) {
        GradientSurface {
            Settings(
                onBack = {},
            )
        }
    }
}

@Composable
@Suppress("LongParameterList")
fun Settings(
    onBack: () -> Unit,
) {
    Scaffold(topBar = {
        SettingsTopAppBar(
            onBack = onBack,
        )
    }) { paddingValues ->
        SettingsMainContent(
            modifier = Modifier
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingHuge,
                    bottom = paddingValues.calculateBottomPadding() + ZcashTheme.dimens.spacingHuge,
                    start = dimens.spacingHuge,
                    end = dimens.spacingHuge
                )
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SettingsTopAppBar(
    onBack: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(
                    id = R.string.settings_header
                ),
                style = ZcashTheme.typography.primary.titleSmall,
                color = ZcashTheme.colors.screenTitleColor
            )
        },
        navigationIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.settings_back_content_description)
                    )
                }
                Text(
                    text = stringResource(id = R.string.settings_back),
                    style = ZcashTheme.typography.primary.bodyMedium
                )
            }
        },
    )
}

@Composable
@Suppress("LongParameterList")
private fun SettingsMainContent(
    modifier: Modifier = Modifier
) {
    Column(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            onClick = {},
            text = stringResource(R.string.settings_backup_wallet),
            outerPaddingValues = PaddingValues(
                horizontal = dimens.spacingNone,
                vertical = dimens.spacingSmall
            ),
        )

        Spacer(modifier = Modifier.height(dimens.spacingDefault))
        PrimaryButton(
            onClick = { },
            text = stringResource(R.string.settings_send_us_feedback),
            outerPaddingValues = PaddingValues(
                horizontal = dimens.spacingNone,
                vertical = dimens.spacingSmall
            ),
        )

        Spacer(modifier = Modifier.height(dimens.spacingDefault))

        PrimaryButton(
            onClick = {},
            text = stringResource(R.string.settings_privacy_policy),
            outerPaddingValues = PaddingValues(
                horizontal = dimens.spacingNone,
                vertical = dimens.spacingSmall
            ),
        )

        Spacer(modifier = Modifier.height(dimens.spacingDefault))

        PrimaryButton(
            onClick = {},
            text = stringResource(R.string.settings_documentation),
            outerPaddingValues = PaddingValues(
                horizontal = dimens.spacingNone,
                vertical = dimens.spacingSmall
            ),
        )
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(MINIMAL_WEIGHT)
        )

        PrimaryButton(
            onClick = {},
            text = stringResource(R.string.settings_about),
            outerPaddingValues = PaddingValues(
                horizontal = dimens.spacingNone,
                vertical = dimens.spacingSmall
            ),
        )
    }
}
