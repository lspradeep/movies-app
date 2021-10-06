package com.movies.android.ui.movielist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.movies.android.R
import com.movies.android.data.models.Movie
import com.movies.android.data.models.ResponseStatus
import com.movies.android.databinding.ActivityMainBinding
import com.movies.android.utils.getQueryTextChangeStateFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MovieViewModel
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var searchView: SearchView
    private lateinit var gridLayoutManager: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
        setObservers()
        setUpRecycler()
        viewModel.getMovies(viewModel.moviesFilter)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        setUpSearchView()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_sort) {
            gridLayoutManager.reverseLayout = !gridLayoutManager.reverseLayout
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
                        searchView.setQuery("", false)
                        return@filter false
                    } else {
                        return@filter true
                    }
                }
                .distinctUntilChanged()
                .flowOn(Dispatchers.Default)
                .collect { result ->
                    viewModel.getMovies(viewModel.moviesFilter.copy(keyword = result))
                }
        }
    }

    private fun setUpRecycler() {
        moviesAdapter = MoviesAdapter()
        gridLayoutManager =
            GridLayoutManager(this@MainActivity, 3, GridLayoutManager.VERTICAL, false)
        binding.recyclerMovies.apply {
            adapter = moviesAdapter
            layoutManager = gridLayoutManager

        }
    }

    private fun setObservers() {
        viewModel.moviesList.observe(this) { response ->
            when (response.responseStatus) {
                ResponseStatus.SUCCESS -> {
                    if (!searchView.isIconified) {
                        moviesAdapter.resetAdapter()
                    }
                    moviesAdapter.addAll(response.data?.search.orEmpty())
                }
                ResponseStatus.ERROR -> {

                }
                ResponseStatus.OFFLINE_ERROR -> {

                }
                ResponseStatus.EMPTY -> {

                }
                ResponseStatus.LOADING -> {

                }
            }
        }
    }

}