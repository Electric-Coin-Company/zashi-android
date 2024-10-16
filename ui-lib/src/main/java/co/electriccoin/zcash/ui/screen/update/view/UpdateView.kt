package co.electriccoin.zcash.ui.screen.update.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GridBgScaffold
import co.electriccoin.zcash.ui.design.component.GridBgSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.component.Reference
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.UpdateInfoFixture
import co.electriccoin.zcash.ui.screen.update.UpdateTag
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update.model.UpdateState

@Preview
@Composable
private fun UpdatePreview() {
    ZcashTheme(forceDarkMode = false) {
        Update(
            snackbarHostState = SnackbarHostState(),
            UpdateInfoFixture.new(appUpdateInfo = null),
            onDownload = {},
            onLater = {},
            onReference = {}
        )
    }
}

@Preview
@Composable
private fun UpdateRequiredPreview() {
    ZcashTheme(forceDarkMode = false) {
        Update(
            snackbarHostState = SnackbarHostState(),
            UpdateInfoFixture.new(force = true),
            onDownload = {},
            onLater = {},
            onReference = {}
        )
    }
}

@Preview
@Composable
private fun UpdateAvailableDarkPreview() {
    ZcashTheme(forceDarkMode = true) {
        Update(
            snackbarHostState = SnackbarHostState(),
            UpdateInfoFixture.new(appUpdateInfo = null),
            onDownload = {},
            onLater = {},
            onReference = {}
        )
    }
}

@Preview
@Composable
private fun UpdateRequiredDarkPreview() {
    ZcashTheme(forceDarkMode = true) {
        Update(
            snackbarHostState = SnackbarHostState(),
            UpdateInfoFixture.new(force = true),
            onDownload = {},
            onLater = {},
            onReference = {}
        )
    }
}

@Composable
fun Update(
    snackbarHostState: SnackbarHostState,
    updateInfo: UpdateInfo,
    onDownload: (state: UpdateState) -> Unit,
    onLater: () -> Unit,
    onReference: () -> Unit
) {
    GridBgScaffold(
        topBar = {
            UpdateTopAppBar(updateInfo = updateInfo)
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        bottomBar = {
            UpdateBottomAppBar(
                updateInfo,
                onDownload,
                onLater,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { paddingValues ->
        UpdateContent(
            onReference = onReference,
            updateInfo = updateInfo,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                        start = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                        end = ZcashTheme.dimens.screenHorizontalSpacingRegular
                    )
        )
    }
    UpdateOverlayRunning(updateInfo)
}

@Suppress("MagicNumber")
@Composable
fun UpdateOverlayRunning(updateInfo: UpdateInfo) {
    if (updateInfo.state == UpdateState.Running) {
        Column(
            Modifier
                .background(ZcashTheme.colors.overlay.copy(0.65f))
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null // Set indication to null to disable ripple effect
                ) {}
                .testTag(UpdateTag.PROGRESSBAR_DOWNLOADING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(color = ZcashTheme.colors.overlayProgressBar)
        }
    }
}

@Composable
private fun UpdateTopAppBar(updateInfo: UpdateInfo) {
    GridBgSmallTopAppBar(
        titleText =
            stringResource(
                updateInfo.isForce.let { force ->
                    if (force) {
                        R.string.update_critical_header
                    } else {
                        R.string.update_header
                    }
                }
            ),
    )
}

@Composable
@Suppress("LongMethod")
private fun UpdateBottomAppBar(
    updateInfo: UpdateInfo,
    onDownload: (state: UpdateState) -> Unit,
    onLater: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(
            thickness = DividerDefaults.Thickness,
            color = ZcashTheme.colors.primaryDividerColor
        )

        Column(
            modifier =
                Modifier
                    .padding(
                        top = ZcashTheme.dimens.spacingDefault,
                        bottom = ZcashTheme.dimens.spacingBig,
                        start = ZcashTheme.dimens.screenHorizontalSpacingBig,
                        end = ZcashTheme.dimens.screenHorizontalSpacingBig
                    ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ZashiButton(
                onClick = { onDownload(UpdateState.Running) },
                text = stringResource(R.string.update_download_button),
                modifier =
                    Modifier
                        .testTag(UpdateTag.BTN_DOWNLOAD)
                        .fillMaxWidth(),
                enabled = updateInfo.state != UpdateState.Running,
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

            if (updateInfo.isForce) {
                Text(
                    text = stringResource(R.string.update_later_disabled_button),
                    textAlign = TextAlign.Center,
                    style = ZcashTheme.typography.primary.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = ZcashTheme.colors.textPrimary,
                    modifier =
                        Modifier
                            .padding(all = ZcashTheme.dimens.spacingDefault)
                            .testTag(UpdateTag.BTN_LATER)
                )
            } else {
                Reference(
                    text = stringResource(R.string.update_later_enabled_button),
                    onClick = {
                        if (updateInfo.state != UpdateState.Running) {
                            onLater()
                        } else {
                            // Keep current state
                        }
                    },
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .padding(all = ZcashTheme.dimens.spacingDefault)
                            .testTag(UpdateTag.BTN_LATER)
                )
            }
        }
    }
}

@Composable
@Suppress("LongMethod")
private fun UpdateContent(
    onReference: () -> Unit,
    updateInfo: UpdateInfo,
    modifier: Modifier = Modifier,
) {
    val appName = stringResource(id = R.string.app_name)

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
            imageVector =
                if (updateInfo.isForce) {
                    ImageVector.vectorResource(R.drawable.ic_zashi_logo_sign_warn)
                } else {
                    ImageVector.vectorResource(R.drawable.ic_zashi_logo_update_available)
                },
            colorFilter = ColorFilter.tint(color = ZcashTheme.colors.secondaryColor),
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingBig))

        Header(
            text =
                if (updateInfo.isForce) {
                    stringResource(id = R.string.update_title_required)
                } else {
                    stringResource(id = R.string.update_title_available, appName)
                },
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        Body(
            text =
                if (updateInfo.isForce) {
                    stringResource(id = R.string.update_description_required, appName)
                } else {
                    stringResource(id = R.string.update_description_available, appName)
                },
            textAlign = TextAlign.Center,
            color = ZcashTheme.colors.textDescriptionDark
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Reference(
            text = stringResource(id = R.string.update_link_text),
            onClick = {
                if (updateInfo.state != UpdateState.Running) {
                    onReference()
                } else {
                    // Keep current state
                }
            },
            fontWeight = FontWeight.Normal,
            textStyle = ZcashTheme.typography.primary.bodyMedium,
            textAlign = TextAlign.Center,
            color = ZcashTheme.colors.textDescriptionDark,
            modifier = Modifier.padding(all = ZcashTheme.dimens.spacingDefault)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))
    }
}
