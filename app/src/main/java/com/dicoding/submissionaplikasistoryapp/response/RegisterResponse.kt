package com.dicoding.submissionaplikasistoryapp.response

import com.google.gson.annotations.SerializedName

class RegisterResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)