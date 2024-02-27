package com.example.thenewsapp.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thenewsapp.R
import com.example.thenewsapp.adapters.NewsAdapter
import com.example.thenewsapp.databinding.FragmentSearchBinding
import com.example.thenewsapp.models.NewsResponse
import com.example.thenewsapp.ui.NewsActivity
import com.example.thenewsapp.util.Constants
import com.example.thenewsapp.util.Resource
import com.example.thenewsapp.viewModel.NewsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: FragmentSearchBinding

    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false
    private var searchJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)

        // Initialize ViewModel
        newsViewModel = (requireActivity() as NewsActivity).newsViewModel

        // Initialize RecyclerView
        setupSearchRecycler()

        // Initialize other views and listeners
        initViews()

        // Observe search news results
        observeSearchNews()
    }

    private fun initViews() {
        // Set up item click listener
        newsAdapter.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(R.id.action_searchFragment2_to_articleFragment, bundle)
        }

        // Initialize search edit text listener
        initSearchListener()

        //Initialize swipe-to-refresh listener
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
    }

    private fun initSearchListener() {
        binding.searchEdit.addTextChangedListener { editable ->
            // Cancel previous search job to avoid unnecessary network requests
            searchJob?.cancel()
            searchJob = MainScope().launch {
                // Add delay before triggering search to avoid frequent network requests
                delay(Constants.SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        isLoading = true
                        isLastPage = false
                        // Perform search when text is not empty
                        newsViewModel.searchNews(editable.toString())
                    } else {
                        newsAdapter.differ.submitList(emptyList())
                        isLoading = false
                        isLastPage = true
                    }
                }
            }
        }
    }

    private fun refreshData() {
        // If isLoading is true, it means a search operation is already in progress, so just return
        if (isLoading) {
            return
        }
        // Trigger a new search with the current search query
        newsViewModel.searchNews(binding.searchEdit.text.toString())
    }

    private fun observeSearchNews() {
        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    showLoadingIndicator(false)
                    response.data?.let { newsResponse ->
                        handleSearchNewsResponse(newsResponse)
                    }
                }

                is Resource.Error -> {
                    showLoadingIndicator(false)
                    response.message?.let { message ->
                        Toast.makeText(activity, "Error: $message", Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading -> {
                    showLoadingIndicator(true)
                }
            }
        })
    }

    private fun handleSearchNewsResponse(newsResponse: NewsResponse) {
        newsAdapter.differ.submitList(newsResponse.articles.toList())
        // Calculate total pages for pagination
        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
        isLastPage = newsViewModel.searchNewsPage == totalPages
        // Adjust padding if it's the last page
        if (isLastPage) {
            binding.recyclerSearch.setPadding(0, 0, 0, 0)
        }
        if (binding.swipeRefreshLayout.isRefreshing) {
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showLoadingIndicator(show: Boolean) {
        if (show) {
            binding.paginationProgressBar.visibility = View.VISIBLE
            isLoading = true
        } else {
            binding.paginationProgressBar.visibility = View.INVISIBLE
            isLoading = false
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            // Check if it's not loading, not the last page, and reached the end of the list
            val isAtEndOfList =
                !isLoading && !isLastPage &&
                        (visibleItemCount + firstVisibleItemPosition >= totalItemCount) &&
                        firstVisibleItemPosition >= 0
            if (isAtEndOfList && !isScrolling) {
                // Trigger pagination
                newsViewModel.searchNews(binding.searchEdit.text.toString())
                isScrolling = true
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setupSearchRecycler() {
        newsAdapter = NewsAdapter()
        binding.recyclerSearch.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(scrollListener)
        }
    }
}
