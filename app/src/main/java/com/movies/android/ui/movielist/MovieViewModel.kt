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


    val movieDetail: LiveData<Response<MovieDetail?>>
        get() = _movieDetail

    private val _movieDetail = MutableLiveData<Response<MovieDetail?>>()

    var moviesFilter = MovieFilter("app", 1)

    fun getMovies(filter: MovieFilter) {
        viewModelScope.launch {
            _moviesList.value = Response.Loading()
            try {
                val result = movieRepo.getMovies(filter)
                if (result?.search?.isNullOrEmpty() == false) {
                    _moviesList.value = Response.Success(result, null)
                } else {
                    _moviesList.value = Response.Empty(null)
                }
            } catch (e: Exception) {
                if (e is NoConnectivityException) {
                    _moviesList.value = Response.OfflineError(null)
                } else {
                    _moviesList.value = Response.Error(null)
                }
            }
        }
    }

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