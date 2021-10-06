package com.movies.android.data.models

sealed class Response<T>(
    val data: T?,
    val message: String?,
    val responseStatus: ResponseStatus,
) {
    class Success<T>(data: T, message: String?) :
        Response<T>(data, message, ResponseStatus.SUCCESS)

    class Error<T>(message: String?, data: T? = null) :
        Response<T>(data, message, ResponseStatus.ERROR)

    class OfflineError<T>(message: String?) :
        Response<T>(null, message, ResponseStatus.OFFLINE_ERROR)

    class Empty<T>(message: String?) : Response<T>(null, message, ResponseStatus.EMPTY)

    class Loading<T> : Response<T>(null, null, ResponseStatus.LOADING)

}