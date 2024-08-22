package com.secure.app.screens.login

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.secure.app.retrofit.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {
    var state: UiState<String> by mutableStateOf(UiState.None())
        private set


    fun loginWithGoogleLauncher(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
        state = UiState.Loading()
        authRepo.signInWithGoogle(googleSignInLauncher = launcher)
    }

    fun loginWithGoogleResult(activityResult: ActivityResult) {
        state = UiState.Loading()
        authRepo.signInWithGoogleResult(
            task = GoogleSignIn.getSignedInAccountFromIntent(
                activityResult.data
            )
        ).onSuccess { googleSignInAccount ->
            val credential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
            viewModelScope.launch {
                authRepo.signInWithGoogleCredential(credential = credential).onSuccess {
                    uploadUserData(it)
                }.onFailure { error ->
                    error.printStackTrace()
                    state = UiState.Error(error.message?:"")
                }
            }
        }.onFailure {
            it.printStackTrace()
            state = UiState.Error(it.message?:"")

        }
    }

    private fun uploadUserData(item: FirebaseUser) {

        authRepo.uploadUserData(item) { result ->
            result.onSuccess {
                state = UiState.Success(it)
            }.onFailure {
                it.printStackTrace()
                state = UiState.Error(it.message?:"")
            }
        }

    }
}