package com.dicoding.submissionaplikasistoryapp.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.submissionaplikasistoryapp.data.StoryRepository
import com.dicoding.submissionaplikasistoryapp.data.di.Event
import com.dicoding.submissionaplikasistoryapp.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: StoryRepository) : ViewModel() {
    val registerResponse: LiveData<RegisterResponse> = repository.registerResponse
    val toastText: LiveData<Event<String>> = repository.toastText

    fun postRegister(name: String, email: String, password: String) {
        viewModelScope.launch {
            repository.postRegister(name, email, password)
        }
    }
}