package com.movies.android.repo

import com.movies.android.api.MoviesService
import com.movies.android.data.models.*
import com.movies.android.di.AppModule
import com.movies.android.utils.Constants.API_KEY
import javax.inject.Inject

class MovieRepoImpl @Inject constructor(private val moviesService: MoviesService) : MovieRepo {

    override suspend fun getMovies(movieFilter: MovieFilter): MoviesList? {
        return moviesService.getMovies(API_KEY, movieFilter.keyword, movieFilter.page)
    }

    override suspend fun getMovieDetails(id: String): MovieDetail? {
        return moviesService.getMovieDetail(API_KEY, id)
    }
}