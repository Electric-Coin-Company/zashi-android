package co.electriccoin.zcash.ui.screen.signkeystonetransaction.view

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ZashiBadge
import co.electriccoin.zcash.ui.design.component.ZashiBadgeDefaults
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiQr
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.listitem.BaseListItem
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemDefaults
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.state.SignKeystoneTransactionState
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.state.ZashiAccountInfoListItemState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SignKeystoneTransactionView(state: SignKeystoneTransactionState) {
    BlankBgScaffold(
        topBar = {
            ZashiSmallTopAppBar(
                title = "Sign Transaction",
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
            ZashiAccountInfoListItem(state.accountInfo)
            Spacer(Modifier.height(32.dp))
            QrContent(state)
            Spacer(Modifier.height(32.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Scan with your Keystone wallet",
                style = ZashiTypography.textMd,
                fontWeight = FontWeight.Medium,
                color = ZashiColors.Text.textPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "After you have signed with Keystone, tap on the Get Signature button below.",
                style = ZashiTypography.textSm,
                color = ZashiColors.Text.textTertiary
            )
            Spacer(Modifier.height(32.dp))
            Spacer(Modifier.weight(1f))
            BottomSection(state)
        }
    }
}

@Composable
private fun ZashiAccountInfoListItem(
    state: ZashiAccountInfoListItemState,
    modifier: Modifier = Modifier,
) {
    val color = ZashiListItemDefaults.secondaryColors()

    BaseListItem(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        leading = {
            ZashiListItemDefaults.LeadingItem(
                modifier = it,
                icon = state.icon,
                contentDescription = state.title.getValue()
            )
        },
        content = {
            ZashiListItemDefaults.ContentItem(
                modifier = it,
                text = state.title.getValue(),
                subtitle = state.subtitle.getValue(),
                titleIcons = persistentListOf()
            )
        },
        trailing = {
            ZashiBadge("Hardware", colors = ZashiBadgeDefaults.hyperBlueColors())
        },
        border = BorderStroke(1.dp, color.borderColor),
        onClick = null
    )
}

@Composable
private fun ColumnScope.QrContent(state: SignKeystoneTransactionState) {
    ZashiQr(qrData = state.qrData, modifier = Modifier.align(CenterHorizontally))
    LaunchedEffect(state.qrData) {
        delay(100.milliseconds)
        state.generateNextQrCode()
    }
}

@Composable
private fun BottomSection(state: SignKeystoneTransactionState, modifier: Modifier = Modifier) {
    Column(
        modifier
    ) {
        ZashiButton(
            modifier = Modifier.fillMaxWidth(),
            state = state.positiveButton
        )
        ZashiButton(
            modifier = Modifier.fillMaxWidth(),
            state = state.negativeButton,
            colors = ZashiButtonDefaults.secondaryColors()
        )
    }
}

@PreviewScreens
@Composable
private fun Preview() = ZcashTheme {
    SignKeystoneTransactionView(
        state = SignKeystoneTransactionState(
            accountInfo = ZashiAccountInfoListItemState(
                icon = R.drawable.ic_settings_info,
                title = stringRes("title"),
                subtitle = stringRes("subtitle"),
            ),
            generateNextQrCode = {},
            qrData = "tralala",
            positiveButton = ButtonState(stringRes("Get Signature")),
            negativeButton = ButtonState(stringRes("Reject")),
            onBack = {}
        )
    )
}
