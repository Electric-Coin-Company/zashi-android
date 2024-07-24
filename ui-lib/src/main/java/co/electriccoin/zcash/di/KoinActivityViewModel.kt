package co.electriccoin.zcash.di

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
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
    viewModelStoreOwner: ViewModelStoreOwner = LocalContext.current as ComponentActivity,
    key: String? = null,
    extras: CreationExtras = defaultExtras(LocalContext.current as ComponentActivity),
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
