package co.electriccoin.zcash.app.di

import co.electriccoin.zcash.ui.common.usecase.CloseSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.GetPersistableWalletUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveFastestServersUseCase
import co.electriccoin.zcash.ui.common.usecase.ObservePersistableWalletUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.PersistEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.PersistWalletUseCase
import co.electriccoin.zcash.ui.common.usecase.RefreshFastestServersUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateServerEndpointUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule =
    module {
        factoryOf(::ObserveSynchronizerUseCase)
        factoryOf(::CloseSynchronizerUseCase)
        factoryOf(::GetPersistableWalletUseCase)
        factoryOf(::GetSynchronizerUseCase)
        factoryOf(::ObservePersistableWalletUseCase)
        factoryOf(::PersistEndpointUseCase)
        factoryOf(::PersistWalletUseCase)
        factoryOf(::ValidateServerEndpointUseCase)
        factoryOf(::ValidateEndpointUseCase)
        factoryOf(::ObserveFastestServersUseCase)
        factoryOf(::RefreshFastestServersUseCase)
    }
