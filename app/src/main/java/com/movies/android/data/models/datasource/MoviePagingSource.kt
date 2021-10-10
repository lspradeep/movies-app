package com.movies.android.data.models.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.movies.android.api.MoviesService
import com.movies.android.data.models.Movie
import com.movies.android.utils.Constants.API_KEY
import com.movies.android.utils.Constants.FIRST_PAGE
import java.io.IOException

class MoviePagingSource(
    private val moviesService: MoviesService,
    private val keyword: String?,
) : PagingSource<Int, Movie>() {
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: FIRST_PAGE
        return try {
            val result = moviesService.getMovies(API_KEY, keyword, page)
            return if (!result?.search.isNullOrEmpty() && result?.response?.equals("True") == true) {
                LoadResult.Page(result.search.orEmpty(),
                    if (page == FIRST_PAGE) null else page - 1,
                    page + 1)
            } else {
                LoadResult.Page(emptyList(),
                    if (page == FIRST_PAGE) null else page - 1,
                    null)
            }
        } catch (e: Exception) {
            if (e is IOException) {
                LoadResult.Error(e)
            } else {
                LoadResult.Error(e)
            }
        }
    }
}