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
import com.fredy.mysavings.MainActivity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
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

    private val fakeFirebaseAuth: FirebaseAuth = mockk(relaxed = true)
    private val fakeOneTapClient: SignInClient = mockk(relaxed = true)
    private val fakeAuthResult: AuthResult = mockk()
    private val fakePhoneAuthCredential: PhoneAuthCredential = mockk()

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
        every { fakeFirebaseAuth.signInWithCredential(credential) } returns Tasks.forResult(
            fakeAuthResult
        )

        val resource = googleSignIn(credential).last()

        assertTrue(resource is Resource.Success)
        assertEquals(fakeAuthResult, (resource as Resource.Success).data)
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
        } returns Tasks.forResult(fakeAuthResult)

        val result = loginUser(email, password).last()

        assertTrue(result is Resource.Success)
        assertEquals(fakeAuthResult, (result as Resource.Success).data)
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
        } returns Tasks.forResult(fakeAuthResult)

        val result = registerUser(email, password).last()

        assertTrue(result is Resource.Success)
        assertEquals(fakeAuthResult, (result as Resource.Success).data)
    }

    @Test
    fun `Verify Phone Number`() = runBlocking {
        val context = mockk<Context>()
        val verificationId = "verificationId"
        val code = "123456"

        mockkStatic(PhoneAuthProvider::class)
        every {
            PhoneAuthProvider.getCredential(
                verificationId,
                code
            )
        } returns fakePhoneAuthCredential

        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        every { fakeFirebaseAuth.signInWithCredential(credential) } returns Tasks.forResult(
            fakeAuthResult
        )

        val result = verifyPhoneNumber(context, verificationId, code).last()

        assertTrue(result is Resource.Success)
        assertEquals(fakeAuthResult, (result as Resource.Success).data)
    }


//    @Test
//    fun `Send OTP`() = runBlocking {
//        val context = mockk<MainActivity>()
//        val verificationId = "verificationId"
//        val phoneNumber = "+1234567890"
//        println("bajing")
//        val result = sendOtp(context, phoneNumber).last()
//
//        println("Result: ${result.data}\n${result.message}")
//
//        assertTrue(result is Resource.Success)
//        assertEquals(verificationId, (result as Resource.Success).data)
//    }
//
//    @Test
//    fun `Sign Out`() = runBlocking {
//        val successfulTask = mockk<Task<Void>>()
//        val expectedException = RuntimeException("Sign-out failed!")
//        every { successfulTask.isSuccessful } returns true
//        every { successfulTask.isComplete } returns true
//        every { successfulTask.exception } throws expectedException
//        every { fakeOneTapClient.signOut() } returns successfulTask
//        signOut()
//
//        verify(fakeOneTapClient).signOut()
//        verify(fakeFirebaseAuth).signOut()
//    }
//
//    @Test
//    fun `Update User Information`() = runBlocking {
//        val expectedUsername = "New Username"
//        val profilePictureUri = mockk<Uri>()
//        val emptyOldPassword = ""
//        val emptyNewPassword = ""
//
////        // Mock successful updateProfile and updatePassword calls (if needed)
////        coEvery { fakeFirebaseAuth.currentUser!!.updateProfile(any()) } returns Runs {
////            // Simulate successful update (if needed)
////        }
////        coEvery { fakeFirebaseAuth.currentUser!!.updatePassword(any()) } returns Runs {
////            // Simulate successful update (if password is provided)
////        }
//
//        updateUserInformation(
//            profilePictureUri,
//            expectedUsername,
//            emptyOldPassword,
//            emptyNewPassword
//        ).collect {
//            val successResult = it as Resource.Success
//            assertThat(successResult.data).contains(expectedUsername)
//        }
//
//    }
}
