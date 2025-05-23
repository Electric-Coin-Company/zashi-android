package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun ColumnScope.Spacer(height: Dp) {
    Spacer(Modifier.height(height))
}

@Composable
fun ColumnScope.Spacer(weight: Float) {
    Spacer(Modifier.weight(weight))
}

@Composable
fun RowScope.Spacer(weight: Float) {
    Spacer(Modifier.weight(weight))
}

@Composable
fun RowScope.Spacer(width: Dp) {
    Spacer(Modifier.width(width))
}

@Composable
fun HorizontalSpacer(width: Dp) {
    Spacer(Modifier.width(width))
}

@Composable
fun VerticalSpacer(height: Dp) {
    Spacer(Modifier.height(height))
}
