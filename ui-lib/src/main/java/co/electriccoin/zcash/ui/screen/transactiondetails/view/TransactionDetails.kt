package co.electriccoin.zcash.ui.screen.transactiondetails.view

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
import cash.z.ecc.android.sdk.ext.isShielded
import cash.z.ecc.android.sdk.fixture.TransactionOverviewFixture
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.model.TransactionState
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.android.sdk.model.toZecString
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.AlertDialog
import co.electriccoin.zcash.ui.common.blockExplorerUrlStringId
import co.electriccoin.zcash.ui.common.toFormattedString
import co.electriccoin.zcash.ui.design.component.BalanceText
import co.electriccoin.zcash.ui.design.component.BodyMedium
import co.electriccoin.zcash.ui.design.component.DottedBorderTextButton
import co.electriccoin.zcash.ui.design.component.TitleLarge
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.transactiondetails.model.TransactionDetailsUIModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Preview
@Composable
fun TransactionDetailsPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            val transactionDetailsUIModel = TransactionDetailsUIModel(
                transactionOverview = TransactionOverviewFixture.new(),
                transactionRecipient = TransactionRecipient.Address("jhasdgjhagsdjagsjadjhgasjhdgajshdgjahsgdjasgdjasgdjsad"),
                network = ZcashNetwork.Mainnet,
                networkHeight = BlockHeight.new(
                    zcashNetwork = ZcashNetwork.Mainnet,
                    blockHeight = ZcashNetwork.Mainnet.saplingActivationHeight.value + 10
                )
            )
            TransactionDetails(
                transactionDetailsUIModel = transactionDetailsUIModel,
                onBack = {},
                viewOnBlockExplorer = { _, _ -> },
                isNavigateAwayFromAppWarningShown = false
            )
        }
    }
}

@Composable
fun TransactionDetails(
    transactionDetailsUIModel: TransactionDetailsUIModel?,
    isNavigateAwayFromAppWarningShown: Boolean,
    onBack: () -> Unit,
    viewOnBlockExplorer: (url: String, updateWarningStatus: Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.screen_standard_margin))
            .verticalScroll(rememberScrollState())
    ) {
        val context = LocalContext.current
        val showAppLeavingDialog = remember {
            mutableStateOf(false)
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier.size(dimensionResource(id = R.dimen.back_icon_size))
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = stringResource(R.string.receive_back_content_description)
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_nighthawk_logo),
            contentDescription = "logo",
            contentScale = ContentScale.Inside,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
        TitleLarge(
            text = stringResource(id = R.string.ns_nighthawk),
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
        BodyMedium(
            text = stringResource(id = R.string.ns_transaction_details),
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = ZcashTheme.colors.surfaceEnd
        )
        Spacer(modifier = Modifier.height(38.dp))

        if (transactionDetailsUIModel == null) {
            Twig.info { "Transaction overview ui model is null" }
            return@Column
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_icon_downloading),
            contentDescription = null,
            modifier = Modifier
                .rotate(if (transactionDetailsUIModel.transactionOverview.isSentTransaction) 180f else 0f)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(21.dp))

        // Amount section
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            BalanceText(text = (transactionDetailsUIModel.transactionOverview.netValue - transactionDetailsUIModel.transactionOverview.feePaid).toZecString())
            Spacer(modifier = Modifier.width(4.dp))
            BalanceText(
                text = stringResource(id = R.string.ns_zec),
                color = ZcashTheme.colors.surfaceEnd
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        BodyMedium(
            text = stringResource(id = R.string.ns_around, "--"),
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = ZcashTheme.colors.surfaceEnd
        )
        Spacer(
            modifier = Modifier.height(30.dp)
        )

        val (transactionStateTextId, transactionStateIconId) = when (transactionDetailsUIModel.transactionOverview.transactionState) {
            TransactionState.Confirmed -> Pair(R.string.ns_confirmed, R.drawable.ic_icon_confirmed)
            TransactionState.Pending -> Pair(R.string.ns_pending, R.drawable.ic_icon_preparing)
            TransactionState.Expired -> Pair(R.string.ns_expired, R.drawable.ic_done_24dp)
        }

        DottedBorderTextButton(
            onClick = {},
            text = stringResource(id = transactionStateTextId),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(40.dp),
            borderColor = ZcashTheme.colors.surfaceEnd,
            startIcon = transactionStateIconId
        )

        Spacer(
            modifier = Modifier.heightIn(min = 50.dp)
        )

        // Memo
        if (transactionDetailsUIModel.memo.isNotBlank()) {
            BodyMedium(
                text = stringResource(id = R.string.ns_memo),
                color = ZcashTheme.colors.surfaceEnd
            )
            Spacer(modifier = Modifier.height(10.dp))
            BodyMedium(text = transactionDetailsUIModel.memo)
            Spacer(modifier = Modifier.height(40.dp))
        }

        // Time
        Divider(
            thickness = 1.dp,
            color = ZcashTheme.colors.surfaceEnd
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BodyMedium(
                text = stringResource(id = R.string.ns_time_utc),
                color = ZcashTheme.colors.surfaceEnd
            )
            BodyMedium(
                text = Instant.fromEpochSeconds(transactionDetailsUIModel.transactionOverview.blockTimeEpochSeconds)
                    .toLocalDateTime(TimeZone.UTC).toString().replace("T", " "),
                color = ZcashTheme.colors.surfaceEnd
            )
        }

        // Network
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
            BodyMedium(
                text = stringResource(id = R.string.ns_network),
                color = ZcashTheme.colors.surfaceEnd
            )
            BodyMedium(
                text = transactionDetailsUIModel.network.networkName,
                color = ZcashTheme.colors.surfaceEnd
            )
        }

        // BlockId
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
            BodyMedium(
                text = stringResource(id = R.string.ns_block_id),
                color = ZcashTheme.colors.surfaceEnd
            )
            BodyMedium(
                text = "${transactionDetailsUIModel.transactionOverview.minedHeight?.value}",
                color = ZcashTheme.colors.surfaceEnd
            )
        }

        // Confirmations
        val countText = getCountText(transactionDetailsUIModel)
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
            BodyMedium(
                text = stringResource(id = R.string.ns_confirmations),
                color = ZcashTheme.colors.surfaceEnd
            )
            BodyMedium(text = countText, color = ZcashTheme.colors.surfaceEnd)
        }

        // TransactionId
        Spacer(modifier = Modifier.height(10.dp))
        val transactionId =
            transactionDetailsUIModel.transactionOverview.rawId.byteArray.toFormattedString()
        Divider(
            thickness = 1.dp,
            color = ZcashTheme.colors.surfaceEnd
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BodyMedium(
                text = stringResource(id = R.string.ns_transaction_id),
                color = ZcashTheme.colors.surfaceEnd
            )
            Spacer(modifier = Modifier.width(50.dp))
            BodyMedium(
                text = transactionId,
                color = ZcashTheme.colors.surfaceEnd,
                textAlign = TextAlign.End
            )
        }

        val onViewBlockExplorerClicked = { updateWarningStatus: Boolean ->
            viewOnBlockExplorer(
                context.getString(
                    transactionDetailsUIModel.network.blockExplorerUrlStringId(),
                    transactionId
                ),
                updateWarningStatus
            )
        }
        TextButton(
            onClick = {
                if (isNavigateAwayFromAppWarningShown) {
                    onViewBlockExplorerClicked(false)
                } else {
                    showAppLeavingDialog.value = true
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            BodyMedium(
                text = stringResource(id = R.string.ns_view_block_explorer),
                color = ZcashTheme.colors.onBackgroundHeader,
                textAlign = TextAlign.End
            )
        }

        // Recipient
        if (transactionDetailsUIModel.transactionOverview.isSentTransaction) {
            val recipientAddress = when (transactionDetailsUIModel.transactionRecipient) {
                is TransactionRecipient.Account -> ""
                is TransactionRecipient.Address -> transactionDetailsUIModel.transactionRecipient.addressValue
            }
            if (recipientAddress.isNotBlank()) {
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
                    BodyMedium(
                        text = stringResource(id = R.string.ns_recipient),
                        color = ZcashTheme.colors.surfaceEnd
                    )
                    BodyMedium(
                        text = if (recipientAddress.isShielded()) stringResource(id = R.string.ns_shielded) else stringResource(
                            id = R.string.ns_transparent
                        ), color = ZcashTheme.colors.surfaceEnd
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BodyMedium(
                        text = stringResource(id = R.string.ns_address),
                        color = ZcashTheme.colors.surfaceEnd
                    )
                    Spacer(modifier = Modifier.width(50.dp))
                    BodyMedium(
                        text = buildAnnotatedString {
                            if (recipientAddress.length > 20) {
                                withStyle(style = SpanStyle(color = Color.White)) {
                                    append(recipientAddress.take(10))
                                }
                                withStyle(style = SpanStyle(color = ZcashTheme.colors.surfaceEnd)) {
                                    append(
                                        recipientAddress.substring(
                                            10,
                                            recipientAddress.length - 10
                                        )
                                    )
                                }
                                withStyle(style = SpanStyle(color = Color.White)) {
                                    append(recipientAddress.takeLast(10))
                                }
                            } else {
                                withStyle(style = SpanStyle(color = ZcashTheme.colors.surfaceEnd)) {
                                    append(recipientAddress)
                                }
                            }
                        },
                        textAlign = TextAlign.End
                    )
                }
            }
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
            BodyMedium(
                text = stringResource(id = R.string.ns_subtotal),
                color = ZcashTheme.colors.surfaceEnd
            )
            BodyMedium(
                text = (transactionDetailsUIModel.transactionOverview.netValue - transactionDetailsUIModel.transactionOverview.feePaid).toZecString() + stringResource(
                    id = R.string.ns_zec
                ), color = ZcashTheme.colors.surfaceEnd
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BodyMedium(
                text = stringResource(id = R.string.ns_network_fee),
                color = ZcashTheme.colors.surfaceEnd
            )
            Spacer(modifier = Modifier.width(50.dp))
            BodyMedium(
                text = (transactionDetailsUIModel.transactionOverview.feePaid).toZecString() + stringResource(
                    id = R.string.ns_zec
                ), color = ZcashTheme.colors.surfaceEnd, textAlign = TextAlign.End
            )
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
            BodyMedium(
                text = stringResource(id = R.string.ns_total_amount),
                color = ZcashTheme.colors.surfaceEnd
            )
            BodyMedium(
                text = (transactionDetailsUIModel.transactionOverview.netValue).toZecString() + stringResource(
                    id = R.string.ns_zec
                ), color = ZcashTheme.colors.surfaceEnd
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Divider(
            thickness = 1.dp,
            color = ZcashTheme.colors.surfaceEnd
        )

        if (showAppLeavingDialog.value) {
            AlertDialog(
                title = stringResource(id = R.string.dialog_first_use_view_tx_title),
                desc = stringResource(id = R.string.dialog_first_use_view_tx_message),
                confirmText = stringResource(id = R.string.dialog_first_use_view_tx_positive),
                dismissText = stringResource(id = R.string.ns_cancel),
                onConfirm = {
                    onViewBlockExplorerClicked(true)
                    showAppLeavingDialog.value = false
                },
                onDismiss = {
                    showAppLeavingDialog.value = false
                }
            )
        }
    }
}

private fun getCountText(transactionDetailsUIModel: TransactionDetailsUIModel): String {
    val latestBlockHeight = transactionDetailsUIModel.networkHeight
    val minedHeight = transactionDetailsUIModel.transactionOverview.minedHeight
    return if (latestBlockHeight == null) {
        if (isSufficientlyOld(transactionDetailsUIModel)) "Confirmed" else "Transaction Count unavailable"
    } else if (minedHeight != null) {
        "${latestBlockHeight.value - minedHeight.value}"
    } else if ((transactionDetailsUIModel.transactionOverview.expiryHeight?.value
            ?: Long.MAX_VALUE) < latestBlockHeight.value
    ) {
        "Pending"
    } else {
        "Expired"
    }
}

private fun isSufficientlyOld(tx: TransactionDetailsUIModel): Boolean {
    val threshold = 75 * 1000 * 25 // approx 25 blocks
    val delta = System.currentTimeMillis() / 1000L - tx.transactionOverview.blockTimeEpochSeconds
    return (tx.transactionOverview.minedHeight?.value
        ?: Long.MIN_VALUE) > tx.network.saplingActivationHeight.value &&
            delta < threshold
}
