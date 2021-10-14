package com.movies.android.ui.movielist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.movies.android.databinding.ItemBottomProgressBinding

class ProgressAdapter(private val retry: () -> Unit) : LoadStateAdapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): BaseViewHolder {
        return ProgressViewHolder(
            ItemBottomProgressBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            retry
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, loadState: LoadState) {
        if (holder is ProgressViewHolder) {
            holder.bind(loadState)
        }
    }
}