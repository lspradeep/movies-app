package com.movies.android.ui.movielist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.movies.android.utils.PaginationListener


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MovieViewModel
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var searchView: SearchView
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
        setObservers()
        setUpRecycler()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        setUpSearchView()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_sort) {

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.isIconified = true;
        } else {
            super.onBackPressed();
        }
    }

    private fun setUpSearchView() {
        lifecycleScope.launch {
            searchView.getQueryTextChangeStateFlow()
                .debounce(1000)
                .filter { query ->
                    if (query.isEmpty()) {
                        if (!searchView.isIconified) {
                            searchView.setQuery("", false)
                            viewModel.moviesFilter =
                                viewModel.moviesFilter.copy(keyword = "App", page = 1)
                            makeApiCall()
                        }
                        return@filter false
                    } else {
                        return@filter true
                    }
                }
                .distinctUntilChanged()
                .flowOn(Dispatchers.Default)
                .collect { result ->
                    viewModel.moviesFilter = viewModel.moviesFilter.copy(keyword = result, page = 1)
                    makeApiCall()
                }
        }
    }

    private fun makeApiCall() {
        viewModel.getMovies(viewModel.moviesFilter)
    }

    private fun setUpRecycler() {
        moviesAdapter = MoviesAdapter()
        gridLayoutManager =
            GridLayoutManager(this@MainActivity, 2, GridLayoutManager.VERTICAL, false)
        linearLayoutManager = LinearLayoutManager(this)
        binding.recyclerMovies.apply {
            adapter = moviesAdapter
            layoutManager = linearLayoutManager
            itemAnimator = null
        }
        binding.recyclerMovies.addOnScrollListener(object : PaginationListener(linearLayoutManager) {
            override fun loadMoreItems() {
                viewModel.getNextPage()
            }

            override val isLastPage: Boolean
                get() = viewModel.isLastPage

            override val isLoading: Boolean
                get() = viewModel.isLoading

        })
//        binding.recyclerMovies.addOnScrollListener(recyclerViewOnScrollListener)
    }

    private val recyclerViewOnScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount: Int = gridLayoutManager.childCount
                val totalItemCount: Int = gridLayoutManager.itemCount
                val firstVisibleItemPosition: Int = gridLayoutManager.findFirstVisibleItemPosition()
                if (!viewModel.isLoading && !viewModel.isLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= 10) {
                        viewModel.getNextPage()
                    }
                }
            }
        }

    private fun setObservers() {
        viewModel.moviesList.observe(this) { response ->
            if (viewModel.currentPage == 1) {
                moviesAdapter.resetAdapter()
            }
            when (response.responseStatus) {
                ResponseStatus.SUCCESS -> {
                    moviesAdapter.hideLoading()
                    moviesAdapter.addAll(response.data?.search.orEmpty())
                }
                ResponseStatus.ERROR -> {
                    moviesAdapter.hideLoading()
                }
                ResponseStatus.OFFLINE_ERROR -> {
                    moviesAdapter.hideLoading()
                }
                ResponseStatus.EMPTY -> {
                    moviesAdapter.hideLoading()
                }
                ResponseStatus.LOADING -> {
                    moviesAdapter.showLoading()
                }
            }
        }
    }

}