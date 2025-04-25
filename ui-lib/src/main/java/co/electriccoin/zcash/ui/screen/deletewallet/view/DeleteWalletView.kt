package co.electriccoin.zcash.ui.screen.deletewallet.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiCheckbox
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.design.util.stringRes

@PreviewScreens
@Composable
private fun ExportPrivateDataPreview() =
    ZcashTheme {
        DeleteWallet(
            snackbarHostState = SnackbarHostState(),
            onBack = {},
            onConfirm = {},
        )
    }

@Composable
fun DeleteWallet(
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
) {
    Scaffold(
        topBar = {
            DeleteWalletDataTopAppBar(
                onBack = onBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        DeleteWalletContent(
            onConfirm = onConfirm,
            modifier =
                Modifier
                    .fillMaxSize()
                    .scaffoldPadding(paddingValues)
                    .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun DeleteWalletDataTopAppBar(
    onBack: () -> Unit
) {
    ZashiSmallTopAppBar(
        title = stringResource(R.string.delete_wallet_title),
        navigationAction = {
            ZashiTopAppBarBackNavigation(
                onBack = onBack
            )
        }
    )
}

@Composable
private fun DeleteWalletContent(
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.delete_wallet_title),
            style = ZashiTypography.header6,
            color = ZashiColors.Text.textPrimary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(ZashiDimensions.Spacing.spacingXl))

        Text(
            text = stringResource(R.string.delete_wallet_text_1),
            style = ZashiTypography.textMd,
            color = ZashiColors.Text.textPrimary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(ZashiDimensions.Spacing.spacingXl))

        Text(
            text = stringResource(R.string.delete_wallet_text_2),
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textPrimary,
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        val checkedState = rememberSaveable { mutableStateOf(false) }

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingXl))

        Row(Modifier.fillMaxWidth()) {
            ZashiCheckbox(
                isChecked = checkedState.value,
                onClick = {
                    checkedState.value = checkedState.value.not()
                },
                text = stringRes(R.string.delete_wallet_acknowledge),
            )
        }

        Spacer(Modifier.height(ZashiDimensions.Spacing.spacingLg))

        ZashiButton(
            onClick = onConfirm,
            text = stringResource(R.string.delete_wallet_button),
            enabled = checkedState.value,
            modifier = Modifier.fillMaxWidth(),
            colors = ZashiButtonDefaults.destructive1Colors()
        )
    }
}
