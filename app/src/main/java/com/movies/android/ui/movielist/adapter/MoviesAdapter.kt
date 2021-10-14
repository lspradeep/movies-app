package com.movies.android.ui.movielist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.movies.android.R
import com.movies.android.data.models.Movie

class MoviesAdapter : PagingDataAdapter<Movie, BaseViewHolder>(MoviesDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return MovieViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_movie, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is MovieViewHolder) {
            holder.bind(getItem(position) as Movie)
        }
    }

//    override fun getItemViewType(position: Int): Int {
//        return when(getItem(position)){
//            is Movie -> R.layout.item_movie
//            else -> throw UnsupportedOperationException()
//        }
//    }

    object MoviesDiffUtil : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.imdbID == newItem.imdbID
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }
}