package co.electriccoin.zcash.ui.screen.transactionfilters.fixture

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.screen.transactionfilters.TransactionFilterState
import co.electriccoin.zcash.ui.screen.transactionfilters.TransactionFiltersState
import kotlinx.collections.immutable.persistentListOf

object TransactionFiltersStateFixture {
    val FILTERS =
        persistentListOf(
            TransactionFilterState(
                text = StringResource.ByString("Sent"),
                isSelected = true,
                onClick = {}
            ),
            TransactionFilterState(
                text = StringResource.ByString("Received"),
                isSelected = false,
                onClick = {}
            ),
            TransactionFilterState(
                text = StringResource.ByString("Memos"),
                isSelected = false,
                onClick = {}
            ),
            TransactionFilterState(
                text = StringResource.ByString("Unread"),
                isSelected = false,
                onClick = {}
            ),
            TransactionFilterState(
                text = StringResource.ByString("Bookmarked"),
                isSelected = false,
                onClick = {}
            ),
            TransactionFilterState(
                text = StringResource.ByString("Notes"),
                isSelected = false,
                onClick = {}
            )
        )

    val PRIMARY_BUTTON_STATE =
        ButtonState(
            text = StringResource.ByString("Apply"),
            isEnabled = true
        )

    val SECONDARY_BUTTON_STATE =
        ButtonState(
            text = StringResource.ByString("Reset"),
            isEnabled = true
        )

    fun new(
        onBack: () -> Unit = {},
        filters: List<TransactionFilterState> = FILTERS,
        primaryButtonState: ButtonState = PRIMARY_BUTTON_STATE,
        secondaryButtonState: ButtonState = SECONDARY_BUTTON_STATE
    ) = TransactionFiltersState(
        filters = filters,
        onBack = onBack,
        primaryButton = primaryButtonState,
        secondaryButton = secondaryButtonState
    )
}
