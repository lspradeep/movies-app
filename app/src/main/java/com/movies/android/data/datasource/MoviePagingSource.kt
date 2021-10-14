package com.movies.android.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.movies.android.api.MoviesService
import com.movies.android.data.models.Movie
import com.movies.android.repo.MovieRepository.Companion.NETWORK_PAGE_SIZE
import com.movies.android.utils.Constants.API_KEY
import java.io.IOException

const val MOVIE_STARTING_PAGE_INDEX = 1

class MoviePagingSource(
    private val moviesService: MoviesService,
    private val keyword: String?,
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val position = params.key ?: MOVIE_STARTING_PAGE_INDEX
        return try {
            val response = moviesService.getMovies(API_KEY, keyword, position)
            val movies = response?.movies.orEmpty()
            val nextKey = if (movies.isEmpty()) {
                null
            } else {
                position + 1
            }

            LoadResult.Page(
                data = movies,
                prevKey = if (position == MOVIE_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            if (e is IOException) {
                LoadResult.Error(e)
            } else {
                LoadResult.Error(e)
            }
        }
    }

    // The refresh key is used for subsequent refresh calls to PagingSource.load after the initial load
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}