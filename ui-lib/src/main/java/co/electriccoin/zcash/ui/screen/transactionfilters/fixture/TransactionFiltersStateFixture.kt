package co.electriccoin.zcash.ui.screen.transactionfilters.fixture

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.screen.transactionfilters.model.TransactionFilterState
import co.electriccoin.zcash.ui.screen.transactionfilters.model.TransactionFilterType
import co.electriccoin.zcash.ui.screen.transactionfilters.model.TransactionFiltersState
import kotlinx.collections.immutable.persistentListOf

object TransactionFiltersStateFixture {
    val FILTERS = persistentListOf(
        TransactionFilterState(
            type = TransactionFilterType.SENT,
            text = StringResource.ByString("Sent"),
            isSelected = true,
            onClick = {}
        ),
        TransactionFilterState(
            type = TransactionFilterType.RECEIVED,
            text = StringResource.ByString("Received"),
            isSelected = false,
            onClick = {}
        ),
        TransactionFilterState(
            type = TransactionFilterType.MEMOS,
            text = StringResource.ByString("Memos"),
            isSelected = false,
            onClick = {}
        ),
        TransactionFilterState(
            type = TransactionFilterType.UNREAD,
            text = StringResource.ByString("Unread"),
            isSelected = false,
            onClick = {}
        ),
        TransactionFilterState(
            type = TransactionFilterType.BOOKMARKED,
            text = StringResource.ByString("Bookmarked"),
            isSelected = false,
            onClick = {}
        ),
        TransactionFilterState(
            type = TransactionFilterType.NOTES,
            text = StringResource.ByString("Notes"),
            isSelected = false,
            onClick = {}
        )
    )

    val PRIMARY_BUTTON_STATE = ButtonState(
        text = StringResource.ByString("Apply"),
        isEnabled = true
    )

    val SECONDARY_BUTTON_STATE = ButtonState(
        text = StringResource.ByString("Apply"),
        isEnabled = true
    )

    fun new(
        onBack: () -> Unit = {},
        filters: List<TransactionFilterState> = FILTERS,
        primaryButtonState: ButtonState = PRIMARY_BUTTON_STATE,
        secondaryButtonState: ButtonState = SECONDARY_BUTTON_STATE
    ) = TransactionFiltersState(
        onBack = onBack,
        filters = filters,
        primaryButton = primaryButtonState,
        secondaryButton = secondaryButtonState
    )
}
