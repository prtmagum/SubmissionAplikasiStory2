package com.dicoding.submissionaplikasistoryapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.submissionaplikasistoryapp.data.StoryRepository
import com.dicoding.submissionaplikasistoryapp.data.di.Event
import com.dicoding.submissionaplikasistoryapp.data.pref.UserModel
import com.dicoding.submissionaplikasistoryapp.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {
    val isLoading: LiveData<Boolean> = repository.isLoading
    val loginResponse: LiveData<LoginResponse> = repository.loginResponse
    val toastText: LiveData<Event<String>> = repository.toastText

    fun postLogin(email: String, password: String) {
        viewModelScope.launch {
            repository.postLogin(email, password)
        }
    }

    fun saveSession(session: UserModel) {
        viewModelScope.launch {
            repository.saveSession(session)
        }
    }

}