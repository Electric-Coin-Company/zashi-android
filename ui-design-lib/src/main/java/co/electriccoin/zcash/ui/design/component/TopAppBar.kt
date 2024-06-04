@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.internal.SecondaryTypography
import co.electriccoin.zcash.ui.design.theme.internal.TopAppBarColors

@Preview
@Composable
private fun TopAppBarTextComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            SmallTopAppBar(titleText = "Screen A", backText = "Back")
        }
    }
}

@Preview
@Composable
private fun TopAppBarTextRestoringComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            SmallTopAppBar(
                titleText = "Screen A",
                backText = "Back",
                subTitle = "[RESTORING YOUR WALLET…]"
            )
        }
    }
}

@Preview
@Composable
private fun TopAppBarTextRestoringLongComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            SmallTopAppBar(
                titleText = "Screen A",
                backText = "Back",
                subTitle = "[RESTORING YOUR WALLET LONG TEXT…]"
            )
        }
    }
}

@Preview
@Composable
private fun TopAppBarLogoComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            SmallTopAppBar(showTitleLogo = true, backText = "Back")
        }
    }
}

@Preview
@Composable
private fun TopAppBarLogoRestoringComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            SmallTopAppBar(
                showTitleLogo = true,
                backText = "Back",
                subTitle = "[RESTORING YOUR WALLET…]"
            )
        }
    }
}

@Preview
@Composable
private fun TopAppBarRegularMenuComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            SmallTopAppBar(
                titleText = "Screen B",
                regularActions = {
                    TopBarRegularMenuExample(
                        actionOneCallback = {},
                        actionTwoCallback = {}
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun TopAppBarOneVisibleActionMenuComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            SmallTopAppBar(
                titleText = "Screen C",
                backText = "Back",
                regularActions = {
                    TopBarOneVisibleActionMenuExample(
                        actionCallback = {}
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun TopAppBarHamburgerMenuComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            SmallTopAppBar(
                titleText = "Screen D",
                backText = "Back",
                hamburgerMenuActions = {
                    TopBarHamburgerMenuExample(
                        actionCallback = {}
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun TopAppBarHamburgerPlusActionComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            SmallTopAppBar(
                titleText = "Screen E",
                hamburgerMenuActions = {
                    TopBarHamburgerMenuExample(
                        actionCallback = {}
                    )
                },
                regularActions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Content description text"
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun TopBarHamburgerMenuExample(
    modifier: Modifier = Modifier,
    actionCallback: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        var expanded by rememberSaveable { mutableStateOf(false) }
        IconButton(
            onClick = {
                expanded = true
                actionCallback()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.hamburger_menu_icon),
                contentDescription = "Content description text"
            )
        }
    }
}

@Composable
private fun TopBarRegularMenuExample(
    actionOneCallback: () -> Unit,
    actionTwoCallback: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        var expanded by rememberSaveable { mutableStateOf(false) }
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Content description text"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Action One") },
                onClick = {
                    actionOneCallback()
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Action Two") },
                onClick = {
                    actionTwoCallback()
                    expanded = false
                }
            )
        }
    }
}

@Composable
private fun TopBarOneVisibleActionMenuExample(
    modifier: Modifier = Modifier,
    actionCallback: () -> Unit
) {
    Reference(
        text = "Action 1",
        onClick = actionCallback,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(all = ZcashTheme.dimens.spacingDefault)
    )
}

@Composable
@Suppress("LongParameterList")
fun GridBgSmallTopAppBar(
    modifier: Modifier = Modifier,
    backContentDescriptionText: String? = null,
    backText: String? = null,
    colors: TopAppBarColors = ZcashTheme.colors.topAppBarColors,
    hamburgerMenuActions: (@Composable RowScope.() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    regularActions: (@Composable RowScope.() -> Unit)? = null,
    subTitle: String? = null,
    showTitleLogo: Boolean = false,
    titleText: String? = null,
) {
    SmallTopAppBar(
        modifier =
            modifier.then(
                Modifier.gridBackground(
                    backgroundColor = ZcashTheme.colors.backgroundColor,
                    gridSize = ZcashTheme.dimens.gridCellSize,
                    gridColor = ZcashTheme.colors.gridColor,
                    gridLineWidth = ZcashTheme.dimens.gridLineWidth
                )
            ),
        backContentDescriptionText = backContentDescriptionText,
        backText = backText,
        colors = colors.copyColors(containerColor = Color.Transparent),
        hamburgerMenuActions = hamburgerMenuActions,
        onBack = onBack,
        regularActions = regularActions,
        subTitle = subTitle,
        showTitleLogo = showTitleLogo,
        titleText = titleText
    )
}

@Composable
@Suppress("LongParameterList", "LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
fun SmallTopAppBar(
    modifier: Modifier = Modifier,
    backContentDescriptionText: String? = null,
    backText: String? = null,
    colors: TopAppBarColors = ZcashTheme.colors.topAppBarColors,
    hamburgerMenuActions: (@Composable RowScope.() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    regularActions: (@Composable RowScope.() -> Unit)? = null,
    subTitle: String? = null,
    showTitleLogo: Boolean = false,
    titleText: String? = null,
) {
    CenterAlignedTopAppBar(
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var restoringSpacerHeight: Dp = 0.dp

                if (titleText != null) {
                    Text(
                        text = titleText.uppercase(),
                        style = SecondaryTypography.headlineSmall,
                        color = colors.titleColor,
                    )
                    restoringSpacerHeight = ZcashTheme.dimens.spacingTiny
                } else if (showTitleLogo) {
                    Icon(
                        painter = painterResource(id = R.drawable.zashi_text_logo),
                        contentDescription = null,
                        tint = colors.titleColor,
                        modifier = Modifier.height(ZcashTheme.dimens.topAppBarZcashLogoHeight)
                    )
                    restoringSpacerHeight = ZcashTheme.dimens.spacingSmall
                }

                if (subTitle != null) {
                    Spacer(modifier = Modifier.height(restoringSpacerHeight))

                    @Suppress("MagicNumber")
                    Text(
                        text = subTitle.uppercase(),
                        style = ZcashTheme.extendedTypography.restoringTopAppBarStyle,
                        color = colors.subTitleColor,
                        modifier = Modifier.fillMaxWidth(0.75f),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        navigationIcon = {
            backText?.let {
                Box(
                    modifier =
                        Modifier
                            .wrapContentSize()
                            .clip(RoundedCornerShape(ZcashTheme.dimens.regularRippleEffectCorner))
                            .clickable { onBack?.run { onBack() } }
                ) {
                    Row(
                        modifier = Modifier.padding(all = ZcashTheme.dimens.spacingDefault),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = backContentDescriptionText,
                            tint = colors.navigationColor,
                        )
                        Spacer(modifier = Modifier.size(size = ZcashTheme.dimens.spacingSmall))
                        Text(text = backText.uppercase())
                    }
                }
            }
        },
        actions = {
            regularActions?.invoke(this)
            hamburgerMenuActions?.invoke(this)
        },
        colors = colors.toMaterialTopAppBarColors(),
        modifier =
            Modifier
                .testTag(CommonTag.TOP_APP_BAR)
                .then(modifier)
    )
}
