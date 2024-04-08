package com.fredy.mysavings.Feature.Domain.Repository

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response.CurrencyInfoItem
import com.fredy.mysavings.Feature.Data.Database.Model.Currency
import com.fredy.mysavings.Feature.Data.Database.Model.RatesCache
import com.fredy.mysavings.Feature.Data.Database.Model.UserData
import com.fredy.mysavings.Util.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeAuthRepository : AuthRepository {

    val users = mutableListOf<UserData>()

    override suspend fun getCurrentUser(): UserData? {
        return users.firstOrNull()
    }

    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flowOf()
    }

    override fun registerUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flowOf()
    }

    override fun updateUserInformation(profilePicture: Uri?, username: String, oldPassword: String, password: String): Flow<Resource<String>> {
        return flowOf()
    }

    override fun googleSignIn(credential: AuthCredential): Flow<Resource<AuthResult>> {
        return flowOf()
    }

    override fun sendOtp(context: Context, phoneNumber: String): Flow<Resource<String>> {
        return flowOf()
    }

    override fun verifyPhoneNumber(context: Context, verificationId: String, code: String): Flow<Resource<AuthResult>> {
        return flowOf()
    }

    override suspend fun signOut() {
    }
}
