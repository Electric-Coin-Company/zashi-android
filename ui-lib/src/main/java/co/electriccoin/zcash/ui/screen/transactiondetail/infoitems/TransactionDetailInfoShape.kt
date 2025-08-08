package co.electriccoin.zcash.ui.screen.transactiondetail.infoitems

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class TransactionDetailInfoShape(
    val topStart: Dp = 0.dp,
    val topEnd: Dp = 0.dp,
    val bottomStart: Dp = 0.dp,
    val bottomEnd: Dp = 0.dp
) {
    FIRST(topStart = CORNER_RADIUS, topEnd = CORNER_RADIUS),
    MIDDLE,
    LAST(bottomStart = CORNER_RADIUS, bottomEnd = CORNER_RADIUS),
    SINGLE(topStart = CORNER_RADIUS, topEnd = CORNER_RADIUS, bottomStart = CORNER_RADIUS, bottomEnd = CORNER_RADIUS);

    fun toShape() = RoundedCornerShape(
        topStart = topStart,
        topEnd = topEnd,
        bottomStart = bottomStart,
        bottomEnd = bottomEnd
    )
}

private val CORNER_RADIUS = 12.dp
