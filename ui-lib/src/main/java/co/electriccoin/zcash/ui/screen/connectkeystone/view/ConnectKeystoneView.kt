package co.electriccoin.zcash.ui.screen.connectkeystone.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiListItem
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarCloseNavigation
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.HyperBlue
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.screen.connectkeystone.model.ConnectKeystoneState

@Composable
fun ConnectKeystoneView(state: ConnectKeystoneState) {
    BlankBgScaffold(
        topBar = {
            ZashiSmallTopAppBar(
                navigationAction = {
                    ZashiTopAppBarCloseNavigation(state.onBackClick)
                }
            )
        }
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .scaffoldPadding(it)
        ) {
            HeaderSection(state)
            Spacer(Modifier.height(24.dp))
            HowToConnectSection()
            Spacer(Modifier.height(24.dp))
            Spacer(Modifier.weight(1f))
            BottomSection(state)
        }
    }
}

@Composable
private fun ColumnScope.BottomSection(state: ConnectKeystoneState) {
    Image(
        modifier = Modifier.fillMaxWidth(),
        painter = painterResource(id = R.drawable.image_keystone_security),
        contentDescription = null
    )
    Spacer(Modifier.height(24.dp))
    ZashiButton(
        modifier = Modifier.fillMaxWidth(),
        text = "Continue",
        onClick = state.onContinueClick
    )
}

@Composable
private fun ColumnScope.HowToConnectSection() {
    Text(
        "How to connect:",
        style = ZashiTypography.textLg,
        color = ZashiColors.Text.textPrimary,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(Modifier.height(4.dp))
    ZashiListItem(
        text = "Unlock your Keystone",
        contentPadding = PaddingValues(top = 8.dp, end = 20.dp, bottom = 8.dp),
        icon = R.drawable.ic_connect_keystone_1
    )
    ZashiListItem(
        text = "Tap the menu icon",
        contentPadding = PaddingValues(top = 8.dp, end = 20.dp, bottom = 8.dp),
        icon = R.drawable.ic_connect_keystone_2
    )
    ZashiListItem(
        text = "Select Watch-only Wallet",
        contentPadding = PaddingValues(top = 8.dp, end = 20.dp, bottom = 8.dp),
        icon = R.drawable.ic_connect_keystone_3
    )
    ZashiListItem(
        text = "Select Zashi app",
        contentPadding = PaddingValues(top = 8.dp, end = 20.dp, bottom = 8.dp),
        icon = R.drawable.ic_connect_keystone_4
    )
}

@Composable
private fun ColumnScope.HeaderSection(state: ConnectKeystoneState) {
    Image(
        modifier = Modifier.height(32.dp),
        painter = painterResource(R.drawable.image_keystone_header),
        contentDescription = null
    )
    Spacer(Modifier.height(24.dp))
    Text(
        "Connect Hardware Wallet",
        style = ZashiTypography.header6,
        color = ZashiColors.Text.textPrimary,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(Modifier.height(8.dp))
    Text(
        buildAnnotatedString {
            append("Connect an airgapped hardware wallet that communicates through QR-code.")
            appendLine()
            appendLine()
            append("Have questions?")
            appendLine()
            withLink(
                LinkAnnotation.Clickable("CLICKABLE") { state.onViewKeystoneTutorialClicked() }
            ) {
                withStyle(
                    style =
                        SpanStyle(
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.SemiBold,
                            color = HyperBlue.`700`
                        )
                ) {
                    append("View Keystone tutorial")
                }
            }
        },
        style = ZashiTypography.textSm,
        color = ZashiColors.Text.textTertiary,
    )
}

@PreviewScreens
@Composable
private fun ConnectKeystoneViewPreview() =
    ZcashTheme {
        ConnectKeystoneView(
            state =
                ConnectKeystoneState(
                    onBackClick = {},
                    onContinueClick = {},
                    onViewKeystoneTutorialClicked = {}
                )
        )
    }
