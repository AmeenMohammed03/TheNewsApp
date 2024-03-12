package com.example.thenewsapp.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.thenewsapp.R
import com.example.thenewsapp.adapters.NewsAdapter
import com.example.thenewsapp.db.NewsData
import com.example.thenewsapp.db.NewsDataBase
import com.example.thenewsapp.manager.NewsManager
import com.example.thenewsapp.models.Article
import com.example.thenewsapp.repository.NewsRepository
import com.example.thenewsapp.ui.DialogUtil
import com.example.thenewsapp.ui.contracts.NewsActivityInterface
import com.example.thenewsapp.ui.contracts.NewsFragmentInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LatestNewsFragment: Fragment(R.layout.fragment_latest_news), NewsAdapter.OnItemClickListener ,
    NewsFragmentInterface {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: NewsAdapter
    private lateinit var newsRepository: NewsRepository
    private lateinit var dialog: AlertDialog
    private lateinit var manager: NewsManager
    private lateinit var activityCallBack: NewsActivityInterface
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    companion object
    {
        lateinit var dataBase: NewsDataBase
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_latest_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBase = Room.databaseBuilder(requireContext(), NewsDataBase::class.java, "news_data").build()
        initUi()
    }

    override fun initUi() {
        recyclerView = requireView().findViewById(R.id.recyclerHeadlines) as RecyclerView
        swipeRefreshLayout = requireView().findViewById(R.id.swipeRefreshLayout)
        progressBar = requireView().findViewById(R.id.paginationProgressBar)
        adapter = NewsAdapter(this)
        manager = NewsManager(this)
        activityCallBack = requireActivity() as NewsActivityInterface
        newsRepository = NewsRepository()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener {
            val countryCode = activityCallBack.getSelectedCountryCode()
            manager.getLatestNews(countryCode)
        }
        val countryCode = activityCallBack.getSelectedCountryCode()
        manager.getLatestNews(countryCode)
    }

    override fun getLatestNews(countryCode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = newsRepository.getHeadlines(countryCode)
                manager.handleLatestNewsResponse(response)
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error in Fetching Headlines: ${e.message}")
            }
        }
    }

    override fun saveDataInRoom(data: NewsData) {
        CoroutineScope(Dispatchers.IO).launch {
            dataBase.newsDao().deleteNewsData()
            dataBase.newsDao().insertNewsData(data)
        }
    }

    override fun submitListToAdapter(articles: List<Article>) {
        CoroutineScope(Dispatchers.Main).launch {
            adapter.differ.submitList(articles)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun showNoNetworkDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                dialog = DialogUtil.showDialog(
                    requireContext(),
                    "No Network",
                    "Please check your internet connection and try again"
                )
                dialog.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun showInternalErrorDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                dialog = DialogUtil.showDialog(
                    requireContext(),
                    "Internal Error",
                    "An internal error occurred. Please try again later"
                )
                dialog.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun isNetworkAvailable(): Boolean {
        (requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }
    }

    override fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    override fun onItemClick(article: Article) {
        val bundle = Bundle()
        bundle.putString("url", article.url)
        val articleFragment = ArticleFragment()
        articleFragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.news_container_view, articleFragment)
            addToBackStack(null)
            commit()
        }
    }
//    fun searchHeadlines(query: String?, articles: List<Article>) {
//        val filteredList = mutableListOf<Article>()
//
//        query?.let { q ->
//            for (article in articles) {
//                if (article.title.contains(q, true) || article.description.contains(q, true)) {
//                    filteredList.add(article)
//                }
//            }
//        }
//
//        submitListToAdapter(filteredList)
//    }
}