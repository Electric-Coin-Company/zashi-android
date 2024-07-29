package co.electriccoin.zcash.ui.screen.whatsnew.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.TopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.fixture.ChangelogFixture
import co.electriccoin.zcash.ui.screen.whatsnew.model.WhatsNewSectionState
import co.electriccoin.zcash.ui.screen.whatsnew.model.WhatsNewState
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WhatsNewView(
    state: WhatsNewState,
    walletState: TopAppBarSubTitleState,
    onBack: () -> Unit
) {
    BlankBgScaffold(
        topBar = {
            AppBar(walletState = walletState, onBack = onBack)
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingDefault,
                        bottom = paddingValues.calculateBottomPadding() + ZcashTheme.dimens.spacingDefault,
                        start = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                        end = ZcashTheme.dimens.screenHorizontalSpacingRegular
                    )
                    .verticalScroll(rememberScrollState())
        ) {
            Row {
                Text(
                    text = state.version.getValue(),
                    style = ZcashTheme.typography.primary.titleSmall,
                    fontSize = 13.sp
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = DateTimeFormatter.ISO_LOCAL_DATE.format(state.date.toJavaLocalDate()),
                    textAlign = TextAlign.End,
                    style = ZcashTheme.typography.primary.titleSmall,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

            state.sections.forEach { section ->
                WhatsNewSection(section)
                Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))
            }
        }
    }
}

@Composable
private fun WhatsNewSection(state: WhatsNewSectionState) {
    val bulletString = "\u2022\t\t"
    val bulletTextStyle = MaterialTheme.typography.bodySmall
    val bulletTextMeasurer = rememberTextMeasurer()
    val bulletStringWidth =
        remember(bulletTextStyle, bulletTextMeasurer) {
            bulletTextMeasurer.measure(text = bulletString, style = bulletTextStyle).size.width
        }
    val bulletRestLine = with(LocalDensity.current) { bulletStringWidth.toSp() }
    val bulletParagraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = bulletRestLine))
    val bulletStyle =
        state.content.getValue().split("\n-")
            .filter { it.isNotBlank() }
            .map {
                it.replace("\n-", "").trim()
            }
            .let { text ->
                buildAnnotatedString {
                    text.forEach {
                        withStyle(style = bulletParagraphStyle) {
                            append(bulletString)
                            append(it)
                        }
                    }
                }
            }

    Column {
        Text(
            text = state.title.getValue(),
            style = ZcashTheme.typography.primary.titleSmall,
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingMin))

        Text(
            text = bulletStyle,
            style = bulletTextStyle
        )
    }
}

@Composable
private fun AppBar(
    walletState: TopAppBarSubTitleState,
    onBack: () -> Unit
) {
    SmallTopAppBar(
        subTitle =
            when (walletState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        titleText = stringResource(id = R.string.whats_new_title).uppercase(),
        navigationAction = {
            TopAppBarBackNavigation(
                backText = stringResource(id = R.string.back_navigation).uppercase(),
                backContentDescriptionText = stringResource(R.string.back_navigation_content_description),
                onBack = onBack
            )
        },
    )
}

@Composable
private fun WhatsNewViewPreview() {
    BlankSurface {
        WhatsNewView(
            state = WhatsNewState.new(ChangelogFixture.new()),
            walletState = TopAppBarSubTitleState.None,
            onBack = {}
        )
    }
}

@Preview
@Composable
private fun WhatsNewViewPreviewLight() =
    ZcashTheme(forceDarkMode = false) {
        WhatsNewViewPreview()
    }

@Preview
@Composable
private fun WhatsNewViewPreviewDark() =
    ZcashTheme(forceDarkMode = true) {
        WhatsNewViewPreview()
    }
