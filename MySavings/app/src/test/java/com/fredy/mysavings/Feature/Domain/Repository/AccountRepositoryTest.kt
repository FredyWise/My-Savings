package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.mysavings.Feature.Data.Database.Dao.AccountDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.AccountDataSource
import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.Feature.Data.Database.Model.UserData
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.TestCase.assertEquals

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AccountRepositoryImplTest {


}
