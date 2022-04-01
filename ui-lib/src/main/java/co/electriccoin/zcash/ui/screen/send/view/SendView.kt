package co.electriccoin.zcash.ui.screen.send.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.sdk.ext.ui.ZecStringExt
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import cash.z.ecc.sdk.model.Memo
import cash.z.ecc.sdk.model.MonetarySeparators
import cash.z.ecc.sdk.model.Zatoshi
import cash.z.ecc.sdk.model.ZecSend
import cash.z.ecc.sdk.model.ZecSendValidation
import cash.z.ecc.sdk.model.ZecString
import cash.z.ecc.sdk.model.new
import cash.z.ecc.sdk.model.toZecString
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TextField
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.send.ext.ABBREVIATION_INDEX
import co.electriccoin.zcash.ui.screen.send.ext.Saver
import co.electriccoin.zcash.ui.screen.send.ext.abbreviated
import co.electriccoin.zcash.ui.screen.send.model.SendStage

@Composable
@Preview
fun PreviewSend() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            Send(
                mySpendableBalance = ZatoshiFixture.new(),
                goBack = {},
                onCreateAndSend = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Send(
    mySpendableBalance: Zatoshi,
    goBack: () -> Unit,
    onCreateAndSend: (ZecSend) -> Unit,
) {
    // For now, we're avoiding sub-navigation to keep the navigation logic simple.  But this might
    // change once deep-linking support  is added.  It depends on whether deep linking should do one of:
    // 1. Use a different UI flow entirely
    // 2. Show a pre-filled Send form
    // 3. Go directly to the press-and-hold confirmation
    val (sendStage, setSendStage) = rememberSaveable { mutableStateOf(SendStage.Form) }

    Scaffold(topBar = {
        SendTopAppBar(onBack = {
            when (sendStage) {
                SendStage.Form -> goBack()
                SendStage.Confirmation -> setSendStage(SendStage.Form)
            }
        })
    }) {
        SendMainContent(
            mySpendableBalance,
            sendStage,
            setSendStage,
            onCreateAndSend = onCreateAndSend
        )
    }
}

@Composable
private fun SendTopAppBar(onBack: () -> Unit) {
    SmallTopAppBar(
        title = { Text(text = stringResource(id = R.string.send_title)) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.send_back_content_description)
                )
            }
        }
    )
}

@Composable
private fun SendMainContent(
    myBalance: Zatoshi,
    sendStage: SendStage,
    setSendStage: (SendStage) -> Unit,
    onCreateAndSend: (ZecSend) -> Unit
) {
    val (zecSend, setZecSend) = rememberSaveable(stateSaver = ZecSend.Saver) { mutableStateOf(null) }

    if (sendStage == SendStage.Form || null == zecSend) {
        SendForm(
            myBalance = myBalance,
            previousZecSend = zecSend,
            onCreateAndSend = {
                setSendStage(SendStage.Confirmation)
                setZecSend(it)
            }
        )
    } else {
        Confirmation(zecSend) {
            onCreateAndSend(zecSend)
        }
    }
}

// TODO [#217]: Need to handle changing of Locale after user input, but before submitting the button.
// TODO [#288]: TextField component can't do long-press backspace.
// TODO [#294]: DetektAll failed LongMethod
@Suppress("LongMethod")
@Composable
private fun SendForm(
    myBalance: Zatoshi,
    previousZecSend: ZecSend?,
    onCreateAndSend: (ZecSend) -> Unit
) {
    val context = LocalContext.current
    val monetarySeparators = MonetarySeparators.current()
    val allowedCharacters = ZecString.allowedCharacters(monetarySeparators)

    var amountZecString by rememberSaveable {
        mutableStateOf(previousZecSend?.amount?.toZecString() ?: "")
    }
    var recipientAddressString by rememberSaveable {
        mutableStateOf(previousZecSend?.destination?.address ?: "")
    }
    var memoString by rememberSaveable { mutableStateOf(previousZecSend?.memo?.value ?: "") }

    var validation by rememberSaveable { mutableStateOf<Set<ZecSendValidation.Invalid.ValidationError>>(emptySet()) }

    Column(Modifier.fillMaxHeight()) {
        Row(Modifier.fillMaxWidth()) {
            Text(text = myBalance.toZecString())
        }

        TextField(
            value = amountZecString,
            onValueChange = { newValue ->
                if (!ZecStringExt.filterContinuous(context, monetarySeparators, newValue)) {
                    return@TextField
                }
                amountZecString = newValue.filter { allowedCharacters.contains(it) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(id = R.string.send_amount)) }
        )

        Spacer(Modifier.size(8.dp))

        TextField(
            value = recipientAddressString,
            onValueChange = { recipientAddressString = it },
            label = { Text(stringResource(id = R.string.send_to)) }
        )

        Spacer(Modifier.size(8.dp))

        TextField(value = memoString, onValueChange = {
            if (Memo.isWithinMaxLength(it)) {
                memoString = it
            }
        }, label = { Text(stringResource(id = R.string.send_memo)) })

        Spacer(Modifier.fillMaxHeight(MINIMAL_WEIGHT))

        if (validation.isNotEmpty()) {
            /*
             * Note: this is not localized in that it uses the enum constant name and joins the string
             * without regard for RTL.  This will get resolved once we do proper validation for
             * the fields.
             */
            Text(validation.joinToString(", "))
        }

        PrimaryButton(
            onClick = {
                val zecSendValidation = ZecSend.new(
                    recipientAddressString,
                    amountZecString,
                    memoString,
                    monetarySeparators
                )

                when (zecSendValidation) {
                    is ZecSendValidation.Valid -> onCreateAndSend(zecSendValidation.zecSend)
                    is ZecSendValidation.Invalid -> validation = zecSendValidation.validationErrors
                }
            },
            text = stringResource(id = R.string.send_create),
            // Check for ABBREVIATION_INDEX goes away once proper address validation is in place.
            // For now, it just prevents a crash on the confirmation screen.
            enabled = amountZecString.isNotBlank() && recipientAddressString.length > ABBREVIATION_INDEX
        )
    }
}

@Composable
private fun Confirmation(zecSend: ZecSend, onConfirmation: () -> Unit) {
    Column {
        Text(
            stringResource(
                R.string.send_amount_and_address_format,
                zecSend.amount.toZecString(),
                zecSend.destination.abbreviated()
            )
        )

        // TODO [#249]: Implement press-and-hold
        Button(onClick = onConfirmation) {
            Text(text = stringResource(id = R.string.send_confirm))
        }
    }
}
