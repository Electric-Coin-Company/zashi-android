package co.electriccoin.zcash.ui.screen.update.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.Reference
import co.electriccoin.zcash.ui.design.component.TertiaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.update.RestoreTag
import co.electriccoin.zcash.ui.screen.update.fixture.UpdateInfoFixture
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update.model.UpdateState

@Preview("Update")
@Composable
fun PreviewUpdate() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            Update(
                snackbarHostState = SnackbarHostState(),
                UpdateInfoFixture.new(appUpdateInfo = null),
                onDownload = {},
                onLater = {},
                onReference = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Update(
    snackbarHostState: SnackbarHostState,
    updateInfo: UpdateInfo,
    onDownload: (state: UpdateState) -> Unit,
    onLater: () -> Unit,
    onReference: () -> Unit
) {
    BackHandler(enabled = true) {
        if (updateInfo.isForce) {
            return@BackHandler
        }
        onLater()
    }
    Scaffold(
        topBar = {
            UpdateTopAppBar(updateInfo)
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        bottomBar = {
            UpdateBottomAppBar(
                updateInfo,
                onDownload,
                onLater
            )
        }
    ) {
        UpdateContentNormal(onReference)
    }
    UpdateOverlayRunning(updateInfo)
}

@Suppress("MagicNumber")
@Composable
fun UpdateOverlayRunning(updateInfo: UpdateInfo) {
    if (updateInfo.state == UpdateState.Running) {
        Column(
            Modifier
                .background(ZcashTheme.colors.overlay.copy(0.5f))
                .fillMaxWidth()
                .fillMaxHeight()
                .testTag(RestoreTag.PROGRESSBAR_DOWNLOADING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun UpdateTopAppBar(updateInfo: UpdateInfo) {
    SmallTopAppBar(
        title = {
            Text(
                text = stringResource(
                    updateInfo.isForce.let { force ->
                        if (force) {
                            R.string.update_critical_header
                        } else {
                            R.string.update_header
                        }
                    }
                )
            )
        },
    )
}

@Composable
private fun UpdateBottomAppBar(
    updateInfo: UpdateInfo,
    onDownload: (state: UpdateState) -> Unit,
    onLater: () -> Unit
) {
    Column {
        PrimaryButton(
            onClick = { onDownload(UpdateState.Running) },
            text = stringResource(R.string.update_download_button),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(RestoreTag.BTN_DOWNLOAD),
            enabled = updateInfo.state != UpdateState.Running
        )

        TertiaryButton(
            onClick = onLater,
            text = stringResource(
                updateInfo.isForce.let { force ->
                    if (force) {
                        R.string.update_later_disabled_button
                    } else {
                        R.string.update_later_enabled_button
                    }
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(RestoreTag.BTN_LATER),
            enabled = !updateInfo.isForce && updateInfo.state != UpdateState.Running
        )
    }
}

@Composable
private fun UpdateContentNormal(
    onReference: () -> Unit
) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // TODO [#17]: This suppression and magic number will get replaced once we have real assets
        @Suppress("MagicNumber")
        Image(
            ImageBitmap.imageResource(id = R.drawable.update_main_graphic),
            contentDescription = stringResource(id = R.string.update_image_content_description),
            Modifier.fillMaxSize(0.50f)
        )

        Body(
            text = stringResource(id = R.string.update_description),
            modifier = Modifier
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally)
        )

        Reference(
            text = stringResource(id = R.string.update_link_text),
            modifier = Modifier
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally),
            onClick = {
                onReference()
            }
        )
    }
}
