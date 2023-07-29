package co.electriccoin.zcash.ui.screen.shield.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.AlertDialog
import co.electriccoin.zcash.ui.common.LEARN_UNIFIED_ADDRESSES
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.BodyMedium
import co.electriccoin.zcash.ui.design.component.OutlinedPrimaryButton
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview
@Composable
fun AutoShieldingInfoPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            AutoShieldingInfo(onNext = {}, onLaunchUrl = {})
        }
    }
}

@Composable
fun AutoShieldingInfo(onNext: () -> Unit, onLaunchUrl: (String) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(dimensionResource(id = R.dimen.screen_standard_margin))
                .verticalScroll(rememberScrollState())
        ) {
            val showLearnMoreDialog = remember {
                mutableStateOf(false)
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
            Icon(
                painter = painterResource(id = R.drawable.ic_check_shield),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.CenterHorizontally),
                tint = ZcashTheme.colors.onBackgroundHeader
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
            Body(text = stringResource(id = R.string.autoshielding_title_text))
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
            BodyMedium(text = stringResource(id = R.string.autoshielding_body_text))
            Spacer(modifier = Modifier.height(50.dp))
            PrimaryButton(
                onClick = onNext,
                text = stringResource(id = R.string.autoshielding_button_positive),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.auto_shielding_btn_height))
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedPrimaryButton(
                onClick = { showLearnMoreDialog.value = true },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.auto_shielding_btn_height)),
                text = stringResource(id = R.string.autoshielding_button_neutral)
            )

            // show learn more dialog
            if (showLearnMoreDialog.value) {
                AlertDialog(
                    title = stringResource(id = R.string.link_to_ecc_title),
                    desc = stringResource(id = R.string.link_to_ecc_description),
                    confirmText = stringResource(id = R.string.open_browser),
                    dismissText = stringResource(id = R.string.ns_cancel),
                    onConfirm = {
                        showLearnMoreDialog.value = false
                        onLaunchUrl(LEARN_UNIFIED_ADDRESSES)
                    },
                    onDismiss = { showLearnMoreDialog.value = false }
                )
            }
        }
    }
}