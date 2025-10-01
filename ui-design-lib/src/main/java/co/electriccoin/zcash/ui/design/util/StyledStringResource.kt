package co.electriccoin.zcash.ui.design.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

@Immutable
sealed interface StyledStringResource {
    @Immutable
    data class ByStringResource(
        val resource: StringResource,
        val color: StringResourceColor,
        val fontWeight: FontWeight?,
    ) : StyledStringResource

    @Immutable
    data class ByResource(
        @StringRes val resource: Int,
        val color: StringResourceColor,
        val fontWeight: FontWeight?,
        val args: List<Any>
    ) : StyledStringResource

    operator fun plus(
        other: StyledStringResource
    ): StyledStringResource = CompositeStyledStringResource(listOf(this, other))
}

@Immutable
private data class CompositeStyledStringResource(
    val resources: List<StyledStringResource>
) : StyledStringResource

@Stable
fun styledStringResource(
    stringResource: StringResource,
    color: StringResourceColor = StringResourceColor.PRIMARY,
    fontWeight: FontWeight? = null,
): StyledStringResource =
    StyledStringResource.ByStringResource(
        resource = stringResource,
        color = color,
        fontWeight = fontWeight,
    )

@Stable
fun styledStringResource(
    @StringRes resource: Int,
    color: StringResourceColor,
    fontWeight: FontWeight?,
    vararg args: Any
): StyledStringResource =
    StyledStringResource.ByResource(
        resource = resource,
        color = color,
        fontWeight = fontWeight,
        args = args.toList()
    )

@Stable
fun styledStringResource(
    @StringRes resource: Int,
    fontWeight: FontWeight?,
    vararg args: Any
): StyledStringResource =
    StyledStringResource.ByResource(
        resource = resource,
        color = StringResourceColor.PRIMARY,
        fontWeight = fontWeight,
        args = args.toList()
    )

@Stable
fun styledStringResource(
    @StringRes resource: Int,
    color: StringResourceColor,
    vararg args: Any
): StyledStringResource =
    StyledStringResource.ByResource(
        resource = resource,
        color = color,
        fontWeight = null,
        args = args.toList()
    )

@Stable
fun styledStringResource(
    @StringRes resource: Int,
    vararg args: Any
): StyledStringResource =
    StyledStringResource.ByResource(
        resource = resource,
        color = StringResourceColor.PRIMARY,
        fontWeight = null,
        args = args.toList()
    )

@Suppress("SpreadOperator")
@Composable
fun StyledStringResource.getValue() =
    when (this) {
        is StyledStringResource.ByStringResource ->
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = color.getColor(), fontWeight = fontWeight)) {
                    append(resource.getValue())
                }
            }

        is StyledStringResource.ByResource -> {
            val argsStrings =
                args.map { arg ->
                    when (arg) {
                        is StringResource -> arg.getValue()
                        is StyledStringResource -> {
                            when (arg) {
                                is StyledStringResource.ByResource ->
                                    stringRes(
                                        arg.resource,
                                        *arg.args.map { if (it is StringResource) it.getValue() else it }.toTypedArray()
                                    ).getValue()

                                is StyledStringResource.ByStringResource -> arg.resource.getValue()
                                is CompositeStyledStringResource -> arg.convertComposite()
                            }
                        }

                        else -> arg
                    }
                }

            val fullString = stringRes(resource, *argsStrings.toTypedArray()).getValue()

            buildAnnotatedString {
                withStyle(
                    style =
                        SpanStyle(
                            color = color.getColor(),
                            fontWeight = fontWeight
                        )
                ) {
                    append(fullString)
                }

                argsStrings.forEachIndexed { index, string ->
                    when (val res = args[index]) {
                        is StyledStringResource.ByResource ->
                            addStyle(
                                fullString = fullString,
                                string = string.toString(),
                                color = res.color.getColor(),
                                fontWeight = res.fontWeight
                            )

                        is StyledStringResource.ByStringResource ->
                            addStyle(
                                fullString = fullString,
                                string = string.toString(),
                                color = res.color.getColor(),
                                fontWeight = res.fontWeight
                            )
                    }
                }
            }
        }

        is CompositeStyledStringResource -> convertComposite()
    }

@Composable
private fun CompositeStyledStringResource.convertComposite(): AnnotatedString =
    buildAnnotatedString {
        resources.forEach {
            val value: AnnotatedString = it.getValue()
            append(value)
        }
    }

@Composable
private fun AnnotatedString.Builder.addStyle(
    fullString: String,
    string: String,
    color: Color,
    fontWeight: FontWeight?
) {
    val startIndex = fullString.indexOf(string)

    addStyle(
        style =
            SpanStyle(
                color = color,
                fontWeight = fontWeight
            ),
        start = startIndex,
        end = startIndex + string.length
    )
}
