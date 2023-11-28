package com.fredy.mysavings.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Database.Entity.UserData
import com.fredy.mysavings.Repository.AuthRepository
import com.fredy.mysavings.Util.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repository: AuthRepository,
): ViewModel() {
    private val _currentUser = MutableStateFlow(
        UserData()
    )
    val currentUser = _currentUser.asStateFlow()
    init {
        viewModelScope.launch {
            repository.getCurrentUser()?.let { currentUser ->
                _currentUser.update { currentUser }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
        }
    }

}