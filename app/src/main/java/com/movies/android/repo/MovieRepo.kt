package com.movies.android.repo

import com.movies.android.data.models.*

interface MovieRepo {
    suspend fun getMovies(movieFilter: MovieFilter):MoviesList?

    suspend fun getMovieDetails(id: String): MovieDetail?
}