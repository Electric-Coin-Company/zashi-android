package co.electriccoin.zcash.ui.screen.send.nighthawk.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BalanceText
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.BodyMedium
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TitleLarge
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.send.nighthawk.model.SendAndReviewUiState

@Composable
@Preview
fun ReviewAndSendPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            ReviewAndSend(sendAndReviewUiState = SendAndReviewUiState(), onBack = {}, onSendZCash = {})
        }
    }
}

@Composable
fun ReviewAndSend(
    sendAndReviewUiState: SendAndReviewUiState,
    onBack: () -> Unit,
    onSendZCash: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(dimensionResource(id = R.dimen.screen_standard_margin))
        .verticalScroll(rememberScrollState())
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(dimensionResource(id = R.dimen.back_icon_size))) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.receive_back_content_description))
        }
        Image(painter = painterResource(id = R.drawable.ic_nighthawk_logo), contentDescription = "logo", contentScale = ContentScale.Inside, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
        TitleLarge(text = stringResource(id = R.string.ns_nighthawk), textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
        BodyMedium(text = stringResource(id = R.string.ns_review_and_send), textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally), color = ZcashTheme.colors.surfaceEnd)
        Spacer(modifier = Modifier.height(45.dp))

        // Amount section
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            BalanceText(text = sendAndReviewUiState.amountToSend)
            Spacer(modifier = Modifier.width(4.dp))
            BalanceText(text = sendAndReviewUiState.amountUnit, color = ZcashTheme.colors.surfaceEnd)
        }
        Spacer(modifier = Modifier.width(12.dp))
        if (sendAndReviewUiState.convertedAmountWithCurrency.isNotBlank()) {
            BodyMedium(text = stringResource(id = R.string.ns_around, sendAndReviewUiState.convertedAmountWithCurrency), textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally), color = ZcashTheme.colors.surfaceEnd)
        }
        Spacer(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 40.dp)
        )

        //Memo
        if (sendAndReviewUiState.memo.isNotBlank()) {
            BodyMedium(text = stringResource(id = R.string.ns_memo), color = ZcashTheme.colors.surfaceEnd)
            Spacer(modifier = Modifier.height(10.dp))
            Body(text = sendAndReviewUiState.memo)
            Spacer(modifier = Modifier.height(40.dp))
        }

        // Network
        Divider(
            thickness = 1.dp,
            color = ZcashTheme.colors.surfaceEnd
        )
        Spacer(modifier = Modifier.height(10.dp))
        val network = ZcashNetwork.fromResources(LocalContext.current).networkName.replaceFirstChar { it.titlecase() }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BodyMedium(text = stringResource(id = R.string.ns_network), color = ZcashTheme.colors.surfaceEnd)
            BodyMedium(text = network, color = ZcashTheme.colors.surfaceEnd)
        }

        // Recipient
        Spacer(modifier = Modifier.height(10.dp))
        Divider(
            thickness = 1.dp,
            color = ZcashTheme.colors.surfaceEnd
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BodyMedium(text = stringResource(id = R.string.ns_recipient), color = ZcashTheme.colors.surfaceEnd)
            BodyMedium(text = sendAndReviewUiState.recipientType, color = ZcashTheme.colors.surfaceEnd)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BodyMedium(text = stringResource(id = R.string.ns_address), color = ZcashTheme.colors.surfaceEnd)
            Spacer(modifier = Modifier.width(50.dp))
            val receiverAddress = sendAndReviewUiState.receiverAddress
            BodyMedium(
                text = buildAnnotatedString {
                    if (receiverAddress.length > 20) {
                        withStyle(style = SpanStyle(color = Color.White)) {
                            append(receiverAddress.take(10))
                        }
                        withStyle(style = SpanStyle(color = ZcashTheme.colors.surfaceEnd)) {
                            append(receiverAddress.substring(10, receiverAddress.length - 10))
                        }
                        withStyle(style = SpanStyle(color = Color.White)) {
                            append(receiverAddress.takeLast(10))
                        }
                    } else {
                        withStyle(style = SpanStyle(color = ZcashTheme.colors.surfaceEnd)) {
                            append(receiverAddress)
                        }
                    }
                },
                textAlign = TextAlign.End
            )
        }

        //Sub total
        Spacer(modifier = Modifier.height(10.dp))
        Divider(
            thickness = 1.dp,
            color = ZcashTheme.colors.surfaceEnd
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BodyMedium(text = stringResource(id = R.string.ns_subtotal), color = ZcashTheme.colors.surfaceEnd)
            BodyMedium(text = sendAndReviewUiState.subTotal + " " + sendAndReviewUiState.amountUnit, color = ZcashTheme.colors.surfaceEnd)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BodyMedium(text = stringResource(id = R.string.ns_network_fee), color = ZcashTheme.colors.surfaceEnd)
            Spacer(modifier = Modifier.width(50.dp))
            BodyMedium(text = sendAndReviewUiState.networkFees + " " + sendAndReviewUiState.amountUnit, color = ZcashTheme.colors.surfaceEnd, textAlign = TextAlign.End)
        }

        // Total
        Spacer(modifier = Modifier.height(10.dp))
        Divider(
            thickness = 1.dp,
            color = ZcashTheme.colors.surfaceEnd
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BodyMedium(text = stringResource(id = R.string.ns_total_amount), color = ZcashTheme.colors.surfaceEnd)
            BodyMedium(text = sendAndReviewUiState.totalAmount + " " + sendAndReviewUiState.amountUnit, color = ZcashTheme.colors.surfaceEnd)
        }

        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            onClick = onSendZCash,
            text = stringResource(id = R.string.ns_continue).uppercase(),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .sizeIn(minWidth = dimensionResource(id = R.dimen.button_min_width), minHeight = dimensionResource(id = R.dimen.button_height)),
        )
    }
}
