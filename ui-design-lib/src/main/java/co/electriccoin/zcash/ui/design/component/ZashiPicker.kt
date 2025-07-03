package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun ZashiPicker(
    state: PickerState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        onClick = state.onClick,
        shape = RoundedCornerShape(ZashiDimensions.Radius.radiusLg),
        color = ZashiColors.Dropdowns.Default.bg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.bigIcon is ImageResource.ByDrawable) {
                Box {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(state.bigIcon.resource),
                        contentDescription = null,
                    )
                    if (state.smallIcon is ImageResource.ByDrawable) {
                        Image(
                            modifier =
                                Modifier
                                    .size(14.dp)
                                    .align(Alignment.BottomEnd)
                                    .offset(3.dp, 3.dp),
                            painter = painterResource(state.smallIcon.resource),
                            contentDescription = null,
                        )
                    }
                }
                Spacer(8.dp)
            }

            if (state.text != null) {
                Text(
                    text = state.text.getValue(),
                    style = ZashiTypography.textMd,
                    fontWeight = FontWeight.Medium,
                    color = ZashiColors.Dropdowns.Filled.textMain
                )
            } else {
                Text(
                    text = state.placeholder.getValue(),
                    style = ZashiTypography.textMd,
                    fontWeight = FontWeight.Medium,
                    color = ZashiColors.Dropdowns.Default.text
                )
            }

            Spacer(1f)
            Image(
                painter = painterResource(R.drawable.ic_chevron_down),
                contentDescription = null
            )
        }
    }
}

@Immutable
data class PickerState(
    val bigIcon: ImageResource?,
    val smallIcon: ImageResource?,
    val text: StringResource?,
    val placeholder: StringResource,
    val onClick: () -> Unit
)

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            ZashiPicker(
                state =
                    PickerState(
                        bigIcon = imageRes(R.drawable.ic_item_keystone),
                        smallIcon = imageRes(R.drawable.ic_item_keystone),
                        text = stringRes("Text"),
                        placeholder = stringRes("Placeholder"),
                        onClick = {}
                    )
            )
        }
    }

@PreviewScreens
@Composable
private fun PlaceholderPreview() =
    ZcashTheme {
        BlankSurface {
            ZashiPicker(
                state =
                    PickerState(
                        bigIcon = null,
                        smallIcon = null,
                        text = null,
                        placeholder = stringRes("Placeholder..."),
                        onClick = {}
                    )
            )
        }
    }
