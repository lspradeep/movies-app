package com.movies.android.ui.movielist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.movies.android.R
import com.movies.android.data.models.Movie

class MoviesAdapter : RecyclerView.Adapter<MovieViewHolder>() {

    val movies = mutableListOf<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_movie, parent, false))
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    fun addAll(newList: List<Movie>) {
        newList.forEach { newItem ->
            movies.add(movies.size, newItem)
            notifyItemInserted(movies.size)
        }
    }

    fun resetAdapter() {
        val sizeToRemove = movies.size
        movies.clear()
        notifyItemRangeRemoved(0, sizeToRemove)
    }

    fun getData(): List<Movie> {
        return movies
    }
}