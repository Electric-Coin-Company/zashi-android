package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import android.content.Intent
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getString
import co.electriccoin.zcash.ui.util.EmailUtil

class SendEmailUseCase(
    private val context: Context,
) {
    suspend operator fun invoke(
        address: StringResource,
        subject: StringResource,
        message: StringResource
    ) {
        val intent =
            EmailUtil.newMailActivityIntent(
                recipientAddress = address.getString(context),
                messageSubject = subject.getString(context),
                messageBody = message.getString(context)
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

        context.startActivity(intent)
    }
}
