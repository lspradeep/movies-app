package com.movies.android.di

import com.movies.android.BuildConfig
import com.movies.android.api.MoviesService
import com.movies.android.repo.MovieRepo
import com.movies.android.repo.MovieRepoImpl
import com.movies.android.utils.Constants.BASE_URL
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    fun provideMoviesService(okHttpClient: OkHttpClient): MoviesService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MoviesService::class.java)
    }


    @Provides
    fun provideOkHttClient(): OkHttpClient {
        val builder = OkHttpClient().newBuilder()
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
            builder.addInterceptor(loggingInterceptor)
        }
        return builder.build()
    }
}