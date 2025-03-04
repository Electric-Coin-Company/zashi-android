package co.electriccoin.zcash.ui.screen.transactiondetail.infoitems

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

enum class TransactionDetailInfoShape(
    val shape: Shape
) {
    FIRST(RoundedCornerShape(topStart = CORNER_RADIUS, topEnd = CORNER_RADIUS)),
    MIDDLE(RectangleShape),
    LAST(RoundedCornerShape(bottomStart = CORNER_RADIUS, bottomEnd = CORNER_RADIUS)),
    SINGLE(RoundedCornerShape(CORNER_RADIUS))
}

private val CORNER_RADIUS = 12.dp
