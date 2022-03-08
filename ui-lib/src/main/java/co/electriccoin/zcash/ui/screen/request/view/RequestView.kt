package co.electriccoin.zcash.ui.screen.request.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cash.z.ecc.sdk.fixture.WalletAddressFixture
import cash.z.ecc.sdk.model.MonetarySeparators
import cash.z.ecc.sdk.model.WalletAddress
import cash.z.ecc.sdk.model.Zatoshi
import cash.z.ecc.sdk.model.ZecRequest
import cash.z.ecc.sdk.model.ZecRequestMessage
import cash.z.ecc.sdk.model.ZecString
import cash.z.ecc.sdk.model.fromZecString
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TextField
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import kotlinx.coroutines.runBlocking

@Composable
fun PreviewRequest() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            Request(
                myAddress = runBlocking { WalletAddressFixture.unified() },
                goBack = {},
                onCreateAndSend = {}
            )
        }
    }
}

/**
 * @param myAddress The address that ZEC should be sent to.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Request(
    myAddress: WalletAddress.Unified,
    goBack: () -> Unit,
    onCreateAndSend: (ZecRequest) -> Unit,
) {
    Scaffold(topBar = {
        RequestTopAppBar(onBack = goBack)
    }) {
        RequestMainContent(
            myAddress,
            onCreateAndSend = onCreateAndSend
        )
    }
}

@Composable
private fun RequestTopAppBar(onBack: () -> Unit) {
    SmallTopAppBar(
        title = { Text(text = stringResource(id = R.string.request_title)) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.request_back_content_description)
                )
            }
        }
    )
}

// TODO [#215]: Need to add some UI to explain to the user if a request is invalid
// TODO [#217]: Need to handle changing of Locale after user input, but before submitting the button.
@Composable
private fun RequestMainContent(
    myAddress: WalletAddress.Unified,
    onCreateAndSend: (ZecRequest) -> Unit
) {
    val monetarySeparators = MonetarySeparators.current()
    val allowedCharacters = ZecString.allowedCharacters(monetarySeparators)

    var amountZecString by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }

    Column(Modifier.fillMaxHeight()) {
        TextField(
            value = amountZecString,
            onValueChange = { newValue ->
                // TODO [#218]: this doesn't prevent illegal input. So users could still type `1.2.3.4`
                amountZecString = newValue.filter { allowedCharacters.contains(it) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(id = R.string.request_amount)) }
        )

        Spacer(Modifier.size(8.dp))

        TextField(value = message, onValueChange = {
            if (it.length <= ZecRequestMessage.MAX_MESSAGE_LENGTH) {
                message = it
            }
        }, label = { Text(stringResource(id = R.string.request_message)) })

        Spacer(Modifier.fillMaxHeight(MINIMAL_WEIGHT))

        val zatoshi = Zatoshi.fromZecString(amountZecString, monetarySeparators)

        PrimaryButton(
            onClick = {
                onCreateAndSend(ZecRequest(myAddress, zatoshi!!, ZecRequestMessage(message)))
            },
            text = stringResource(id = R.string.request_create),
            enabled = null != zatoshi
        )
    }
}
