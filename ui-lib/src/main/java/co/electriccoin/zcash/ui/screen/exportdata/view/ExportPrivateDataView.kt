package co.electriccoin.zcash.ui.screen.exportdata.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.LabeledCheckBox
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.TopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.component.TopScreenLogoTitle
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.scaffoldPadding

@Preview("Export Private Data")
@Composable
private fun ExportPrivateDataPreview() {
    ZcashTheme(forceDarkMode = false) {
        ExportPrivateData(
            snackbarHostState = SnackbarHostState(),
            onBack = {},
            onAgree = {},
            onConfirm = {},
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }
}

@Composable
fun ExportPrivateData(
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onAgree: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    topAppBarSubTitleState: TopAppBarSubTitleState,
) {
    BlankBgScaffold(
        topBar = {
            ExportPrivateDataTopAppBar(
                onBack = onBack,
                subTitleState = topAppBarSubTitleState,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        ExportPrivateDataContent(
            onAgree = onAgree,
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
private fun ExportPrivateDataTopAppBar(
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
        navigationAction = {
            TopAppBarBackNavigation(
                backText = stringResource(id = R.string.back_navigation).uppercase(),
                backContentDescriptionText = stringResource(R.string.back_navigation_content_description),
                onBack = onBack
            )
        },
    )
}

@Composable
private fun ExportPrivateDataContent(
    onAgree: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopScreenLogoTitle(
            title = stringResource(R.string.export_data_header),
            logoContentDescription = stringResource(R.string.zcash_logo_content_description)
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingLarge))

        Body(
            modifier = Modifier.testTag(ExportPrivateDataScreenTag.WARNING_TEXT_TAG),
            text = stringResource(R.string.export_data_text_1)
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        Text(
            modifier = Modifier.testTag(ExportPrivateDataScreenTag.ADDITIONAL_TEXT_TAG),
            text = stringResource(R.string.export_data_text_2),
            fontSize = 14.sp
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        val checkedState = rememberSaveable { mutableStateOf(false) }
        Row(Modifier.fillMaxWidth()) {
            LabeledCheckBox(
                checked = checkedState.value,
                onCheckedChange = {
                    checkedState.value = it
                    onAgree(it)
                },
                text = stringResource(R.string.export_data_agree),
                checkBoxTestTag = ExportPrivateDataScreenTag.AGREE_CHECKBOX_TAG
            )
        }

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        ZashiButton(
            onClick = onConfirm,
            text = stringResource(R.string.export_data_confirm),
            enabled = checkedState.value,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
