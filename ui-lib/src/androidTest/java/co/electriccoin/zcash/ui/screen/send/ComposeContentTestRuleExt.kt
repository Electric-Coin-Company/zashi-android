package co.electriccoin.zcash.ui.screen.send

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.sdk.fixture.MemoFixture
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.test.getStringResource

internal fun ComposeContentTestRule.clickBack() {
    onNodeWithContentDescription(getStringResource(R.string.send_back_content_description)).also {
        it.performClick()
    }
}

internal fun ComposeContentTestRule.clickSettingsTopAppBarMenu() {
    onNodeWithContentDescription(getStringResource(R.string.settings_menu_content_description)).also {
        it.performClick()
    }
}

internal fun ComposeContentTestRule.clickScanner() {
    onNodeWithContentDescription(getStringResource(R.string.send_scan_content_description)).also {
        it.performClick()
    }
}

internal fun ComposeContentTestRule.setValidAmount() {
    onNodeWithText(getStringResource(R.string.send_amount)).also {
        val separators = MonetarySeparators.current()
        it.performTextClearance()
        it.performTextInput("123${separators.decimal}456")
    }
}

internal fun ComposeContentTestRule.setAmount(amount: String) {
    onNodeWithText(getStringResource(R.string.send_amount)).also {
        it.performTextClearance()
        it.performTextInput(amount)
    }
}

internal fun ComposeContentTestRule.setValidAddress() {
    onNodeWithText(getStringResource(R.string.send_to)).also {
        it.performTextClearance()
        it.performTextInput(WalletAddressFixture.UNIFIED_ADDRESS_STRING)
    }
}

internal fun ComposeContentTestRule.setAddress(address: String) {
    onNodeWithText(getStringResource(R.string.send_to)).also {
        it.performTextClearance()
        it.performTextInput(address)
    }
}

internal fun ComposeContentTestRule.setValidMemo() {
    onNodeWithText(getStringResource(R.string.send_memo)).also {
        it.performTextClearance()
        it.performTextInput(MemoFixture.MEMO_STRING)
    }
}

internal fun ComposeContentTestRule.setMemo(memo: String) {
    onNodeWithText(getStringResource(R.string.send_memo)).also {
        it.performTextClearance()
        it.performTextInput(memo)
    }
}

internal fun ComposeContentTestRule.clickCreateAndSend() {
    onNodeWithTag(SendTag.SEND_FORM_BUTTON).also {
        it.performScrollTo()
        it.performClick()
    }
}

internal fun ComposeContentTestRule.clickConfirmation() {
    onNodeWithTag(SendTag.SEND_CONFIRMATION_BUTTON).also {
        it.performClick()
    }
}

internal fun ComposeContentTestRule.assertOnForm() {
    onNodeWithTag(SendTag.SEND_FORM_BUTTON).also {
        it.assertExists()
    }
}

internal fun ComposeContentTestRule.assertOnConfirmation() {
    onNodeWithTag(SendTag.SEND_CONFIRMATION_BUTTON).also {
        it.assertExists()
    }
}

internal fun ComposeContentTestRule.assertOnSending() {
    onNodeWithText(getStringResource(R.string.send_in_progress_wait)).also {
        it.assertExists()
    }
}

internal fun ComposeContentTestRule.assertOnSendSuccessful() {
    onNodeWithText(getStringResource(R.string.send_successful_title)).also {
        it.assertExists()
    }
}

internal fun ComposeContentTestRule.assertOnSendFailure() {
    onNodeWithText(getStringResource(R.string.send_failure_title)).also {
        it.assertExists()
    }
}

internal fun ComposeContentTestRule.assertSendEnabled() {
    onNodeWithTag(SendTag.SEND_FORM_BUTTON).also {
        it.assertIsEnabled()
    }
}

internal fun ComposeContentTestRule.assertSendDisabled() {
    onNodeWithTag(SendTag.SEND_FORM_BUTTON).also {
        it.assertIsNotEnabled()
    }
}
