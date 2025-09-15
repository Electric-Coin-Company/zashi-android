package co.electriccoin.zcash.ui.design.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class StyledStringResource(
    val resource: StringResource,
    val color: StringResourceColor = StringResourceColor.PRIMARY,
)

@Composable
fun StyledStringResource.getValue() = resource.getValue()

@Composable
fun StyledStringResource.getColor() = color.getColor()
