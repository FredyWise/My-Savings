package com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases

import com.fredy.domain.useCases.WalletUseCases.GetWallets
import com.fredy.domain.useCases.WalletUseCases.GetWalletsTotalBalance

data class WalletUseCases(
    val upsertWallet: UpsertWallet,
    val deleteWallet: DeleteWallet,
    val getWallet: GetWallet,
    val getWalletsOrderedByName: GetWallets,
    val getWalletsTotalBalance: GetWalletsTotalBalance,
    val getWalletsCurrencies: GetWalletsCurrencies,
)

