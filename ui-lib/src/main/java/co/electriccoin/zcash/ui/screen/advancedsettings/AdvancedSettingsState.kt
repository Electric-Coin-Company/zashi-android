package co.electriccoin.zcash.ui.screen.advancedsettings

import co.electriccoin.zcash.ui.design.component.ButtonState

data class AdvancedSettingsState(
    val onBack: () -> Unit,
    val onRecoveryPhraseClick: () -> Unit,
    val onExportPrivateDataClick: () -> Unit,
    val onChooseServerClick: () -> Unit,
    val onCurrencyConversionClick: () -> Unit,
    val onDeleteZashiClick: () -> Unit,
    val coinbaseButton: ButtonState?,
)
