package co.electriccoin.zcash.ui.screen.history.view

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.history.HistoryTag
import co.electriccoin.zcash.ui.screen.history.HistoryTestSetup
import co.electriccoin.zcash.ui.screen.history.fixture.TransactionHistorySyncStateFixture
import co.electriccoin.zcash.ui.screen.history.state.TransactionHistorySyncState
import co.electriccoin.zcash.ui.test.getStringResource
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HistoryViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun check_loading_state() {
        newTestSetup(TransactionHistorySyncState.Loading)

        composeTestRule.onNodeWithTag(HistoryTag.PROGRESS).also {
            it.assertExists()
        }
    }

    @Test
    @MediumTest
    fun check_syncing_state() {
        newTestSetup(
            TransactionHistorySyncStateFixture.new(
                state = TransactionHistorySyncStateFixture.STATE,
                transactions = TransactionHistorySyncStateFixture.TRANSACTIONS
            )
        )

        composeTestRule.onNodeWithText(getStringResource(R.string.history_syncing)).also {
            it.assertExists()
        }
        // No progress bar, as we have some transactions laid out
        composeTestRule.onNodeWithTag(HistoryTag.PROGRESS).also {
            it.assertDoesNotExist()
        }
        composeTestRule.onNodeWithTag(HistoryTag.TRANSACTION_LIST).also {
            it.assertExists()
            it.assertHeightIsAtLeast(1.dp)
        }
    }

    @Test
    @MediumTest
    fun check_done_state_no_transactions() {
        newTestSetup(
            TransactionHistorySyncStateFixture.new(
                state = TransactionHistorySyncState.Done(persistentListOf()),
                transactions = persistentListOf()
            )
        )
        composeTestRule.onNodeWithText(getStringResource(R.string.history_syncing)).also {
            it.assertDoesNotExist()
        }
        composeTestRule.onNodeWithTag(HistoryTag.PROGRESS).also {
            it.assertDoesNotExist()
        }
        composeTestRule.onNodeWithTag(HistoryTag.TRANSACTION_LIST).also {
            it.assertDoesNotExist()
        }
        composeTestRule.onNodeWithText(getStringResource(R.string.history_empty)).also {
            it.assertExists()
        }
    }

    @Test
    @MediumTest
    fun check_done_state_with_transactions() {
        newTestSetup(
            TransactionHistorySyncStateFixture.new(
                state = TransactionHistorySyncState.Done(persistentListOf()),
                transactions = TransactionHistorySyncStateFixture.TRANSACTIONS
            )
        )
        composeTestRule.onNodeWithText(getStringResource(R.string.history_syncing)).also {
            it.assertDoesNotExist()
        }
        composeTestRule.onNodeWithTag(HistoryTag.PROGRESS).also {
            it.assertDoesNotExist()
        }
        composeTestRule.onNodeWithTag(HistoryTag.TRANSACTION_LIST).also {
            it.assertExists()
            it.assertHeightIsAtLeast(1.dp)
        }
        composeTestRule.onNodeWithText(getStringResource(R.string.history_empty)).also {
            it.assertDoesNotExist()
        }
    }

    @Test
    @MediumTest
    fun back_click_test() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnBackClickCount())

        composeTestRule.onNodeWithContentDescription(
            getStringResource(R.string.history_back_content_description)
        ).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnBackClickCount())
    }

    @Test
    @MediumTest
    fun item_click_test() {
        val testSetup = newTestSetup(TransactionHistorySyncStateFixture.STATE)

        assertEquals(0, testSetup.getOnItemClickCount())

        composeTestRule.onAllNodesWithTag(HistoryTag.TRANSACTION_ITEM, useUnmergedTree = true).also {
            it.assertCountEquals(TransactionHistorySyncStateFixture.TRANSACTIONS.size)

            TransactionHistorySyncStateFixture.TRANSACTIONS.forEachIndexed { index, _ ->
                it[index].performClick()
            }
        }

        assertEquals(TransactionHistorySyncStateFixture.TRANSACTIONS.size, testSetup.getOnItemClickCount())
    }

    @Test
    @MediumTest
    fun transaction_id_click_test() {
        val testSetup = newTestSetup(TransactionHistorySyncStateFixture.STATE)

        assertEquals(0, testSetup.getOnItemIdClickCount())

        composeTestRule.onAllNodesWithTag(HistoryTag.TRANSACTION_ID).also {
            it.assertCountEquals(TransactionHistorySyncStateFixture.TRANSACTIONS.size)

            TransactionHistorySyncStateFixture.TRANSACTIONS.forEachIndexed { index, _ ->
                it[index].performClick()
            }
        }

        assertEquals(TransactionHistorySyncStateFixture.TRANSACTIONS.size, testSetup.getOnItemIdClickCount())
    }

    private fun newTestSetup(
        transactionHistorySyncState: TransactionHistorySyncState = TransactionHistorySyncStateFixture.new()
    ): HistoryTestSetup {
        return HistoryTestSetup(
            composeTestRule = composeTestRule,
            initialHistorySyncState = transactionHistorySyncState
        )
    }
}
