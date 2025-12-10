@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.time.Month
import java.time.Year
import java.time.YearMonth
import kotlin.math.absoluteValue
import kotlin.math.pow

@Suppress("MagicNumber")
@Composable
fun ZashiYearMonthWheelDatePicker(
    selection: YearMonth,
    onSelectionChange: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
    verticallyVisibleItems: Int = 3,
    startInclusive: YearMonth = YearMonth.of(2018, 10),
    endInclusive: YearMonth = YearMonth.now(),
) {
    val latestOnSelectionChanged by rememberUpdatedState(onSelectionChange)

    var state by remember {
        mutableStateOf(
            InternalState(
                selectedDate = selection,
                months = getMonthsForYear(Year.of(selection.year), startInclusive, endInclusive),
                years = (startInclusive.year..endInclusive.year).map { Year.of(it) }.toList()
            )
        )
    }

    LaunchedEffect(state.selectedDate) {
        Twig.debug { "Selection changed: ${state.selectedDate}" }
        latestOnSelectionChanged(state.selectedDate)
    }

    Box(modifier = modifier) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
        ) {
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .height(34.dp)
                        .padding(top = 1.dp)
                        .background(ZashiColors.Surfaces.bgSecondary, RoundedCornerShape(6.dp))
            )
            Spacer(36.dp)
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .height(34.dp)
                        .padding(top = 1.dp)
                        .background(ZashiColors.Surfaces.bgSecondary, RoundedCornerShape(6.dp))
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            WheelLazyList(
                modifier = Modifier.weight(1f),
                selection = state.selectedMonthIndex,
                itemCount = state.months.size,
                itemVerticalOffset = verticallyVisibleItems,
                isInfiniteScroll = false,
                onFocusItem = {
                    state =
                        state.copy(
                            selectedDate = state.selectedDate.withMonth(state.months[it].value)
                        )
                },
                itemContent = {
                    Text(
                        text = DateFormatSymbols().months[state.months[it].value - 1],
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillParentMaxWidth(),
                        style = ZashiTypography.header6,
                        color = ZashiColors.Text.textPrimary,
                        maxLines = 1
                    )
                }
            )
            Spacer(36.dp)
            WheelLazyList(
                modifier = Modifier.weight(1f),
                selection = state.selectedYearIndex,
                itemCount = state.years.size,
                itemVerticalOffset = verticallyVisibleItems,
                isInfiniteScroll = false,
                onFocusItem = {
                    val year = state.years[it]
                    val normalizedSelectedMonth =
                        getSelectedMonthForYear(
                            year = year,
                            selectedMonth = state.selectedDate.month,
                            startYearMonth = startInclusive,
                            endYearMonth = endInclusive
                        )
                    val months = getMonthsForYear(year, startInclusive, endInclusive)
                    val selectedDate = state.selectedDate.withYear(year.value).withMonth(normalizedSelectedMonth.value)
                    state =
                        state.copy(
                            selectedDate = selectedDate,
                            months = months
                        )
                },
                itemContent = {
                    Text(
                        text = state.years[it].toString(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillParentMaxWidth(),
                        style = ZashiTypography.header6,
                        color = ZashiColors.Text.textPrimary,
                        maxLines = 1
                    )
                }
            )
        }
    }
}

private fun getMonthsForYear(year: Year, startYearMonth: YearMonth, endYearMonth: YearMonth): List<Month> =
    when (year.value) {
        startYearMonth.year -> {
            (startYearMonth.month.value..Month.DECEMBER.value).map { index ->
                Month.entries.first { it.value == index }
            }
        }

        endYearMonth.year -> {
            (Month.JANUARY.value..endYearMonth.month.value).map { index ->
                Month.entries.first { it.value == index }
            }
        }

        else -> {
            listOf(
                Month.JANUARY,
                Month.FEBRUARY,
                Month.MARCH,
                Month.APRIL,
                Month.MAY,
                Month.JUNE,
                Month.JULY,
                Month.AUGUST,
                Month.SEPTEMBER,
                Month.OCTOBER,
                Month.NOVEMBER,
                Month.DECEMBER
            )
        }
    }

private fun getSelectedMonthForYear(
    year: Year,
    selectedMonth: Month,
    startYearMonth: YearMonth,
    endYearMonth: YearMonth
): Month =
    when (year.value) {
        startYearMonth.year -> {
            val months =
                (startYearMonth.month.value..Month.DECEMBER.value).map { index ->
                    Month.entries.first { it.value == index }
                }
            if (selectedMonth in months) selectedMonth else months.findClosest(selectedMonth)
        }

        endYearMonth.year -> {
            val months =
                (Month.JANUARY.value..endYearMonth.month.value).map { index ->
                    Month.entries.first { it.value == index }
                }
            if (selectedMonth in months) selectedMonth else months.findClosest(selectedMonth)
        }

        else -> selectedMonth
    }

private fun List<Month>.findClosest(target: Month): Month {
    var closestNumber = this[0] // Initialize with the first element
    var minDifference = (this[0].value - target.value).absoluteValue

    for (number in this) {
        val difference = (number.value - target.value).absoluteValue
        if (difference < minDifference) {
            minDifference = difference
            closestNumber = number
        }
    }

    return closestNumber
}

@Suppress("MagicNumber", "ContentSlotReused")
@Composable
private fun WheelLazyList(
    itemCount: Int,
    selection: Int,
    itemVerticalOffset: Int,
    onFocusItem: (Int) -> Unit,
    isInfiniteScroll: Boolean,
    modifier: Modifier = Modifier,
    itemContent: @Composable LazyItemScope.(index: Int) -> Unit,
) {
    val latestOnFocusItem by rememberUpdatedState(onFocusItem)
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val count = if (isInfiniteScroll) itemCount else itemCount + 2 * itemVerticalOffset
    val rowOffsetCount = maxOf(1, minOf(itemVerticalOffset, 4))
    val rowCount = (rowOffsetCount * 2) + 1
    val startIndex = if (isInfiniteScroll) selection + (itemCount * 1000) - itemVerticalOffset else selection
    val state = rememberLazyListState(startIndex)
    val itemHeightPx = with(LocalDensity.current) { 27.dp.toPx() }
    val height = 32.dp * rowCount
    val isScrollInProgress = state.isScrollInProgress

    LaunchedEffect(itemCount) {
        state.scrollToItem(startIndex)
    }

    LaunchedEffect(key1 = isScrollInProgress) {
        if (!isScrollInProgress) {
            calculateIndexToFocus(state, height).let {
                val indexToFocus =
                    if (isInfiniteScroll) {
                        (it + rowOffsetCount) % itemCount
                    } else {
                        ((it + rowOffsetCount) % count) - itemVerticalOffset
                    }

                latestOnFocusItem(indexToFocus)

                if (state.firstVisibleItemScrollOffset != 0) {
                    coroutineScope.launch {
                        state.animateScrollToItem(it, 0)
                    }
                }
            }
        }
    }

    LaunchedEffect(state) {
        snapshotFlow { state.firstVisibleItemIndex }
            .drop(1)
            .collect {
                runCatching { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
            }
    }
    Box(
        modifier =
            modifier
                .height(height)
                .fillMaxWidth(),
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .height(height)
                    .fillMaxWidth(),
            state = state,
        ) {
            items(if (isInfiniteScroll) Int.MAX_VALUE else count) { index ->
                val (scale, alpha, translationY) =
                    remember {
                        derivedStateOf {
                            val info = state.layoutInfo
                            val middleOffset = info.viewportSize.height / 2
                            val item = info.visibleItemsInfo.firstOrNull { it.index == index }
                            val scrollOffset = if (item != null) item.offset + item.size / 2 else -1
                            val coefficient = calculateCoefficient(middleOffset = middleOffset, offset = scrollOffset)
                            val scale = calculateScale(coefficient)
                            val alpha = calculateAlpha(coefficient)
                            val translationY =
                                calculateTranslationY(
                                    coefficient = coefficient,
                                    itemHeightPx = itemHeightPx,
                                    middleOffset = middleOffset,
                                    offset = scrollOffset
                                )
                            Triple(scale, alpha, translationY)
                        }
                    }.value

                Box(
                    modifier =
                        Modifier
                            .height(height / rowCount)
                            .fillMaxWidth()
                            .graphicsLayer {
                                this.alpha = alpha
                                this.scaleX = scale
                                this.scaleY = scale
                                this.translationY = translationY
                            },
                    contentAlignment = Alignment.Center,
                ) {
                    if (isInfiniteScroll) {
                        itemContent(index % itemCount)
                    } else if (index >= rowOffsetCount && index < itemCount + rowOffsetCount) {
                        itemContent((index - rowOffsetCount) % itemCount)
                    }
                }
            }
        }
    }
}

@Suppress("MagicNumber")
private fun calculateCoefficient(
    middleOffset: Int,
    offset: Int
): Float {
    val diff = if (middleOffset > offset) middleOffset - offset else offset - middleOffset
    return (1f - (diff.toFloat() / middleOffset.toFloat())).coerceAtLeast(0f)
}

@Suppress("MagicNumber")
private fun calculateScale(coefficient: Float): Float = coefficient.coerceAtLeast(.6f)

@Suppress("MagicNumber")
private fun calculateAlpha(coefficient: Float): Float = coefficient.pow(1.1f)

@Suppress("MagicNumber")
private fun calculateTranslationY(
    coefficient: Float,
    itemHeightPx: Float,
    middleOffset: Int,
    offset: Int
): Float {
    // if (coefficient in 0.66f..1f) return 0f
    val exponentialCoefficient = 1.2f - 5f.pow(-(coefficient))
    val offsetBy = (1 - exponentialCoefficient) * itemHeightPx
    return if (middleOffset > offset) offsetBy else -offsetBy
}

@Suppress("MagicNumber")
private fun calculateIndexToFocus(
    listState: LazyListState,
    height: Dp
): Int {
    val currentItem = listState.layoutInfo.visibleItemsInfo.firstOrNull()
    var index = currentItem?.index ?: 0
    if (currentItem?.offset != 0 && currentItem != null && currentItem.offset <= -height.value * 3 / 10) {
        index++
    }
    return index
}

@Immutable
private data class InternalState(
    val selectedDate: YearMonth,
    val months: List<Month>,
    val years: List<Year>
) {
    val selectedYearIndex = years.map { it.value }.indexOf(selectedDate.year)
    val selectedMonthIndex = maxOf(months.indexOf(selectedDate.month), 0)
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            ZashiYearMonthWheelDatePicker(
                selection = YearMonth.now(),
                onSelectionChange = {}
            )
        }
    }
