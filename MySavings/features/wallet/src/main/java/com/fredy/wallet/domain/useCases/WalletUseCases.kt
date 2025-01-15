package com.fredy.wallet.domain.useCases

data class WalletUseCases(
    val upsertWallet: UpsertWallet,
    val deleteWallet: DeleteWallet,
    val getWallet: GetWallet,
    val getWalletsOrderedByName: GetWallets,
    val getWalletsTotalBalance: GetWalletsTotalBalance,
    val updateRecordItemWithDeletedWallet: UpdateRecordItemWithDeletedWallet,
    val getUserWalletRecordsOrderedByDateTime: GetUserWalletRecordsOrderedByDateTime,
    val getUserTotalAmountByType: GetUserTotalAmountByType,
)

