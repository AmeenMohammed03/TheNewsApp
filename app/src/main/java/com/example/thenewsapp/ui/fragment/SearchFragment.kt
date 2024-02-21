//package com.example.thenewsapp.ui.fragment
//
//import android.os.Bundle
//import android.view.View
//import android.widget.AbsListView
//import android.widget.Button
//import android.widget.TextView
//import android.widget.Toast
//import androidx.cardview.widget.CardView
//import androidx.core.widget.addTextChangedListener
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.thenewsapp.R
//import com.example.thenewsapp.adapters.com.example.thenewsapp.adapters.NewsAdapter
//import com.example.thenewsapp.databinding.FragmentSearchBinding
//import com.example.thenewsapp.models.NewsResponse
//import com.example.thenewsapp.ui.NewsActivity
//import com.example.thenewsapp.util.Constants
//import com.example.thenewsapp.util.Resource
//import com.example.thenewsapp.viewModel.NewsViewModel
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.MainScope
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//
//class SearchFragment : Fragment(R.layout.fragment_search) {
//
//    private lateinit var newsViewModel: NewsViewModel
//    private lateinit var newsAdapter: com.example.thenewsapp.adapters.NewsAdapter
//    private lateinit var retryButton: Button
//    private lateinit var errorText: TextView
//    private lateinit var itemSearchError: CardView
//    private lateinit var binding: FragmentSearchBinding
//
//    private var isError = false
//    private var isLoading = false
//    private var isLastPage = false
//    private var isScrolling = false
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding = FragmentSearchBinding.bind(view)
//
//        itemSearchError = view.findViewById(R.id.itemSearchError)
//        retryButton = view.findViewById(R.id.retryButton)
//        errorText = view.findViewById(R.id.errorText)
//
//        newsViewModel = (requireActivity() as NewsActivity).newsViewModel
//        setupSearchRecycler()
//
//        newsAdapter.setOnItemClickListener { article ->
//            val bundle = Bundle().apply {
//                putSerializable("article", article)
//            }
//            findNavController().navigate(
//                R.id.action_searchFragment2_to_articleFragment,
//                bundle
//            )
//        }
//
//        var job: Job? = null
//        binding.searchEdit.addTextChangedListener { editable ->
//            job?.cancel()
//            job = MainScope().launch {
//                delay(Constants.SEARCH_NEWS_TIME_DELAY)
//                editable?.let {
//                    if (editable.toString().isNotEmpty()) {
//                        newsViewModel.searchNews(editable.toString())
//                    }
//                }
//            }
//        }
//
//        observeSearchNews()
//
//        retryButton.setOnClickListener {
//            if (binding.searchEdit.text.toString().isNotEmpty()) {
//                newsViewModel.searchNews(binding.searchEdit.text.toString())
//            } else {
//                hideErrorMessage()
//            }
//        }
//    }
//
//    private fun observeSearchNews() {
//        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
//            when (response) {
//                is Resource.Success -> {
//                    hideProgressBar()
//                    hideErrorMessage()
//                    response.data?.let { newsResponse ->
//                        handleSearchNewsResponse(newsResponse)
//                    }
//                }
//                is Resource.Error -> {
//                    hideProgressBar()
//                    response.message?.let { message ->
//                        Toast.makeText(activity, "Sorry error: $message", Toast.LENGTH_SHORT).show()
//                        showErrorMessage(message)
//                    }
//                }
//                is Resource.Loading -> {
//                    showProgressBar()
//                }
//            }
//        })
//    }
//
//    private fun handleSearchNewsResponse(newsResponse: NewsResponse) {
//        newsAdapter.differ.submitList(newsResponse.articles.toList())
//        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
//        isLastPage = newsViewModel.searchNewsPage == totalPages
//        if (isLastPage) {
//            binding.recyclerSearch.setPadding(0, 0, 0, 0)
//        }
//    }
//
//    private fun hideProgressBar() {
//        binding.paginationProgressBar.visibility = View.INVISIBLE
//        isLoading = false
//    }
//
//    private fun showProgressBar() {
//        binding.paginationProgressBar.visibility = View.VISIBLE
//        isLoading = true
//    }
//
//    private fun hideErrorMessage() {
//        itemSearchError.visibility = View.INVISIBLE
//        isError = false
//    }
//
//    private fun showErrorMessage(message: String) {
//        itemSearchError.visibility = View.VISIBLE
//        errorText.text = message
//        isError = true
//    }
//
//    private val scrollListener = object : RecyclerView.OnScrollListener() {
//        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//            super.onScrolled(recyclerView, dx, dy)
//            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//            val firstItemVisiblePosition = layoutManager.findFirstVisibleItemPosition()
//            val visibleItemCount = layoutManager.childCount
//            val totalItemCount = layoutManager.itemCount
//            val isNoErrors = !isError
//            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
//            val isAtLastItem = firstItemVisiblePosition + visibleItemCount >= totalItemCount
//            val isNotAtBeginning = firstItemVisiblePosition >= 0
//            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
//            val shouldPaginate =
//                isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
//            if (shouldPaginate) {
//                newsViewModel.searchNews(binding.searchEdit.text.toString())
//                isScrolling = false
//            }
//        }
//
//        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//            super.onScrollStateChanged(recyclerView, newState)
//            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//                isScrolling = true
//            }
//        }
//    }
//
//    private fun setupSearchRecycler() {
//        newsAdapter = com.example.thenewsapp.adapters.NewsAdapter()
//        binding.recyclerSearch.apply {
//            adapter = newsAdapter
//            layoutManager = LinearLayoutManager(activity)
//            addOnScrollListener(scrollListener)
//        }
//    }
//}
