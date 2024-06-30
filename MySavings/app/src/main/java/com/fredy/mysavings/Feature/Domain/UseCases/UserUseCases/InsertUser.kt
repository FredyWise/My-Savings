package com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Model.UserData
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.flow.firstOrNull

class InsertUser(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserData) {
        val isExist = userRepository.getUser(user.firebaseUserId).firstOrNull()
        Log.i("InsertUser.Data: $isExist")
        if (!isExist.isNotNull()){
            userRepository.upsertUser(user)
        }
    }
}