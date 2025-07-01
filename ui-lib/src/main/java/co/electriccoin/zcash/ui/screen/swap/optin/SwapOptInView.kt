package co.electriccoin.zcash.ui.screen.swap.optin

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.CheckboxState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiCheckboxCard
import co.electriccoin.zcash.ui.design.component.ZashiInfoText
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBigCloseNavigation
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun SwapOptInView(state: SwapOptInState) {
    BlankBgScaffold(
        topBar = {
            ZashiSmallTopAppBar(
                navigationAction = {
                    ZashiTopAppBarBigCloseNavigation(state.onBack)
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .scaffoldPadding(padding)
        ) {
            Image(
                painter = painterResource(state.icon),
                contentDescription = null
            )
            Spacer(24.dp)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Swap or Pay with",
                    style = ZashiTypography.textXl,
                    fontWeight = FontWeight.SemiBold,
                    color = ZashiColors.Text.textPrimary
                )
                Spacer(10.dp)
                Image(
                    painter = painterResource(state.logo),
                    contentDescription = null
                )
            }
            Spacer(12.dp)
            Text(
                text = "This feature is powered by a third-party NEAR API. Zashi needs to make networking calls to " +
                    "fetch rates, quotes, execute transactions and check their status in the transaction history which can leak your IP address. We recommend you to allow Tor connection to protect your IP address at all times.",
                style = ZashiTypography.textSm,
                color = ZashiColors.Text.textTertiary,
            )
            Spacer(24.dp)
            ZashiCheckboxCard(
                state = state.thirdParty
            )
            Spacer(20.dp)
            ZashiCheckboxCard(
                state = state.ipAddressProtection
            )
            Spacer(24.dp)
            Spacer(1f)
            ZashiInfoText(
                text = "Note for the super privacy-conscious: Transactions executed via the NEAR API are transparent which means all transaction information is public."
            )
            Spacer(20.dp)
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                state = state.skip,
                colors = ZashiButtonDefaults.secondaryColors()
            )
            Spacer(4.dp)
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                state = state.confirm
            )
        }
    }
}

@Immutable
data class SwapOptInState(
    @DrawableRes val icon: Int = R.drawable.ic_swap_optin_near,
    @DrawableRes val logo: Int = R.drawable.ic_near_logo,
    val thirdParty: CheckboxState,
    val ipAddressProtection: CheckboxState,
    val skip: ButtonState,
    val confirm: ButtonState,
    override val onBack: () -> Unit
): ModalBottomSheetState

@PreviewScreens
@Composable
private fun Preview() = ZcashTheme {
    SwapOptInView(
        SwapOptInState(
            thirdParty = CheckboxState(
                title = stringRes("Allow Third-Party Requests"),
                subtitle = stringRes("Enable API calls to the NEAR API."),
                isChecked = false,
                onClick = {}
            ),
            ipAddressProtection = CheckboxState(
                title = stringRes("Turn on IP Address Protection"),
                subtitle = stringRes("Protect IP address with Tor connection."),
                isChecked = false,
                onClick = {}
            ),
            skip = ButtonState(
                text = stringRes("Skip"),
                onClick = {}
            ),
            confirm = ButtonState(
                text = stringRes("Confirm"),
                onClick = {}
            ),
            onBack = {}
        )
    )
}
