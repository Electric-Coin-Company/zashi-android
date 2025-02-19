package co.electriccoin.zcash.ui.screen.advancedsettings.viewmodel

import androidx.lifecycle.ViewModel
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.NavigationTargets
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.advancedsettings.model.AdvancedSettingsState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdvancedSettingsViewModel(
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    val state: StateFlow<AdvancedSettingsState> = MutableStateFlow(createState()).asStateFlow()

    private fun createState() =
        AdvancedSettingsState(
            onBack = ::onBack,
            items =
                listOfNotNull(
                    ZashiListItemState(
                        title = stringRes(R.string.advanced_settings_recovery),
                        icon = R.drawable.ic_advanced_settings_recovery,
                        onClick = {}
                    ),
                    ZashiListItemState(
                        title = stringRes(R.string.advanced_settings_export),
                        icon = R.drawable.ic_advanced_settings_export,
                        onClick = {}
                    ),
                    ZashiListItemState(
                        title = stringRes(R.string.advanced_settings_choose_server),
                        icon =
                            R.drawable.ic_advanced_settings_choose_server,
                        onClick = ::onChooseServerClick
                    ),
                    ZashiListItemState(
                        title = stringRes(R.string.advanced_settings_currency_conversion),
                        icon =
                            R.drawable.ic_advanced_settings_currency_conversion,
                        onClick = ::onCurrencyConversionClick
                    )
                ).toImmutableList(),
            deleteButton =
                ButtonState(
                    stringRes(R.string.advanced_settings_delete_button),
                    onClick = {}
                ),
        )

    private fun onChooseServerClick() = navigationRouter.forward(NavigationTargets.CHOOSE_SERVER)

    private fun onCurrencyConversionClick() = navigationRouter.forward(NavigationTargets.SETTINGS_EXCHANGE_RATE_OPT_IN)

    fun onBack() = navigationRouter.back()
}
