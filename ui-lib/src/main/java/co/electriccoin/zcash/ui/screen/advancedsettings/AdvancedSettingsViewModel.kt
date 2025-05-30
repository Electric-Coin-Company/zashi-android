package co.electriccoin.zcash.ui.screen.advancedsettings

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.NavigationTargets
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.DistributionDimension
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.usecase.GetWalletRestoringStateUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToTaxExportUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToWalletBackupUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.exchangerate.settings.ExchangeRateSettings
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdvancedSettingsViewModel(
    getWalletRestoringState: GetWalletRestoringStateUseCase,
    private val navigationRouter: NavigationRouter,
    private val navigateToTaxExport: NavigateToTaxExportUseCase,
    private val navigateToWalletBackup: NavigateToWalletBackupUseCase,
    private val getVersionInfo: GetVersionInfoProvider,
) : ViewModel() {
    private val versionInfo by lazy { getVersionInfo() }

    val state: StateFlow<AdvancedSettingsState> =
        getWalletRestoringState
            .observe()
            .map { walletState ->
                createState(walletState)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = createState(getWalletRestoringState.observe().value)
            )

    private fun createState(walletRestoringState: WalletRestoringState) =
        AdvancedSettingsState(
            onBack = ::onBack,
            items =
                mutableStateListOf(
                    ListItemState(
                        title = stringRes(R.string.advanced_settings_recovery),
                        icon = imageRes(R.drawable.ic_advanced_settings_recovery),
                        onClick = ::onSeedRecoveryClick
                    ),
                    ListItemState(
                        title = stringRes(R.string.advanced_settings_export),
                        icon = imageRes(R.drawable.ic_advanced_settings_export),
                        onClick = {}
                    ),
                    ListItemState(
                        title = stringRes(R.string.advanced_settings_tax),
                        icon =
                            imageRes(
                                if (walletRestoringState == WalletRestoringState.RESTORING) {
                                    R.drawable.ic_advanced_settings_tax_disabled
                                } else {
                                    R.drawable.ic_advanced_settings_tax
                                }
                            ),
                        isEnabled = walletRestoringState != WalletRestoringState.RESTORING,
                        onClick = ::onTaxExportClick
                    ),
                    ListItemState(
                        title = stringRes(R.string.advanced_settings_choose_server),
                        icon = imageRes(R.drawable.ic_advanced_settings_choose_server),
                        onClick = ::onChooseServerClick
                    ),
                    ListItemState(
                        title = stringRes(R.string.advanced_settings_currency_conversion),
                        icon = imageRes(R.drawable.ic_advanced_settings_currency_conversion),
                        onClick = ::onCurrencyConversionClick
                    ),
                ).apply {
                    if (versionInfo.distributionDimension == DistributionDimension.STORE) {
                        add(
                            ListItemState(
                                title = stringRes(R.string.advanced_settings_crash_reporting),
                                icon = imageRes(R.drawable.ic_advanced_settings_crash_reporting),
                                onClick = ::onCrashReportingClick
                            )
                        )
                    }
                }.toImmutableList(),
            deleteButton =
                ButtonState(
                    text = stringRes(R.string.advanced_settings_delete_button),
                    onClick = {}
                ),
        )

    fun onBack() = navigationRouter.back()

    private fun onChooseServerClick() = navigationRouter.forward(NavigationTargets.CHOOSE_SERVER)

    private fun onCurrencyConversionClick() = navigationRouter.forward(ExchangeRateSettings)

    private fun onCrashReportingClick() = navigationRouter.forward(NavigationTargets.CRASH_REPORTING_OPT_IN)

    private fun onTaxExportClick() =
        viewModelScope.launch {
            navigateToTaxExport()
        }

    private fun onSeedRecoveryClick() =
        viewModelScope.launch {
            navigateToWalletBackup(isOpenedFromSeedBackupInfo = false)
        }
}
