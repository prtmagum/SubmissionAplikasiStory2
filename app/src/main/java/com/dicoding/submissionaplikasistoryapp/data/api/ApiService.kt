package com.dicoding.submissionaplikasistoryapp.data.api

import com.dicoding.submissionaplikasistoryapp.response.AddStoryResponse
import com.dicoding.submissionaplikasistoryapp.response.LoginResponse
import com.dicoding.submissionaplikasistoryapp.response.RegisterResponse
import com.dicoding.submissionaplikasistoryapp.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun postRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun postLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getListStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): StoryResponse

    @GET("stories")
    fun getListStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") loc: Int = 1
    ): Call<StoryResponse>

    @Multipart
    @POST("stories")
    suspend fun postStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): AddStoryResponse
}