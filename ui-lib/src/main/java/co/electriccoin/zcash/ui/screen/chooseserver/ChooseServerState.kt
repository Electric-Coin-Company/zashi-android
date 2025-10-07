package co.electriccoin.zcash.ui.screen.chooseserver

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.AlertDialogState
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.RadioButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.Itemizable
import co.electriccoin.zcash.ui.design.util.StringResource

@Immutable
data class ChooseServerState(
    val fastest: ServerListState.Fastest,
    val other: ServerListState.Other,
    val saveButton: ButtonState,
    val dialogState: ServerDialogState?,
    val onBack: () -> Unit
)

@Immutable
sealed interface ServerListState {
    val title: StringResource
    val servers: List<ServerState>

    @Immutable
    data class Other(
        override val title: StringResource,
        override val servers: List<ServerState>,
    ) : ServerListState

    @Immutable
    data class Fastest(
        override val title: StringResource,
        override val servers: List<ServerState.Default>,
        val retryButton: ButtonState,
        val isLoading: Boolean
    ) : ServerListState
}

@Immutable
sealed interface ServerState : Itemizable {
    @Immutable
    data class Default(
        override val key: Any,
        val radioButtonState: RadioButtonState,
        val badge: StringResource?,
    ) : ServerState {
        override val contentType: Any = "Default"
    }

    @Immutable
    data class Custom(
        override val key: Any,
        val radioButtonState: RadioButtonState,
        val newServerTextFieldState: TextFieldState,
        val badge: StringResource?,
        val isExpanded: Boolean,
    ) : ServerState {
        override val contentType: Any = "Custom"
    }
}

@Immutable
sealed interface ServerDialogState {
    val state: AlertDialogState

    @Immutable
    data class Validation(
        override val state: AlertDialogState,
        val reason: StringResource?
    ) : ServerDialogState
}
