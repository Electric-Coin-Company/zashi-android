package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.AccountDataSourceImpl
import co.electriccoin.zcash.ui.common.datasource.ProposalDataSource
import co.electriccoin.zcash.ui.common.datasource.ProposalDataSourceImpl
import co.electriccoin.zcash.ui.common.datasource.RestoreTimestampDataSource
import co.electriccoin.zcash.ui.common.datasource.RestoreTimestampDataSourceImpl
import co.electriccoin.zcash.ui.common.datasource.ZashiSpendingKeyDataSource
import co.electriccoin.zcash.ui.common.datasource.ZashiSpendingKeyDataSourceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataSourceModule =
    module {
        singleOf(::AccountDataSourceImpl) bind AccountDataSource::class
        singleOf(::ZashiSpendingKeyDataSourceImpl) bind ZashiSpendingKeyDataSource::class
        singleOf(::ProposalDataSourceImpl) bind ProposalDataSource::class
        singleOf(::RestoreTimestampDataSourceImpl) bind RestoreTimestampDataSource::class
    }
