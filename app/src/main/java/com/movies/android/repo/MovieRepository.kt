package com.movies.android.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.movies.android.api.MoviesService
import com.movies.android.data.datasource.MoviePagingSource
import com.movies.android.data.models.Movie
import com.movies.android.data.models.MovieDetail
import com.movies.android.utils.Constants.API_KEY
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieRepository @Inject constructor(private val service: MoviesService) {

    fun getMovies(keyword: String?): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MoviePagingSource(service, keyword) }
        ).flow
    }

    suspend fun getMovieDetails(id: String): MovieDetail? {
        return service.getMovieDetail(API_KEY, id)
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}