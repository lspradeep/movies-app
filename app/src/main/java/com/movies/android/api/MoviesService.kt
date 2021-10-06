package com.movies.android.api

import com.movies.android.data.models.MovieDetail
import com.movies.android.data.models.MoviesList
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesService {
    //    http://www.omdbapi.com/?apikey={API_KEY}&s={SEARCH_STRING}&page={PAGE_NO}
    @GET("/")
    suspend fun getMovies(
        @Query("apikey") apikey: String,
        @Query("s") keyword: String?,
        @Query("page") pageNo: Int,
    ): MoviesList?

    @GET("/")
    suspend fun getMovieDetail(
        @Query("apikey") apikey: String,
        @Query("i") imdbId: String,
    ): MovieDetail?
}