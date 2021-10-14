package com.movies.android.ui.movielist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.movies.android.R
import com.movies.android.data.models.ResponseStatus
import com.movies.android.databinding.ActivityMainBinding
import com.movies.android.utils.getQueryTextChangeStateFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.LinearLayoutManager
import com.movies.android.ui.movielist.adapter.MoviesAdapter
import com.movies.android.ui.movielist.adapter.ProgressAdapter
import com.movies.android.utils.PaginationListener
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MovieViewModel

    private var searchJob: Job? = null
    private lateinit var searchView: SearchView

    private val moviesAdapter = MoviesAdapter()
    private lateinit var gridLayoutManager: GridLayoutManager
//    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
        setUpRecycler()
    }

    @FlowPreview
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        setUpSearchView()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.isIconified = true;
        } else {
            super.onBackPressed();
        }
    }

    @FlowPreview
    private fun setUpSearchView() {
        lifecycleScope.launch {
            searchView.getQueryTextChangeStateFlow()
                .debounce(1000)
                .distinctUntilChanged()
                .flowOn(Dispatchers.Default)
                .collect { query ->
                    if (query.isEmpty() || searchView.isIconified) {
                        search("Android")
                    } else {
                        search(query)
                    }

                }
        }
    }

    private fun search(query: String?) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.getMovies(query).collectLatest {
                moviesAdapter.submitData(it)
            }
        }
    }

    private fun setUpRecycler() {
        val header = ProgressAdapter { moviesAdapter.retry() }
        binding.recyclerMovies.adapter = moviesAdapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = ProgressAdapter { moviesAdapter.retry() }
        )

        moviesAdapter.addLoadStateListener { loadState ->

            // show empty list
            val isListEmpty =
                loadState.refresh is LoadState.NotLoading && moviesAdapter.itemCount == 0
            //showEmptyList(isListEmpty)

            // Show a retry header if there was an error refreshing, and items were previously
            // cached OR default to the default prepend state
            header.loadState = loadState.mediator
                ?.refresh
                ?.takeIf { it is LoadState.Error && moviesAdapter.itemCount > 0 }
                ?: loadState.prepend

            // Only show the list if refresh succeeds, either from the the local db or the remote.
            binding.recyclerMovies.isVisible =
                loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            binding.progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails and there are no items.
            binding.retryButton.isVisible =
                loadState.mediator?.refresh is LoadState.Error && moviesAdapter.itemCount == 0
            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    this,
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

//        gridLayoutManager =
//            GridLayoutManager(this@MainActivity, 2, GridLayoutManager.VERTICAL, false)
////        linearLayoutManager = LinearLayoutManager(this)
//        binding.recyclerMovies.apply {
//            adapter = moviesAdapter
//            layoutManager = gridLayoutManager
//            itemAnimator = null
//        }
    }


}