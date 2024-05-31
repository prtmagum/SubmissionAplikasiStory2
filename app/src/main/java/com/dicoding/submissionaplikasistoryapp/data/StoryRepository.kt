package com.dicoding.submissionaplikasistoryapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.submissionaplikasistoryapp.data.api.ApiService
import com.dicoding.submissionaplikasistoryapp.data.database.StoryDatabase
import com.dicoding.submissionaplikasistoryapp.data.database.StoryRemoteMediator
import com.dicoding.submissionaplikasistoryapp.data.di.Event
import com.dicoding.submissionaplikasistoryapp.data.pref.UserModel
import com.dicoding.submissionaplikasistoryapp.data.pref.UserPreference
import com.dicoding.submissionaplikasistoryapp.response.ErrorResponse
import com.dicoding.submissionaplikasistoryapp.response.ListStoryItem
import com.dicoding.submissionaplikasistoryapp.response.LoginResponse
import com.dicoding.submissionaplikasistoryapp.response.RegisterResponse
import com.dicoding.submissionaplikasistoryapp.response.StoryResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.File

class StoryRepository private constructor(
    private val pref: UserPreference,
    private val dataBase: StoryDatabase,
    private val apiService: ApiService
) {
    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _list = MutableLiveData<StoryResponse>()
    val list: LiveData<StoryResponse> = _list

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText


    fun postRegister(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = apiService.postRegister(name, email, password)

        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _registerResponse.value = response.body()
                    _toastText.value = Event(response.body()?.message.toString())
                } else {
                    _toastText.value = Event(response.message().toString())
                    Log.e(
                        TAG,
                        "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                    )
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _toastText.value = Event(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun postLogin(email: String, password: String) {
        _isLoading.value = true
        val client = apiService.postLogin(email, password)

        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _loginResponse.value = response.body()
                    _toastText.value = Event(response.body()?.message.toString())
                } else {
                    _toastText.value = Event(response.message().toString())
                    Log.e(
                        TAG,
                        "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                    )
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _toastText.value = Event(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(dataBase, pref, apiService),
            pagingSourceFactory = {
                dataBase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getListStoriesWithLocation(token: String) {
        _isLoading.value = true
        val client = apiService.getListStoriesWithLocation("Bearer $token")

        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _list.value = response.body()
                } else {
                    _toastText.value = Event(response.message().toString())
                    Log.e(
                        TAG,
                        "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                    )
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _toastText.value = Event(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun uploadStories(
        token: String,
        imageFile: File,
        description: String,
        lat: Double?,
        lon: Double?
    ) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        val requestLat = lat?.toString()?.toRequestBody()
        val requestLon = lon?.toString()?.toRequestBody()
        try {
            val successResponse = apiService.postStory(
                "Bearer $token",
                multipartBody,
                requestBody,
                requestLat,
                requestLon
            )
            if (successResponse.error) {
                emit(Result.Error(successResponse.message))
            } else {
                emit(Result.Success(successResponse))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage.toString()))
        }
    }

    fun getSession(): LiveData<UserModel> {
        return pref.getSession().asLiveData()
    }

    suspend fun saveSession(session: UserModel) {
        pref.saveSession(session)
    }

    suspend fun logout() {
        pref.logout()
    }

    companion object {
        private const val TAG = "StoryRepository"

        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            preference: StoryDatabase,
            dataBase: UserPreference,
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(dataBase, preference, apiService)
            }.also { instance = it }
    }
}