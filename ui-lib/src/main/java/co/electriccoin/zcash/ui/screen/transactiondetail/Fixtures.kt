package co.electriccoin.zcash.ui.screen.transactiondetail

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ReceiveShieldedState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ReceiveTransparentState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.SendShieldedState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.SendTransparentState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ShieldingState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.TransactionDetailMemoState
import java.time.ZonedDateTime

object SendShieldStateFixture {
    @Suppress("MagicNumber")
    fun new(contact: StringResource? = stringRes("Contact")) =
        SendShieldedState(
            contact = contact,
            address = stringRes("Address"),
            transactionId = stringRes("Transaction ID"),
            onTransactionIdClick = {},
            onTransactionAddressClick = {},
            fee = stringRes(Zatoshi(1011)),
            completedTimestamp = stringRes(ZonedDateTime.now()),
            memo =
                TransactionDetailMemoState(
                    listOf(
                        stringRes("Long message ".repeat(20)),
                        stringRes("Short message"),
                    )
                ),
        )
}

object SendTransparentStateFixture {
    @Suppress("MagicNumber")
    fun new(contact: StringResource? = stringRes("Contact")) =
        SendTransparentState(
            contact = contact,
            address = stringRes("Address"),
            transactionId = stringRes("Transaction ID"),
            onTransactionIdClick = {},
            onTransactionAddressClick = {},
            fee = stringRes(Zatoshi(1011)),
            completedTimestamp = stringRes(ZonedDateTime.now()),
        )
}

object ReceiveShieldedStateFixture {
    @Suppress("MagicNumber")
    fun new(
        memo: TransactionDetailMemoState =
            TransactionDetailMemoState(
                listOf(
                    stringRes("Long message ".repeat(20)),
                    stringRes("Short message"),
                )
            )
    ) = ReceiveShieldedState(
        transactionId = stringRes("Transaction ID"),
        onTransactionIdClick = {},
        completedTimestamp = stringRes(ZonedDateTime.now()),
        memo = memo,
    )
}

object ReceiveTransparentStateFixture {
    @Suppress("MagicNumber")
    fun new() =
        ReceiveTransparentState(
            transactionId = stringRes("Transaction ID"),
            onTransactionIdClick = {},
            completedTimestamp = stringRes(ZonedDateTime.now()),
        )
}

object ShieldingStateFixture {
    @Suppress("MagicNumber")
    fun new() =
        ShieldingState(
            transactionId = stringRes("Transaction ID"),
            onTransactionIdClick = {},
            completedTimestamp = stringRes(ZonedDateTime.now()),
            fee = stringRes(Zatoshi(1011))
        )
}
