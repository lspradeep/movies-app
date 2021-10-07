package com.movies.android.ui.movielist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movies.android.data.models.Response
import com.movies.android.data.models.MovieDetail
import com.movies.android.data.models.MovieFilter
import com.movies.android.data.models.MoviesList
import com.movies.android.di.AppModule
import com.movies.android.repo.MovieRepo
import com.movies.android.utils.NoConnectivityException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(private val movieRepo: MovieRepo) : ViewModel() {

    val moviesList: LiveData<Response<MoviesList?>>
        get() = _moviesList

    private val _moviesList = MutableLiveData<Response<MoviesList?>>()

    private var _totalResultCount = 0

    private var _resultCount = 0

    val isLastPage: Boolean
        get() = _isLastPage

    private var _isLastPage = false

    val isLoading: Boolean
        get() = _isLoading

    private var _isLoading = true

    val currentPage: Int
        get() = _currentPage

    private var _currentPage = 0

    var moviesFilter = MovieFilter("App", 1)

    init {
        getMovies(moviesFilter)
    }

    fun getMovies(filter: MovieFilter) {
        _isLastPage = false
        _resultCount = 0
        _totalResultCount = 0
        _currentPage = filter.page

        viewModelScope.launch {
            _moviesList.value = Response.Loading()
            _isLoading = true
            try {
                val result = movieRepo.getMovies(filter)
                _totalResultCount = result?.totalResults?.toInt() ?: 0
                if (result?.search?.isNullOrEmpty() == false && result.response == "True") {
                    _resultCount = _resultCount.plus(result.search.count())
                    _moviesList.value = Response.Success(result, null)
                } else {
                    _isLastPage = true
                    _moviesList.value = Response.Empty(null)
                }
                _isLoading = false
            } catch (e: Exception) {
                _isLoading = false
                if (e is NoConnectivityException) {
                    _moviesList.value = Response.OfflineError(null)
                } else {
                    _moviesList.value = Response.Error(null)
                }
            }
        }
    }

    fun getNextPage() {
        _isLastPage = false
        _currentPage = moviesFilter.page + 1
        moviesFilter = moviesFilter.copy(page = _currentPage)
        _isLoading = true
        viewModelScope.launch {
            _moviesList.value = Response.Loading()
            try {
                val result = movieRepo.getMovies(moviesFilter)
                if (result?.search?.isNullOrEmpty() == false && result.response == "True") {
                    _resultCount = _resultCount.plus(result.search.count())
                    _moviesList.value = Response.Success(result, null)
                } else {
                    _isLastPage = true
                    _moviesList.value = Response.Empty(null)
                }
                _isLoading = false
            } catch (e: Exception) {
                if (e is NoConnectivityException) {
                    _moviesList.value = Response.OfflineError(null)
                } else {
                    _moviesList.value = Response.Error(null)
                }
                _isLoading = false
            }
        }
    }


    val movieDetail: LiveData<Response<MovieDetail?>>
        get() = _movieDetail

    private val _movieDetail = MutableLiveData<Response<MovieDetail?>>()

    fun getMoviesDetails(id: String) {
        viewModelScope.launch {
            _movieDetail.value = Response.Loading()
            try {
                val result = movieRepo.getMovieDetails(id)
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