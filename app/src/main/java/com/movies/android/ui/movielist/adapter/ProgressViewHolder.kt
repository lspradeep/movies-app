package com.movies.android.ui.movielist.adapter

import androidx.core.view.isVisible
import androidx.paging.LoadState
import com.movies.android.databinding.ItemBottomProgressBinding
import com.movies.android.ui.movielist.adapter.BaseViewHolder

class ProgressViewHolder(private val itemBinding: ItemBottomProgressBinding, retry: () -> Unit) :
    BaseViewHolder(itemBinding.root) {

    init {
        itemBinding.retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        itemBinding.progressBar.isVisible = loadState is LoadState.Loading
        itemBinding.retryButton.isVisible = loadState is LoadState.Error
    }
}