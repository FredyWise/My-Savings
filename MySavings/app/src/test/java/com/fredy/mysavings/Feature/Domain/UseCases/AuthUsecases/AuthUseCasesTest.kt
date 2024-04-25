package com.fredy.mysavings.Feature.Domain.UseCases.AuthUsecases

import android.content.Context
import android.net.Uri
import com.fredy.mysavings.BaseUseCaseTest
import com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases.GoogleSignIn
import com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases.LoginUser
import com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases.RegisterUser
import com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases.SendOtp
import com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases.SignOut
import com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases.UpdateUserInformation
import com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases.VerifyPhoneNumber
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import kotlin.test.Test
import kotlin.test.assertEquals


class AuthUseCasesTest : BaseUseCaseTest() {

    private lateinit var googleSignIn: GoogleSignIn
    private lateinit var loginUser: LoginUser
    private lateinit var registerUser: RegisterUser
    private lateinit var signOut: SignOut
    private lateinit var verifyPhoneNumber: VerifyPhoneNumber
    private lateinit var sendOtp: SendOtp
    private lateinit var updateUserInformation: UpdateUserInformation

    private val fakeFirebaseAuth: FirebaseAuth = mockk()
    private val fakeOneTapClient: SignInClient = mockk()
    private val fakeAuthResult: Task<AuthResult> = mockk()

    @Before
    fun setUp() {
        googleSignIn = GoogleSignIn(fakeFirebaseAuth)
        loginUser = LoginUser(fakeFirebaseAuth)
        registerUser = RegisterUser(fakeFirebaseAuth)
        signOut = SignOut(fakeFirebaseAuth, fakeOneTapClient)
        verifyPhoneNumber = VerifyPhoneNumber(fakeFirebaseAuth)
        sendOtp = SendOtp(fakeFirebaseAuth)
        updateUserInformation = UpdateUserInformation(fakeFirebaseAuth)
    }

    @Test
    fun `Google Sign In`() = runBlocking {
        val credential = mock(AuthCredential::class.java)
        every { fakeFirebaseAuth.signInWithCredential(credential) } returns fakeAuthResult


        val resource = googleSignIn(credential).last()
        println("Result: ${resource.data}; \n ${resource.message}")
        assertTrue(resource is Resource.Success)
        assertEquals(fakeAuthResult.result, (resource as Resource.Success).data)
    }

    @Test
    fun `Login User`() = runBlocking {
        val email = "test@example.com"
        val password = "password"
        every {
            fakeFirebaseAuth.signInWithEmailAndPassword(
                email,
                password
            )
        } returns fakeAuthResult

        val result = loginUser(email, password).first()

        assertTrue(result is Resource.Success)
        assertEquals(fakeAuthResult.result, (result as Resource.Success).data)
    }

    @Test
    fun `Register User`() = runBlocking {
        val email = "test@example.com"
        val password = "password"
        every {
            fakeFirebaseAuth.createUserWithEmailAndPassword(
                email,
                password
            )
        } returns fakeAuthResult

        val result = registerUser(email, password).first()

        assertTrue(result is Resource.Success)
        assertEquals(fakeAuthResult.result, (result as Resource.Success).data)
    }

    @Test
    fun `Verify Phone Number`() = runBlocking {
        val context = mockk<Context>()
        val verificationId = "verificationId"
        val code = "123456"
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        every { fakeFirebaseAuth.signInWithCredential(credential) } returns fakeAuthResult

        val result = verifyPhoneNumber(context, verificationId, code).first()

        assertTrue(result is Resource.Success)
        assertEquals(fakeAuthResult.result, (result as Resource.Success).data)
    }

    @Test
    fun `Send OTP`() = runBlocking {
        val context = mock(Context::class.java)
        val phoneNumber = "+1234567890"
        val result = sendOtp(context, phoneNumber).first()

        assertTrue(result is Resource.Success)
        assertEquals("verificationId", (result as Resource.Success).data)
    }

    @Test
    fun `Sign Out`() = runBlocking {
        signOut()

        verify(fakeOneTapClient).signOut()
        verify(fakeFirebaseAuth).signOut()
    }

    @Test
    fun `Update User Information`() = runBlocking {
        val profilePicture = mock(Uri::class.java)
        val username = "User A"
        val oldPassword = "oldPassword"
        val password = "newPassword"
        val result = updateUserInformation(profilePicture, username, oldPassword, password).first()

        assertTrue(result is Resource.Success)
        assertEquals("$username updated successfully", (result as Resource.Success).data)
    }
}
