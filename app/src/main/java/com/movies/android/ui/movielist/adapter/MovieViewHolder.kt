package com.movies.android.ui.movielist.adapter

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.movies.android.R
import com.movies.android.data.models.Movie
import com.movies.android.databinding.ItemMovieBinding
import com.movies.android.ui.movielist.adapter.BaseViewHolder

class MovieViewHolder(private val itemMovieBinding: ItemMovieBinding) :
    BaseViewHolder(itemMovieBinding.root) {

    fun bind(movie: Movie) {
        Glide.with(itemMovieBinding.root.context)
            .load(movie.poster)
            .error(R.drawable.img_placeholder_movie)
            .placeholder(R.drawable.img_placeholder_movie)
            .apply(RequestOptions().fitCenter())
            .into(itemMovieBinding.imgMovie)

    }
}