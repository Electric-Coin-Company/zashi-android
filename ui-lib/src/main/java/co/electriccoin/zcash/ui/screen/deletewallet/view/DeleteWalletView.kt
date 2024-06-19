package co.electriccoin.zcash.ui.screen.deletewallet.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GridBgScaffold
import co.electriccoin.zcash.ui.design.component.GridBgSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.LabeledCheckBox
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TopScreenLogoTitle
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview("Delete Wallet")
@Composable
private fun ExportPrivateDataPreview() {
    ZcashTheme(forceDarkMode = false) {
        DeleteWallet(
            snackbarHostState = SnackbarHostState(),
            onBack = {},
            onConfirm = {},
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }
}

@Composable
fun DeleteWallet(
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    topAppBarSubTitleState: TopAppBarSubTitleState,
) {
    GridBgScaffold(
        topBar = {
            DeleteWalletDataTopAppBar(
                onBack = onBack,
                subTitleState = topAppBarSubTitleState,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        DeleteWalletContent(
            onConfirm = onConfirm,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                        start = ZcashTheme.dimens.screenHorizontalSpacingBig,
                        end = ZcashTheme.dimens.screenHorizontalSpacingBig
                    )
                    .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun DeleteWalletDataTopAppBar(
    onBack: () -> Unit,
    subTitleState: TopAppBarSubTitleState
) {
    GridBgSmallTopAppBar(
        subTitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        backText = stringResource(R.string.delete_wallet_back).uppercase(),
        backContentDescriptionText = stringResource(R.string.delete_wallet_back_content_description),
        onBack = onBack,
    )
}

@Composable
private fun DeleteWalletContent(
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val appName = stringResource(id = R.string.app_name)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopScreenLogoTitle(
            title = stringResource(R.string.delete_wallet_title, appName),
            logoContentDescription = stringResource(R.string.zcash_logo_content_description)
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingBig))

        Text(
            text = stringResource(R.string.delete_wallet_text_1),
            style = ZcashTheme.extendedTypography.deleteWalletWarnStyle
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingUpLarge))

        Body(
            text =
                stringResource(
                    R.string.delete_wallet_text_2,
                    appName
                )
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        val checkedState = rememberSaveable { mutableStateOf(false) }
        Row(Modifier.fillMaxWidth()) {
            LabeledCheckBox(
                checked = checkedState.value,
                onCheckedChange = {
                    checkedState.value = it
                },
                text = stringResource(R.string.delete_wallet_acknowledge),
            )
        }

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        PrimaryButton(
            onClick = onConfirm,
            text = stringResource(R.string.delete_wallet_button, appName).uppercase(),
            enabled = checkedState.value,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingHuge))
    }
}
