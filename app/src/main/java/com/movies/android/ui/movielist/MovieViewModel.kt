package com.movies.android.ui.movielist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.movies.android.data.models.Movie
import com.movies.android.data.models.MovieDetail
import com.movies.android.data.models.Response
import com.movies.android.repo.MovieRepository
import com.movies.android.utils.NoConnectivityException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"

@HiltViewModel
class MovieViewModel @Inject constructor(private val movieRepository: MovieRepository) :
    ViewModel() {

    private var currentQueryValue: String? = null

    private var currentSearchResult: Flow<PagingData<Movie>>? = null

    val moviesList: LiveData<Response<List<Movie>?>>
        get() = _moviesList

    private val _moviesList = MutableLiveData<Response<List<Movie>?>>()

    fun getMovies(queryString: String?): Flow<PagingData<Movie>> {
        val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString
        return movieRepository.getMovies(queryString)
            .cachedIn(viewModelScope)

    }

    val movieDetail: LiveData<Response<MovieDetail?>>
        get() = _movieDetail

    private val _movieDetail = MutableLiveData<Response<MovieDetail?>>()

    fun getMoviesDetails(id: String) {
        viewModelScope.launch {
            _movieDetail.value = Response.Loading()
            try {
                val result = movieRepository.getMovieDetails(id)
                if (result != null) {
                    _movieDetail.value = Response.Success(result, null)
                } else {
                    _movieDetail.value = Response.Empty(null)
                }
            } catch (e: Exception) {
                if (e is NoConnectivityException) {
                    _movieDetail.value = Response.OfflineError(null)
                } else {
                    _movieDetail.value = Response.Error(null)
                }
            }
        }
    }
}