package co.electriccoin.zcash.ui.screen.send.nighthawk.model

sealed class NumberPadValueTypes(val keyValue: String) {
    data class Separator(val value: String): NumberPadValueTypes(value)
    data class BackSpace(val value: String): NumberPadValueTypes(value)
    data class Number(val value: String): NumberPadValueTypes(value)
}

val numberPadKeys = listOf(
    NumberPadValueTypes.Number("1"),
    NumberPadValueTypes.Number("2"),
    NumberPadValueTypes.Number("3"),
    NumberPadValueTypes.Number("4"),
    NumberPadValueTypes.Number("5"),
    NumberPadValueTypes.Number("6"),
    NumberPadValueTypes.Number("7"),
    NumberPadValueTypes.Number("8"),
    NumberPadValueTypes.Number("9"),
    NumberPadValueTypes.Separator("."),
    NumberPadValueTypes.Number("0"),
    NumberPadValueTypes.BackSpace("<"),
)

val passwordPadKeys = listOf(
    NumberPadValueTypes.Number("1"),
    NumberPadValueTypes.Number("2"),
    NumberPadValueTypes.Number("3"),
    NumberPadValueTypes.Number("4"),
    NumberPadValueTypes.Number("5"),
    NumberPadValueTypes.Number("6"),
    NumberPadValueTypes.Number("7"),
    NumberPadValueTypes.Number("8"),
    NumberPadValueTypes.Number("9"),
    NumberPadValueTypes.Separator(""),
    NumberPadValueTypes.Number("0"),
    NumberPadValueTypes.BackSpace("<"),
)
