package co.electroniccoin.zcash.ui.screenshot

import android.app.Application
import android.content.Context
import co.electriccoin.zcash.di.coreModule
import co.electriccoin.zcash.di.repositoryModule
import co.electriccoin.zcash.di.useCaseModule
import co.electriccoin.zcash.di.viewModelModule
import co.electriccoin.zcash.test.ZcashUiTestRunner
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ZcashScreenshotTestRunner : ZcashUiTestRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, ZcashUiTestApplication::class.java.name, context)
    }
}

class ZcashUiTestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@ZcashUiTestApplication)
            modules(
                coreModule,
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
        }
    }
}
