package com.movies.android.data.models

import com.google.gson.annotations.SerializedName

data class MoviesList(
    @SerializedName("Search")
    val movies: List<Movie>?,

    @SerializedName("totalResults")
    val totalResults: String,

    @SerializedName("Response")
    val response: String?,
)