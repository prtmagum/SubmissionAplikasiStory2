package com.dicoding.submissionaplikasistoryapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.submissionaplikasistoryapp.data.StoryRepository
import com.dicoding.submissionaplikasistoryapp.data.di.Injection
import com.dicoding.submissionaplikasistoryapp.view.login.LoginViewModel
import com.dicoding.submissionaplikasistoryapp.view.main.MainViewModel
import com.dicoding.submissionaplikasistoryapp.view.signup.RegisterViewModel
import com.dicoding.submissionaplikasistoryapp.view.upload.AddStoryViewModel

class ViewModelFactory(private val repository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
        }
    }
}