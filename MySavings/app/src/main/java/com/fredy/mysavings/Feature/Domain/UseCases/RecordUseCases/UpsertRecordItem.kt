package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData

class UpsertRecordItem(
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(record: Record): String {
        val currentUser = userRepository.getCurrentUser()!!
        val currentUserId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
        return recordRepository.upsertRecordItem(
            record.copy(
                userIdFk = currentUserId,
                categoryIdFk = if (record.categoryIdFk == DefaultData.transferCategory.categoryId) record.categoryIdFk + currentUserId else record.categoryIdFk
            )
        )
    }
}