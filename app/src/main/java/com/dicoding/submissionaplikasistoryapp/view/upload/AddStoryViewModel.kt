package com.dicoding.submissionaplikasistoryapp.view.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.submissionaplikasistoryapp.data.StoryRepository
import com.dicoding.submissionaplikasistoryapp.data.pref.UserModel
import java.io.File

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession()
    }

    fun uploadStories(
        token: String,
        file: File,
        description: String,
        lat: Double? = null,
        lon: Double? = null
    ) =
        repository.uploadStories(token, file, description, lat, lon)
}