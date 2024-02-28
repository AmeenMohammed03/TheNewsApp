package com.example.thenewsapp.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.thenewsapp.R
import com.example.thenewsapp.adapters.NewsAdapter
import com.example.thenewsapp.databinding.FragmentHeadlinesBinding
import com.example.thenewsapp.models.NewsResponse
import com.example.thenewsapp.ui.NewsActivity
import com.example.thenewsapp.util.Constants
import com.example.thenewsapp.util.Resource
import com.example.thenewsapp.viewModel.NewsViewModel

class HeadlinesFragment : Fragment(R.layout.fragment_headlines) {

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: FragmentHeadlinesBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var sharedPreferences: SharedPreferences

    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false
    private var selectedCountryCode: String = "us"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeadlinesBinding.bind(view)
        swipeRefreshLayout = binding.swipeRefreshLayout

        sharedPreferences =
            requireContext().getSharedPreferences("NewsAppPrefs", Context.MODE_PRIVATE)

        newsViewModel = (requireActivity() as NewsActivity).newsViewModel
        setupHeadlinesRecycler()
        observeHeadlines()
        initViews()
    }

    private fun initViews() {
        // Set up item click listener
        newsAdapter.setOnItemClickListener { article ->
            try {
                val action = HeadlinesFragmentDirections.actionHeadlinesFragmentToArticleFragment(article)
                findNavController().navigate(action)
                println("Navigation to article fragment successful")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Navigation to article fragment failed: ${e.message}")
                Toast.makeText(requireContext(), "Navigation failed", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up swipe refresh listener
        swipeRefreshLayout.setOnRefreshListener {
            reloadHeadlines()
        }
    }

    private fun reloadHeadlines() {
        if (!isLoading) { // Check if loading is already in progress
            isLoading = true // Set loading flag to true before fetching headlines
            swipeRefreshLayout.isRefreshing = true // Show refresh indicator
            newsViewModel.getHeadlines(selectedCountryCode) // Fetch headlines
            // Do not reset isLoading flag here, it should be reset in observeHeadlines()
            // (requireActivity() as NewsActivity).updateLastUpdatedTime()
        }
    }

    private fun observeHeadlines() {
        newsViewModel.headlines.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    showLoadingIndicator(false)
                    swipeRefreshLayout.isRefreshing = false
                    response.data?.let { newsResponse ->
                        handleHeadlinesResponse(newsResponse)
                    }
                }

                is Resource.Error -> {
                    showLoadingIndicator(true)
                    swipeRefreshLayout.isRefreshing = false
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

    private fun handleHeadlinesResponse(newsResponse: NewsResponse?) {
        newsResponse?.let { response ->
            response.articles?.let { articles ->
                newsAdapter.differ.submitList(articles.toList())
                val totalPages = response.totalResults / Constants.QUERY_PAGE_SIZE + 2
                isLastPage = newsViewModel.headlinesPage == totalPages
                if (isLastPage) {
                    binding.recyclerHeadlines.setPadding(0, 0, 0, 0)
                }
            }
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

    private fun setupHeadlinesRecycler() {
        newsAdapter = NewsAdapter()
        binding.recyclerHeadlines.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(scrollListener)
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val isAtEndOfList = !isLoading && !isLastPage &&
                    (visibleItemCount + firstVisibleItemPosition >= totalItemCount) &&
                    firstVisibleItemPosition >= 0
            if (isAtEndOfList && !isScrolling) {
                newsViewModel.getHeadlines("us")
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
}
