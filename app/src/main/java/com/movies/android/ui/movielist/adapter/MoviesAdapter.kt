package com.movies.android.ui.movielist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.movies.android.R
import com.movies.android.data.models.Movie
import com.movies.android.ui.movielist.BaseViewHolder
import com.movies.android.ui.movielist.MovieViewHolder
import com.movies.android.ui.movielist.ProgressViewHolder

class MoviesAdapter : PagingDataAdapter<Movie, BaseViewHolder>(MoviesDiffUtil) {

    private val movies = mutableListOf<Movie>()
    private var showLoading = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        if (viewType == ViewType.NORMAL.type) {
            return MovieViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_movie, parent, false))
        }
        return ProgressViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_bottom_progress, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is MovieViewHolder) {
            holder.bind(movies[position])
        }
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

    fun showLoading() {
        if (!showLoading && getData().isNotEmpty()) {
            val posToAdd = movies.size
            showLoading = true
            movies.add(posToAdd, Movie(null, null, null, null, null))
            notifyItemInserted(posToAdd)
        }
    }

    fun hideLoading() {
        if (showLoading) {
            val posToRemove = movies.size - 1
            showLoading = false
            movies.removeAt(posToRemove)
            notifyItemRemoved(posToRemove)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (showLoading) ViewType.LOADING.type else ViewType.NORMAL.type
    }

    fun resetAdapter() {
        val sizeToRemove = movies.size
        movies.clear()
        notifyItemRangeRemoved(0, sizeToRemove)
    }

    fun getData(): List<Movie> {
        return movies
    }

    enum class ViewType(val type: Int) {
        LOADING(100),
        NORMAL(101)
    }

    object MoviesDiffUtil : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.imdbID == newItem.imdbID
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }
}