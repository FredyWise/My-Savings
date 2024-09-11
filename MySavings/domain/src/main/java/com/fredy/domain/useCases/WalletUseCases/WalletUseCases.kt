package com.fredy.domain.useCases.WalletUseCases

data class WalletUseCases(
    val upsertWallet: UpsertWallet,
    val deleteWallet: DeleteWallet,
    val getWallet: GetWallet,
    val getWalletsOrderedByName: GetWallets,
    val getWalletsTotalBalance: GetWalletsTotalBalance,
    val getWalletsCurrencies: GetWalletsCurrencies,
)

