package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import android.content.Intent
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getString
import co.electriccoin.zcash.ui.screen.feedback.model.FeedbackEmoji
import co.electriccoin.zcash.ui.screen.support.model.SupportInfoType
import co.electriccoin.zcash.ui.util.EmailUtil

class SendSupportEmailUseCase(
    private val context: Context,
    private val getSupport: GetSupportUseCase
) {
    suspend operator fun invoke(
        emoji: FeedbackEmoji,
        message: StringResource
    ) {
        val intent =
            EmailUtil
                .newMailActivityIntent(
                    recipientAddress = context.getString(R.string.support_email_address),
                    messageSubject = context.getString(R.string.app_name),
                    messageBody =
                        buildString {
                            appendLine(
                                context.getString(
                                    R.string.support_email_part_1,
                                    emoji.encoding,
                                    emoji.order.toString()
                                )
                            )
                            appendLine()
                            appendLine(context.getString(R.string.support_email_part_2, message.getString(context)))
                            appendLine()
                            appendLine()
                            appendLine(getSupport().toSupportString(SupportInfoType.entries.toSet()))
                        }
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

        context.startActivity(intent)
    }
}
