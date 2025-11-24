package co.electriccoin.zcash.ui.screen.advancedsettings.debug.db

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DebugDBVM(
    private val synchronizerProvider: SynchronizerProvider,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    private val output = MutableStateFlow<String?>(null)

    private val query = MutableStateFlow("")

    private var executeJob: Job? = null

    val state =
        combine(query, output) { queryText, outputText ->
            createState(queryText, outputText)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = createState("", null)
        )

    private fun createState(
        queryText: String,
        outputText: String?
    ) = DebugDBState(
        query =
            TextFieldState(
                value = stringRes(queryText),
                onValueChange = ::onQueryChanged
            ),
        output = outputText?.let { stringRes(it) } ?: stringRes(""),
        execute =
            ButtonState(
                text = stringRes("Execute"),
                onClick = ::onExecuteClick
            ),
        onBack = ::onBack
    )

    private fun onBack() = navigationRouter.back()

    private fun onQueryChanged(newQuery: String) = query.update { newQuery }

    @Suppress("TooGenericExceptionCaught")
    private fun onExecuteClick() {
        if (executeJob?.isActive == true) return
        executeJob =
            viewModelScope.launch {
                val currentQuery = query.value
                if (currentQuery.isBlank()) {
                    output.update { "Please enter a query" }
                } else {
                    try {
                        val synchronizer = synchronizerProvider.getSynchronizer()
                        val result = synchronizer.debugQuery(currentQuery)
                        output.update { result }
                    } catch (e: Exception) {
                        output.update { "Error: ${e.message}" }
                    }
                }
            }
    }
}
