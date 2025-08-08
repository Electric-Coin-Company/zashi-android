package co.electriccoin.zcash.ui.screen.transactiondetail

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.TickerLocation.HIDDEN
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByAddress
import co.electriccoin.zcash.ui.design.util.stringResByDateTime
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ReceiveShieldedState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ReceiveTransparentState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.SendShieldedState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.SendTransparentState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ShieldingState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.TransactionDetailMemoState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.TransactionDetailMemosState
import java.time.ZonedDateTime

object SendShieldStateFixture {
    @Suppress("MagicNumber")
    fun new(contact: StringResource? = stringRes("Contact")) =
        SendShieldedState(
            contact = contact,
            address = stringResByAddress(value = "Address", abbreviated = true),
            transactionId = stringRes("Transaction ID"),
            onTransactionIdClick = {},
            onTransactionAddressClick = {},
            fee = stringRes(Zatoshi(1011), HIDDEN),
            completedTimestamp = stringResByDateTime(ZonedDateTime.now(), true),
            memo =
                TransactionDetailMemosState(
                    listOf(
                        TransactionDetailMemoState(content = stringRes("Long message ".repeat(20)), onClick = {}),
                        TransactionDetailMemoState(content = stringRes("Short message"), onClick = {}),
                    )
                ),
            note = stringRes("None"),
            isPending = false
        )
}

object SendTransparentStateFixture {
    @Suppress("MagicNumber")
    fun new(contact: StringResource? = stringRes("Contact")) =
        SendTransparentState(
            contact = contact,
            address = stringResByAddress(value = "Address", abbreviated = false),
            addressAbbreviated = stringResByAddress(value = "Address", abbreviated = true),
            transactionId = stringRes("Transaction ID"),
            onTransactionIdClick = {},
            onTransactionAddressClick = {},
            fee = stringRes(Zatoshi(1011), HIDDEN),
            completedTimestamp = stringResByDateTime(ZonedDateTime.now(), true),
            note = stringRes("None"),
            isPending = false
        )
}

object ReceiveShieldedStateFixture {
    @Suppress("MagicNumber")
    fun new(
        memo: TransactionDetailMemosState =
            TransactionDetailMemosState(
                listOf(
                    TransactionDetailMemoState(content = stringRes("Long message ".repeat(20)), onClick = {}),
                    TransactionDetailMemoState(content = stringRes("Short message"), onClick = {}),
                )
            )
    ) = ReceiveShieldedState(
        memo = memo,
        transactionId = stringRes("Transaction ID"),
        onTransactionIdClick = {},
        completedTimestamp = stringResByDateTime(ZonedDateTime.now(), true),
        note = stringRes("None"),
        isPending = false
    )
}

object ReceiveTransparentStateFixture {
    @Suppress("MagicNumber")
    fun new() =
        ReceiveTransparentState(
            transactionId = stringRes("Transaction ID"),
            onTransactionIdClick = {},
            completedTimestamp = stringResByDateTime(ZonedDateTime.now(), true),
            note = stringRes("None"),
            isPending = false
        )
}

object ShieldingStateFixture {
    @Suppress("MagicNumber")
    fun new() =
        ShieldingState(
            transactionId = stringRes("Transaction ID"),
            onTransactionIdClick = {},
            completedTimestamp = stringResByDateTime(ZonedDateTime.now(), true),
            fee = stringRes(Zatoshi(1011), HIDDEN),
            note = stringRes("None"),
            isPending = false
        )
}
