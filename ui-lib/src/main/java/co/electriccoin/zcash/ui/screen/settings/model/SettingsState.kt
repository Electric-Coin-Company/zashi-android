package co.electriccoin.zcash.ui.screen.settings.model

import co.electriccoin.zcash.ui.design.util.StringResource

data class SettingsState(
    val isLoading: Boolean,
    val version: StringResource,
    val settingsTroubleshootingState: SettingsTroubleshootingState?,
    val onAddressBookClick: () -> Unit,
    val onBack: () -> Unit,
    val onAdvancedSettingsClick: () -> Unit,
    val onAboutUsClick: () -> Unit,
    val onSendUsFeedbackClick: () -> Unit,
)
