package co.electriccoin.zcash.ui.design.util

import androidx.compose.runtime.Composable

data class StyledStringResource(
    val resource: StringResource,
    val color: StringResourceColor = StringResourceColor.DEFAULT,
)

@Composable
fun StyledStringResource.getValue() = resource.getValue()

@Composable
fun StyledStringResource.getColor() = color.getColor()
