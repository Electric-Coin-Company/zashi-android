package co.electriccoin.zcash.ui.screen.transactiondetail.infoitems

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.common.model.SwapStatus
import co.electriccoin.zcash.ui.common.model.SwapStatus.FAILED
import co.electriccoin.zcash.ui.common.model.SwapStatus.INCOMPLETE_DEPOSIT
import co.electriccoin.zcash.ui.common.model.SwapStatus.PENDING
import co.electriccoin.zcash.ui.common.model.SwapStatus.REFUNDED
import co.electriccoin.zcash.ui.common.model.SwapStatus.SUCCESS
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.ShimmerRectangle
import co.electriccoin.zcash.ui.design.component.ZashiBadge
import co.electriccoin.zcash.ui.design.component.ZashiBadgeDefaults
import co.electriccoin.zcash.ui.design.component.rememberZashiShimmer
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import com.valentinilk.shimmer.shimmer

@Composable
fun TransactionDetailSwapStatusRow(
    state: TransactionDetailSwapStatusRowState,
    modifier: Modifier = Modifier
) {
    TransactionDetailRowSurface(
        onClick = null,
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            maxLines = 1,
            text = state.title.getValue(),
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textTertiary
        )
        if (state.status != null) {
            Spacer(Modifier.width(16.dp))
            StatusChip(state.status)
        } else {
            Spacer(Modifier.width(16.dp))
            Box(
                modifier = Modifier.shimmer(rememberZashiShimmer())
            ) {
                ShimmerRectangle(
                    width = 64.dp,
                    height = 20.dp,
                    color = ZashiColors.Surfaces.bgTertiary
                )
            }
        }
    }
}

@Composable
private fun StatusChip(swapStatus: SwapStatus) {
    ZashiBadge(
        text = when (swapStatus) {
            INCOMPLETE_DEPOSIT -> "Incomplete deposit"
            PENDING -> "Pending"
            SUCCESS -> "Completed"
            REFUNDED -> "Refunded"
            FAILED -> "Failed"
        },
        colors = when (swapStatus) {
            PENDING -> ZashiBadgeDefaults.hyperBlueColors()
            SUCCESS -> ZashiBadgeDefaults.successColors()
            INCOMPLETE_DEPOSIT,
            REFUNDED,
            FAILED -> ZashiBadgeDefaults.errorColors()
        },
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Immutable
data class TransactionDetailSwapStatusRowState(
    val title: StringResource,
    val status: SwapStatus?,
)

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            TransactionDetailSwapStatusRow(
                state =
                    TransactionDetailSwapStatusRowState(
                        title = stringRes("Title"),
                        status = SUCCESS,
                    )
            )
        }
    }

@PreviewScreens
@Composable
private fun LoadingPreview() =
    ZcashTheme {
        BlankSurface {
            TransactionDetailSwapStatusRow(
                state =
                    TransactionDetailSwapStatusRowState(
                        title = stringRes("Title"),
                        status = null,
                    )
            )
        }
    }

