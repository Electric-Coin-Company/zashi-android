package co.electriccoin.zcash.ui.screen.chooseserver

import co.electriccoin.zcash.ui.design.component.AlertDialogState
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.Itemizable
import co.electriccoin.zcash.ui.design.util.StringResource

data class ChooseServerState(
    val fastest: ServerListState.Fastest,
    val all: ServerListState.All,
    val saveButton: ButtonState,
    val dialogState: ServerDialogState?
)

sealed interface ServerListState {

    val title: StringResource
    val servers: List<ServerState>

    data class All(
        override val title: StringResource,
        override val servers: List<ServerState>,
    ) : ServerListState

    data class Fastest(
        override val title: StringResource,
        override val servers: List<ServerState.Default>,
        val isLoading: Boolean
    ) : ServerListState
}

sealed interface ServerState : Itemizable {
    data class Default(
        val name: StringResource,
        val isChecked: Boolean,
        val onClick: () -> Unit
    ) : ServerState {
        override val contentType: Any = "Default"
        override val key: Any = contentType
    }

    data class Custom(
        val name: StringResource,
        val isChecked: Boolean,
        val newServerTextFieldState: TextFieldState,
        val onClick: () -> Unit
    ) : ServerState {
        override val contentType: Any = "Custom"
        override val key: Any = contentType
    }
}

sealed interface ServerDialogState {
    val state: AlertDialogState

    data class Validation(override val state: AlertDialogState, val reason: StringResource?) : ServerDialogState
    data class SaveSuccess(override val state: AlertDialogState) : ServerDialogState
}
