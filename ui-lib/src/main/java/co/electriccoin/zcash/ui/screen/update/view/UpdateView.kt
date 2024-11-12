package co.electriccoin.zcash.ui.screen.update.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarCloseNavigation
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarHamburgerNavigation
import co.electriccoin.zcash.ui.design.component.zashiVerticalGradient
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.fixture.UpdateInfoFixture
import co.electriccoin.zcash.ui.screen.update.UpdateTag.BTN_DOWNLOAD
import co.electriccoin.zcash.ui.screen.update.UpdateTag.BTN_LATER
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update.model.UpdateState

@Composable
fun Update(
    snackbarHostState: SnackbarHostState,
    updateInfo: UpdateInfo,
    onDownload: (state: UpdateState) -> Unit,
    onLater: () -> Unit,
    onReference: () -> Unit,
    onSettings: () -> Unit
) {
    Box(
        modifier =
            Modifier.background(
                zashiVerticalGradient(
                    if (updateInfo.isForce) {
                        ZashiColors.Utility.WarningYellow.utilityOrange100
                    } else {
                        ZashiColors.Utility.Purple.utilityPurple100
                    }
                )
            )
    ) {
        Scaffold(
            topBar = {
                ZashiSmallTopAppBar(
                    title = null,
                    subtitle = null,
                    colors = ZcashTheme.colors.topAppBarColors.copyColors(containerColor = Color.Transparent),
                    navigationAction = {
                        if (updateInfo.isForce.not()) {
                            ZashiTopAppBarCloseNavigation(modifier = Modifier.testTag(BTN_LATER), onBack = onLater)
                        }
                    },
                    hamburgerMenuActions = {
                        if (updateInfo.isForce) {
                            ZashiTopAppBarHamburgerNavigation(onSettings)
                        }
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
            containerColor = Color.Transparent
        ) {
            Column(modifier = Modifier.scaffoldPadding(it)) {
                @Suppress("MagicNumber")
                Spacer(Modifier.weight(.75f))
                Image(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    painter =
                        painterResource(
                            if (updateInfo.isForce) {
                                R.drawable.ic_update_required
                            } else {
                                R.drawable.ic_update
                            }
                        ),
                    contentDescription = ""
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text =
                        if (updateInfo.isForce) {
                            stringResource(id = R.string.update_title_required)
                        } else {
                            stringResource(id = R.string.update_title_available)
                        },
                    style = ZashiTypography.header6,
                    fontWeight = FontWeight.SemiBold,
                    color = ZashiColors.Text.textPrimary
                )
                Spacer(Modifier.height(12.dp))

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text =
                        buildAnnotatedString {
                            append(
                                if (updateInfo.isForce) {
                                    stringResource(id = R.string.update_description_required)
                                } else {
                                    stringResource(id = R.string.update_description_available)
                                }
                            )
                            appendLine()
                            appendLine()

                            withStyle(
                                style =
                                    SpanStyle(
                                        textDecoration = TextDecoration.Underline
                                    )
                            ) {
                                withLink(
                                    LinkAnnotation.Clickable(CLICKABLE_TAG) {
                                        if (updateInfo.state != UpdateState.Running) {
                                            onReference()
                                        }
                                    }
                                ) {
                                    append(stringResource(R.string.update_link_text))
                                }
                            }
                        },
                    style = ZashiTypography.textSm,
                    textAlign = TextAlign.Center,
                    color = ZashiColors.Text.textPrimary,
                )
                Spacer(Modifier.weight(1f))
                ZashiButton(
                    modifier = Modifier.fillMaxWidth().testTag(BTN_DOWNLOAD),
                    text = stringResource(R.string.update_download_button),
                    onClick = { onDownload(UpdateState.Running) },
                    enabled = updateInfo.state != UpdateState.Running,
                    isLoading = updateInfo.state == UpdateState.Running
                )
            }
        }
    }
}

@PreviewScreens
@Composable
private fun UpdatePreview() =
    ZcashTheme {
        Update(
            snackbarHostState = SnackbarHostState(),
            updateInfo = UpdateInfoFixture.new(appUpdateInfo = null),
            onDownload = {},
            onLater = {},
            onReference = {},
            onSettings = {}
        )
    }

@PreviewScreens
@Composable
private fun UpdateRequiredPreview() =
    ZcashTheme {
        Update(
            snackbarHostState = SnackbarHostState(),
            updateInfo = UpdateInfoFixture.new(force = true),
            onDownload = {},
            onLater = {},
            onReference = {},
            onSettings = {}
        )
    }

private const val CLICKABLE_TAG = "clickable"
