package co.electriccoin.zcash.ui.design.util

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.component.ShimmerCircle
import co.electriccoin.zcash.ui.design.component.rememberZashiShimmer
import com.valentinilk.shimmer.shimmer

@Immutable
sealed interface ImageResource {
    @Immutable
    @JvmInline
    value class ByDrawable(
        @DrawableRes val resource: Int
    ) : ImageResource

    @JvmInline
    @Immutable
    value class DisplayString(
        val value: String
    ) : ImageResource

    @Immutable
    data object Loading : ImageResource
}

@Stable
fun imageRes(
    @DrawableRes resource: Int
): ImageResource = ImageResource.ByDrawable(resource)

@Stable
fun imageRes(value: String): ImageResource = ImageResource.DisplayString(value)

@Stable
fun loadingImageRes(): ImageResource = ImageResource.Loading

@Composable
fun ImageResource.Loading.ComposeAsShimmerCircle(modifier: Modifier = Modifier, size: Dp = 24.dp) {
    Box(
        modifier = modifier.shimmer(rememberZashiShimmer())
    ) {
        ShimmerCircle(size = size)
    }
}

@Composable
fun ImageResource.ByDrawable.Compose(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    Image(
        modifier = modifier,
        painter = painterResource(resource),
        contentDescription = contentDescription,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter
    )
}
