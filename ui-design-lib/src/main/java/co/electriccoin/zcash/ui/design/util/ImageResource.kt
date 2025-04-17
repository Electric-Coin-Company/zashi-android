package co.electriccoin.zcash.ui.design.util

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
sealed interface ImageResource {
    @Immutable
    @JvmInline
    value class ByDrawable(@DrawableRes val resource: Int) : ImageResource

    @JvmInline
    @Immutable
    value class DisplayString(val value: String) : ImageResource

    @Immutable
    data object Loading: ImageResource
}

@Stable
fun imageRes(
    @DrawableRes resource: Int
): ImageResource = ImageResource.ByDrawable(resource)

@Stable
fun imageRes(value: String): ImageResource = ImageResource.DisplayString(value)

@Stable
fun loadingImageRes(): ImageResource = ImageResource.Loading
