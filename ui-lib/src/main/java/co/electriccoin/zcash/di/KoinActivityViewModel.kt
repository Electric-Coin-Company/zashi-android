package co.electriccoin.zcash.di

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import org.koin.androidx.compose.defaultExtras
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.currentKoinScope
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

@Suppress("LongParameterList")
@Composable
inline fun <reified T : ViewModel> koinActivityViewModel(
    qualifier: Qualifier? = null,
    viewModelStoreOwner: ViewModelStoreOwner = LocalContext.componentActivity(),
    key: String? = null,
    extras: CreationExtras = defaultExtras(LocalContext.componentActivity()),
    scope: Scope = currentKoinScope(),
    noinline parameters: ParametersDefinition? = null,
) = koinViewModel<T>(
    qualifier = qualifier,
    viewModelStoreOwner = viewModelStoreOwner,
    key = key,
    extras = extras,
    scope = scope,
    parameters = parameters,
)

@Composable
fun ProvidableCompositionLocal<Context>.componentActivity(): ComponentActivity {
    val context = this.current
    return when {
        context is ComponentActivity -> context
        context is ContextWrapper && context.baseContext is ComponentActivity ->
            context.baseContext as ComponentActivity

        else -> throw ClassCastException("Context is not a ComponentActivity")
    }
}
