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
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarCloseNavigation
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItem
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.HyperBlue
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.orDark
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
private fun BottomSection(state: ConnectKeystoneState) {
    Column {
        ZashiButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Ready to Scan", // TODO keystone strings
            onClick = state.onContinueClick
        )
    }
}

@Composable
private fun HowToConnectSection() {
    val listItemContentPadding = PaddingValues(top = 8.dp, end = 20.dp, bottom = 8.dp)
    Column {
        Text(
            "Instructions:", // TODO keystone strings
            style = ZashiTypography.textLg,
            color = ZashiColors.Text.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        ZashiListItem(
            title = "Unlock your Keystone", // TODO keystone strings
            contentPadding = listItemContentPadding,
            icon = R.drawable.ic_connect_keystone_1
        )
        ZashiListItem(
            title = "Tap the menu icon", // TODO keystone strings
            contentPadding = listItemContentPadding,
            icon = R.drawable.ic_connect_keystone_2
        )
        ZashiListItem(
            title = "Select Watch-only Wallet", // TODO keystone strings
            contentPadding = listItemContentPadding,
            icon = R.drawable.ic_connect_keystone_3
        )
        ZashiListItem(
            title = "Select Zashi app and scan QR code", // TODO keystone strings
            contentPadding = listItemContentPadding,
            icon = R.drawable.ic_connect_keystone_4
        )
    }
}

@Composable
private fun HeaderSection(state: ConnectKeystoneState) {
    Column {
        Image(
            modifier = Modifier.height(32.dp),
            painter = painterResource(co.electriccoin.zcash.ui.design.R.drawable.image_keystone),
            contentDescription = null
        )
        Spacer(Modifier.height(24.dp))
        Text(
            "Connect Hardware Wallet", // TODO keystone strings
            style = ZashiTypography.header6,
            color = ZashiColors.Text.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            buildAnnotatedString {
                append("Scan your device’s QR code to connect. ") // TODO keystone strings
                appendLine()
                appendLine()
                withLink(
                    LinkAnnotation.Clickable("CLICKABLE") { state.onViewKeystoneTutorialClicked() }
                ) {
                    withStyle(
                        style = ZashiTypography.textSm
                            .copy(
                                textDecoration = TextDecoration.Underline,
                                fontWeight = FontWeight.SemiBold,
                                color = HyperBlue.`700` orDark HyperBlue.`400`,
                            )
                            .toSpanStyle()
                    ) {
                        append("View Keystone tutorial") // TODO keystone strings
                    }
                }
            },
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textTertiary,
        )
    }
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
