package com.dicoding.submissionaplikasistoryapp.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class StoryResponse(

    @field:SerializedName("listStory")
    val listStory: List<ListStoryItem> = emptyList(),

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

@Entity(tableName = "story")
data class ListStoryItem(

    @field:SerializedName("photoUrl")
    val photo: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("id")
    @PrimaryKey
    val id: String,

    @field:SerializedName("lon")
    val lon: Double? = null,

    @field:SerializedName("lat")
    val lat: Double? = null
)