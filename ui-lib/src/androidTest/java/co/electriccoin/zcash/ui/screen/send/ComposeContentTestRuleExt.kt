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
import cash.z.ecc.sdk.fixture.ZecSendFixture
import cash.z.ecc.sdk.type.ZcashCurrency
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.sendconfirmation.SendConfirmationTag
import co.electriccoin.zcash.ui.test.getAppContext
import co.electriccoin.zcash.ui.test.getStringResource
import co.electriccoin.zcash.ui.test.getStringResourceWithArgs

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
    onNodeWithText(
        getStringResourceWithArgs(
            R.string.send_amount_hint,
            ZcashCurrency.fromResources(getAppContext()).name
        )
    ).also {
        it.performTextClearance()
        it.performTextInput(ZecSendFixture.AMOUNT.value.toString())
    }
}

internal fun ComposeContentTestRule.setAmount(amount: String) {
    onNodeWithText(
        getStringResourceWithArgs(
            R.string.send_amount_hint,
            ZcashCurrency.fromResources(getAppContext()).name
        )
    ).also {
        it.performTextClearance()
        it.performTextInput(amount)
    }
}

internal fun ComposeContentTestRule.setValidAddress() {
    onNodeWithText(getStringResource(R.string.send_address_hint)).also {
        it.performTextClearance()
        it.performTextInput(ZecSendFixture.ADDRESS)
    }
}

internal fun ComposeContentTestRule.setAddress(address: String) {
    onNodeWithText(getStringResource(R.string.send_address_hint)).also {
        it.performTextClearance()
        it.performTextInput(address)
    }
}

internal fun ComposeContentTestRule.setValidMemo() {
    onNodeWithText(getStringResource(R.string.send_memo_hint)).also {
        it.performTextClearance()
        it.performTextInput(ZecSendFixture.MEMO.value)
    }
}

internal fun ComposeContentTestRule.setMemo(memo: String) {
    onNodeWithText(getStringResource(R.string.send_memo_hint)).also {
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

internal fun ComposeContentTestRule.dismissFailureDialog() {
    onNodeWithText(getStringResource(R.string.send_dialog_error_btn)).also {
        it.performClick()
    }
}

internal fun ComposeContentTestRule.assertOnForm() {
    onNodeWithTag(SendTag.SEND_FORM_BUTTON).also {
        it.assertExists()
    }
}

internal fun ComposeContentTestRule.assertOnConfirmation() {
    onNodeWithTag(SendConfirmationTag.SEND_CONFIRMATION_SEND_BUTTON).also {
        it.assertExists()
    }
}

internal fun ComposeContentTestRule.assertOnSendFailure() {
    onNodeWithText(getStringResource(R.string.send_dialog_error_title)).also {
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
