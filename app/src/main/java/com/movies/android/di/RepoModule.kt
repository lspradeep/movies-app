package com.movies.android.di

import com.movies.android.repo.MovieRepo
import com.movies.android.repo.MovieRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepoModule {
    @Binds
    abstract fun getMovieRepo(movieRepoImpl: MovieRepoImpl): MovieRepo
}