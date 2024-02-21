package com.example.thenewsapp.ui.fragment

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

    private var isError = false
    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeadlinesBinding.bind(view)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        newsViewModel = (requireActivity() as NewsActivity).newsViewModel
        setupHeadlinesRecycler()

        newsAdapter.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_headlinesFragment2_to_articleFragment,
                bundle
            )
        }
        swipeRefreshLayout.setOnRefreshListener {
            reloadHeadlines()
        }
        observeHeadlines()
    }

    private fun reloadHeadlines() {
        swipeRefreshLayout.isRefreshing = true
        newsViewModel.getHeadlines("us")
    }
    private fun observeHeadlines() {
        newsViewModel.headlines.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    ProgressBar(false)
                    swipeRefreshLayout.isRefreshing = false
                    response.data?.let { newsResponse ->
                        handleHeadlinesResponse(newsResponse)
                    }
                }
                is Resource.Error -> {
                    ProgressBar(true)
                    swipeRefreshLayout.isRefreshing = false
                    response.message?.let { message ->
                        Toast.makeText(activity, "Sorry error: $message", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    ProgressBar(false)
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

    private fun ProgressBar(show: Boolean) {
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
            val firstItemVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstItemVisiblePosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstItemVisiblePosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate =
                isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                newsViewModel.getHeadlines("us")
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
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
}
