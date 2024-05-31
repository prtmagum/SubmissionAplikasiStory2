package com.dicoding.submissionaplikasistoryapp.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.submissionaplikasistoryapp.data.pref.UserPreference
import com.dicoding.submissionaplikasistoryapp.data.StoryRepository
import com.dicoding.submissionaplikasistoryapp.data.api.ApiConfig
import com.dicoding.submissionaplikasistoryapp.data.database.StoryDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("token")

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val dataBase = StoryDatabase.getDatabase(context)
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryRepository.getInstance(dataBase, pref, apiService)
    }
}