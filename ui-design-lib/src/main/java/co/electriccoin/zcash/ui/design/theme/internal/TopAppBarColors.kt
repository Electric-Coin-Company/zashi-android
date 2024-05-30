@file:Suppress("MagicNumber")

package co.electriccoin.zcash.ui.design.theme.internal

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
interface TopAppBarColors {
    val containerColor: Color
    val navigationColor: Color
    val titleColor: Color
    val subTitleColor: Color
    val actionColor: Color

    @OptIn(ExperimentalMaterial3Api::class)
    fun toMaterialTopAppBarColors() =
        androidx.compose.material3.TopAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = containerColor,
            navigationIconContentColor = navigationColor,
            titleContentColor = titleColor,
            actionIconContentColor = actionColor
        )

    fun copyColors(
        containerColor: Color = this.containerColor,
        navigationColor: Color = this.navigationColor,
        titleColor: Color = this.titleColor,
        subTitleColor: Color = this.subTitleColor,
        actionColor: Color = this.actionColor,
    ): TopAppBarColors
}

@Immutable
internal data class DefaultTopAppBarColors(
    override val containerColor: Color = Color.Unspecified,
    override val navigationColor: Color = Color.Unspecified,
    override val titleColor: Color = Color.Unspecified,
    override val subTitleColor: Color = Color.Unspecified,
    override val actionColor: Color = Color.Unspecified,
) : TopAppBarColors {
    override fun copyColors(
        containerColor: Color,
        navigationColor: Color,
        titleColor: Color,
        subTitleColor: Color,
        actionColor: Color
    ) = this.copy(
        containerColor = containerColor,
        navigationColor = navigationColor,
        titleColor = titleColor,
        subTitleColor = subTitleColor,
        actionColor = actionColor
    )
}

@Immutable
internal data class LightTopAppBarColors(
    override val containerColor: Color = Color(0xFFFFFFFF),
    override val navigationColor: Color = Color(0xFF000000),
    override val titleColor: Color = Color(0xFF000000),
    override val subTitleColor: Color = Color(0xFF8A8888),
    override val actionColor: Color = Color(0xFF000000),
) : TopAppBarColors {
    override fun copyColors(
        containerColor: Color,
        navigationColor: Color,
        titleColor: Color,
        subTitleColor: Color,
        actionColor: Color
    ) = this.copy(
        containerColor = containerColor,
        navigationColor = navigationColor,
        titleColor = titleColor,
        subTitleColor = subTitleColor,
        actionColor = actionColor
    )
}

@Immutable
internal data class DarkTopAppBarColors(
    override val containerColor: Color = Color(0xFF000000),
    override val navigationColor: Color = Color(0xFFFFFFFF),
    override val titleColor: Color = Color(0xFFFFFFFF),
    override val subTitleColor: Color = Color(0xFF8A8888),
    override val actionColor: Color = Color(0xFFFFFFFF),
) : TopAppBarColors {
    override fun copyColors(
        containerColor: Color,
        navigationColor: Color,
        titleColor: Color,
        subTitleColor: Color,
        actionColor: Color
    ) = this.copy(
        containerColor = containerColor,
        navigationColor = navigationColor,
        titleColor = titleColor,
        subTitleColor = subTitleColor,
        actionColor = actionColor
    )
}

@Immutable
internal data class TransparentTopAppBarColors(
    override val containerColor: Color = Color(0x00000000),
    override val navigationColor: Color = Color(0xFFFFFFFF),
    override val titleColor: Color = Color(0xFFFFFFFF),
    override val subTitleColor: Color = Color(0xFFFFFFFF),
    override val actionColor: Color = Color(0xFFFFFFFF),
) : TopAppBarColors {
    override fun copyColors(
        containerColor: Color,
        navigationColor: Color,
        titleColor: Color,
        subTitleColor: Color,
        actionColor: Color
    ) = this.copy(
        containerColor = containerColor,
        navigationColor = navigationColor,
        titleColor = titleColor,
        subTitleColor = subTitleColor,
        actionColor = actionColor
    )
}
