package co.electriccoin.zcash.ui.screen.send.view

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.ZecSendExt
import cash.z.ecc.android.sdk.model.ZecString
import cash.z.ecc.android.sdk.model.ZecStringExt
import cash.z.ecc.android.sdk.model.toZecString
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TimedButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.ZcashTheme.dimens
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

/**
 * @param pressAndHoldInteractionSource This is an argument that can be injected for automated testing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Send(
    mySpendableBalance: Zatoshi,
    goBack: () -> Unit,
    onCreateAndSend: (ZecSend) -> Unit,
    pressAndHoldInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() }
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
    }) { paddingValues ->
        SendMainContent(
            myBalance = mySpendableBalance,
            sendStage = sendStage,
            pressAndHoldInteractionSource = pressAndHoldInteractionSource,
            setSendStage = setSendStage,
            onCreateAndSend = onCreateAndSend,
            modifier = Modifier
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    top = paddingValues.calculateTopPadding() + dimens.spacingDefault,
                    bottom = dimens.spacingDefault,
                    start = dimens.spacingDefault,
                    end = dimens.spacingDefault
                )
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SendTopAppBar(onBack: () -> Unit) {
    TopAppBar(
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

@Suppress("LongParameterList")
@Composable
private fun SendMainContent(
    myBalance: Zatoshi,
    sendStage: SendStage,
    pressAndHoldInteractionSource: MutableInteractionSource,
    setSendStage: (SendStage) -> Unit,
    onCreateAndSend: (ZecSend) -> Unit,
    modifier: Modifier = Modifier
) {
    val (zecSend, setZecSend) = rememberSaveable(stateSaver = ZecSend.Saver) { mutableStateOf(null) }

    if (sendStage == SendStage.Form || null == zecSend) {
        SendForm(
            myBalance = myBalance,
            previousZecSend = zecSend,
            onCreateAndSend = {
                setSendStage(SendStage.Confirmation)
                setZecSend(it)
            },
            modifier = modifier
        )
    } else {
        Confirmation(
            zecSend = zecSend,
            pressAndHoldInteractionSource = pressAndHoldInteractionSource,
            onConfirmation = {
                onCreateAndSend(zecSend)
            },
            modifier = modifier
        )
    }
}

// TODO [#217]: Need to handle changing of Locale after user input, but before submitting the button.
// TODO [#288]: TextField component can't do long-press backspace.
// TODO [#294]: DetektAll failed LongMethod
@Suppress("LongMethod")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SendForm(
    myBalance: Zatoshi,
    previousZecSend: ZecSend?,
    onCreateAndSend: (ZecSend) -> Unit,
    modifier: Modifier = Modifier
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

    var validation by rememberSaveable {
        mutableStateOf<Set<ZecSendExt.ZecSendValidation.Invalid.ValidationError>>(emptySet())
    }

    Column(
        modifier
            .fillMaxHeight()
    ) {
        Header(
            text = stringResource(id = R.string.send_balance, myBalance.toZecString()),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Body(
            text = stringResource(id = R.string.send_balance_subtitle),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimens.spacingLarge))

        TextField(
            value = amountZecString,
            onValueChange = { newValue ->
                if (!ZecStringExt.filterContinuous(context, monetarySeparators, newValue)) {
                    return@TextField
                }
                amountZecString = newValue.filter { allowedCharacters.contains(it) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(id = R.string.send_amount)) },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.size(8.dp))

        TextField(
            value = recipientAddressString,
            onValueChange = { recipientAddressString = it },
            label = { Text(stringResource(id = R.string.send_to)) },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.size(8.dp))

        TextField(
            value = memoString,
            onValueChange = {
                if (Memo.isWithinMaxLength(it)) {
                    memoString = it
                }
            },
            label = { Text(stringResource(id = R.string.send_memo)) },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.fillMaxHeight(MINIMAL_WEIGHT))

        if (validation.isNotEmpty()) {
            /*
             * Note: this is not localized in that it uses the enum constant name and joins the string
             * without regard for RTL.  This will get resolved once we do proper validation for
             * the fields.
             */
            Text(
                text = validation.joinToString(", "),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(dimens.spacingDefault))

        PrimaryButton(
            onClick = {
                val zecSendValidation = ZecSendExt.new(
                    context,
                    recipientAddressString,
                    amountZecString,
                    memoString,
                    monetarySeparators
                )

                when (zecSendValidation) {
                    is ZecSendExt.ZecSendValidation.Valid -> onCreateAndSend(zecSendValidation.zecSend)
                    is ZecSendExt.ZecSendValidation.Invalid -> validation = zecSendValidation.validationErrors
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
private fun Confirmation(
    zecSend: ZecSend,
    pressAndHoldInteractionSource: MutableInteractionSource,
    onConfirmation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            stringResource(
                R.string.send_amount_and_address_format,
                zecSend.amount.toZecString(),
                zecSend.destination.abbreviated()
            )
        )

        TimedButton(
            onClick = onConfirmation,
            {
                Text(text = stringResource(id = R.string.send_confirm))
            },
            interactionSource = pressAndHoldInteractionSource
        )
    }
}
