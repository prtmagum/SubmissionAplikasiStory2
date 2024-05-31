package com.dicoding.submissionaplikasistoryapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.submissionaplikasistoryapp.data.StoryRepository
import com.dicoding.submissionaplikasistoryapp.response.ListStoryItem
import com.dicoding.submissionaplikasistoryapp.data.di.Event
import com.dicoding.submissionaplikasistoryapp.data.pref.UserModel
import com.dicoding.submissionaplikasistoryapp.response.StoryResponse
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {
    val list: LiveData<StoryResponse> = repository.list
    val isLoading: LiveData<Boolean> = repository.isLoading
    val toastText: LiveData<Event<String>> = repository.toastText
    val getListStories: LiveData<PagingData<ListStoryItem>> =
        repository.getStories().cachedIn(viewModelScope)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getListStoriesWithLocation(token: String) {
        viewModelScope.launch {
            repository.getListStoriesWithLocation(token)
        }
    }

    val story: LiveData<PagingData<ListStoryItem>> = repository.getStories().cachedIn(viewModelScope)
}