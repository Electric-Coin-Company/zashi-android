package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.usecase.ApplyTransactionFiltersUseCase
import co.electriccoin.zcash.ui.common.usecase.ApplyTransactionFulltextFiltersUseCase
import co.electriccoin.zcash.ui.common.usecase.CancelProposalFlowUseCase
import co.electriccoin.zcash.ui.common.usecase.CancelSwapUseCase
import co.electriccoin.zcash.ui.common.usecase.ConfirmProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.CopyToClipboardUseCase
import co.electriccoin.zcash.ui.common.usecase.CreateFlexaTransactionUseCase
import co.electriccoin.zcash.ui.common.usecase.CreateKeystoneAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.CreateKeystoneProposalPCZTEncoderUseCase
import co.electriccoin.zcash.ui.common.usecase.CreateOrUpdateTransactionNoteUseCase
import co.electriccoin.zcash.ui.common.usecase.CreateProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.DeleteContactUseCase
import co.electriccoin.zcash.ui.common.usecase.DeleteTransactionNoteUseCase
import co.electriccoin.zcash.ui.common.usecase.DeriveKeystoneAccountUnifiedAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ExportTaxUseCase
import co.electriccoin.zcash.ui.common.usecase.FilterSwapAssetsUseCase
import co.electriccoin.zcash.ui.common.usecase.FlipTransactionBookmarkUseCase
import co.electriccoin.zcash.ui.common.usecase.GetCoinbaseStatusUseCase
import co.electriccoin.zcash.ui.common.usecase.GetConfigurationUseCase
import co.electriccoin.zcash.ui.common.usecase.GetContactByAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.GetCurrentFilteredTransactionsUseCase
import co.electriccoin.zcash.ui.common.usecase.GetExchangeRateUseCase
import co.electriccoin.zcash.ui.common.usecase.GetFlexaStatusUseCase
import co.electriccoin.zcash.ui.common.usecase.GetHomeMessageUseCase
import co.electriccoin.zcash.ui.common.usecase.GetKeystoneStatusUseCase
import co.electriccoin.zcash.ui.common.usecase.GetMetadataUseCase
import co.electriccoin.zcash.ui.common.usecase.GetProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedSwapAssetUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSlippageUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSupportUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSwapAssetsUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransactionDetailByIdUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransactionFiltersUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransactionMetadataUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransactionsUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransparentAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.GetWalletAccountsUseCase
import co.electriccoin.zcash.ui.common.usecase.GetWalletRestoringStateUseCase
import co.electriccoin.zcash.ui.common.usecase.GetZashiAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.IsCoinbaseAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.IsFlexaAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.IsRestoreSuccessDialogVisibleUseCase
import co.electriccoin.zcash.ui.common.usecase.MarkTxMemoAsReadUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToAddressBookUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToCoinbaseUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToErrorUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToNearSwapUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToReceiveUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToRequestShieldedUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToTaxExportUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToWalletBackupUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveAddressBookContactsUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveClearSendUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactByAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactPickedUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveFastestServersUseCase
import co.electriccoin.zcash.ui.common.usecase.ObservePersistableWalletUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSelectedEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveTransactionSubmitStateUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveZashiAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.OnAddressScannedUseCase
import co.electriccoin.zcash.ui.common.usecase.OnUserSavedWalletBackupUseCase
import co.electriccoin.zcash.ui.common.usecase.OnZip321ScannedUseCase
import co.electriccoin.zcash.ui.common.usecase.ParseKeystonePCZTUseCase
import co.electriccoin.zcash.ui.common.usecase.ParseKeystoneSignInRequestUseCase
import co.electriccoin.zcash.ui.common.usecase.ParseKeystoneUrToZashiAccountsUseCase
import co.electriccoin.zcash.ui.common.usecase.PersistEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.PrefillSendUseCase
import co.electriccoin.zcash.ui.common.usecase.RefreshFastestServersUseCase
import co.electriccoin.zcash.ui.common.usecase.RemindWalletBackupLaterUseCase
import co.electriccoin.zcash.ui.common.usecase.RescanBlockchainUseCase
import co.electriccoin.zcash.ui.common.usecase.RescanQrUseCase
import co.electriccoin.zcash.ui.common.usecase.ResetInMemoryDataUseCase
import co.electriccoin.zcash.ui.common.usecase.ResetSharedPrefsDataUseCase
import co.electriccoin.zcash.ui.common.usecase.ResetTransactionFiltersUseCase
import co.electriccoin.zcash.ui.common.usecase.RestoreWalletUseCase
import co.electriccoin.zcash.ui.common.usecase.SaveContactUseCase
import co.electriccoin.zcash.ui.common.usecase.SelectSwapAssetUseCase
import co.electriccoin.zcash.ui.common.usecase.SelectWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.SendEmailUseCase
import co.electriccoin.zcash.ui.common.usecase.SendSupportEmailUseCase
import co.electriccoin.zcash.ui.common.usecase.SendTransactionAgainUseCase
import co.electriccoin.zcash.ui.common.usecase.SetSlippageUseCase
import co.electriccoin.zcash.ui.common.usecase.ShareImageUseCase
import co.electriccoin.zcash.ui.common.usecase.SharePCZTUseCase
import co.electriccoin.zcash.ui.common.usecase.ShieldFundsMessageUseCase
import co.electriccoin.zcash.ui.common.usecase.ShieldFundsUseCase
import co.electriccoin.zcash.ui.common.usecase.UpdateContactUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateContactAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateContactNameUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateSeedUseCase
import co.electriccoin.zcash.ui.common.usecase.ViewTransactionDetailAfterSuccessfulProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.ViewTransactionsAfterSuccessfulProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.Zip321BuildUriUseCase
import co.electriccoin.zcash.ui.common.usecase.Zip321ParseUriValidationUseCase
import co.electriccoin.zcash.ui.util.closeableCallback
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.dsl.onClose

val useCaseModule =
    module {
        factoryOf(::ObserveSynchronizerUseCase)
        factoryOf(::GetSynchronizerUseCase)
        factoryOf(::ObserveFastestServersUseCase)
        factoryOf(::ObserveSelectedEndpointUseCase)
        factoryOf(::RefreshFastestServersUseCase)
        factoryOf(::PersistEndpointUseCase)
        factoryOf(::ValidateEndpointUseCase)
        factoryOf(::GetSelectedEndpointUseCase)
        factoryOf(::GetConfigurationUseCase)
        factoryOf(::RescanBlockchainUseCase)
        factoryOf(::GetTransparentAddressUseCase)
        factoryOf(::ValidateContactAddressUseCase)
        factoryOf(::ValidateContactNameUseCase)
        factoryOf(::SaveContactUseCase)
        factoryOf(::UpdateContactUseCase)
        factoryOf(::DeleteContactUseCase)
        factoryOf(::GetContactByAddressUseCase)
        factoryOf(::ObserveContactByAddressUseCase)
        singleOf(::ObserveContactPickedUseCase)
        factoryOf(::CopyToClipboardUseCase)
        factoryOf(::ShareImageUseCase)
        factoryOf(::Zip321BuildUriUseCase)
        factoryOf(::Zip321ParseUriValidationUseCase)
        factoryOf(::IsCoinbaseAvailableUseCase)
        factoryOf(::ObservePersistableWalletUseCase)
        factoryOf(::GetSupportUseCase)
        factoryOf(::SendEmailUseCase)
        factoryOf(::SendSupportEmailUseCase)
        factoryOf(::IsFlexaAvailableUseCase)
        factoryOf(::GetWalletAccountsUseCase)
        factoryOf(::SelectWalletAccountUseCase)
        factoryOf(::ObserveSelectedWalletAccountUseCase)
        factoryOf(::ObserveZashiAccountUseCase)
        factoryOf(::GetZashiAccountUseCase)
        factoryOf(::CreateKeystoneAccountUseCase)
        factoryOf(::DeriveKeystoneAccountUnifiedAddressUseCase)
        factoryOf(::ParseKeystoneUrToZashiAccountsUseCase)
        factoryOf(::GetExchangeRateUseCase)
        factoryOf(::GetSelectedWalletAccountUseCase)
        singleOf(::ObserveClearSendUseCase)
        singleOf(::PrefillSendUseCase)
        factoryOf(::GetTransactionsUseCase)
        factoryOf(::GetCurrentFilteredTransactionsUseCase) onClose ::closeableCallback
        factoryOf(::CreateProposalUseCase)
        factoryOf(::OnZip321ScannedUseCase)
        factoryOf(::OnAddressScannedUseCase)
        factoryOf(::ParseKeystonePCZTUseCase)
        factoryOf(::ParseKeystoneSignInRequestUseCase)
        factoryOf(::CancelProposalFlowUseCase)
        factoryOf(::ObserveProposalUseCase)
        factoryOf(::SharePCZTUseCase)
        factoryOf(::CreateKeystoneProposalPCZTEncoderUseCase)
        factoryOf(::ViewTransactionsAfterSuccessfulProposalUseCase)
        factoryOf(::ViewTransactionDetailAfterSuccessfulProposalUseCase)
        factoryOf(::NavigateToCoinbaseUseCase)
        factoryOf(::ObserveTransactionSubmitStateUseCase)
        factoryOf(::GetProposalUseCase)
        factoryOf(::ConfirmProposalUseCase)
        factoryOf(::GetWalletRestoringStateUseCase)
        factoryOf(::ApplyTransactionFiltersUseCase)
        factoryOf(::ResetTransactionFiltersUseCase)
        factoryOf(::ApplyTransactionFulltextFiltersUseCase)
        factoryOf(::GetTransactionFiltersUseCase)
        factoryOf(::GetTransactionDetailByIdUseCase)
        factoryOf(::SendTransactionAgainUseCase)
        factoryOf(::ObserveAddressBookContactsUseCase)
        factoryOf(::ResetInMemoryDataUseCase)
        factoryOf(::ResetSharedPrefsDataUseCase)
        factoryOf(::NavigateToAddressBookUseCase)
        factoryOf(::GetTransactionMetadataUseCase)
        factoryOf(::FlipTransactionBookmarkUseCase)
        factoryOf(::DeleteTransactionNoteUseCase)
        factoryOf(::CreateOrUpdateTransactionNoteUseCase)
        factoryOf(::MarkTxMemoAsReadUseCase)
        factoryOf(::GetMetadataUseCase)
        factoryOf(::ExportTaxUseCase)
        factoryOf(::NavigateToTaxExportUseCase)
        factoryOf(::CreateFlexaTransactionUseCase)
        factoryOf(::IsRestoreSuccessDialogVisibleUseCase)
        factoryOf(::ValidateSeedUseCase)
        factoryOf(::RestoreWalletUseCase)
        factoryOf(::NavigateToWalletBackupUseCase)
        factoryOf(::GetKeystoneStatusUseCase)
        factoryOf(::GetCoinbaseStatusUseCase)
        factoryOf(::GetFlexaStatusUseCase)
        singleOf(::GetHomeMessageUseCase)
        factoryOf(::OnUserSavedWalletBackupUseCase)
        factoryOf(::RemindWalletBackupLaterUseCase)
        singleOf(::ShieldFundsUseCase)
        singleOf(::NavigateToErrorUseCase)
        factoryOf(::RescanQrUseCase)
        factoryOf(::ShieldFundsMessageUseCase)
        factoryOf(::NavigateToReceiveUseCase)
        factoryOf(::NavigateToRequestShieldedUseCase)
        factoryOf(::NavigateToNearSwapUseCase)
        factoryOf(::CancelSwapUseCase)
        factoryOf(::GetSelectedSwapAssetUseCase)
        factoryOf(::SelectSwapAssetUseCase)
        factoryOf(::GetSwapAssetsUseCase)
        factoryOf(::FilterSwapAssetsUseCase)
        factoryOf(::SetSlippageUseCase)
        factoryOf(::GetSlippageUseCase)
    }
