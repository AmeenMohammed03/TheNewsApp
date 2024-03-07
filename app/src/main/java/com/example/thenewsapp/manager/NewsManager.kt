package com.example.thenewsapp.manager

import com.example.thenewsapp.models.Article
import com.example.thenewsapp.models.NewsResponse
import com.example.thenewsapp.ui.contracts.NewsFragmentInterface
import com.example.thenewsapp.ui.contracts.SearchNewsFragmentInterface
import retrofit2.Response

class NewsManager() {

    private lateinit var view: NewsFragmentInterface
    private lateinit var searchView: SearchNewsFragmentInterface
    constructor(view: NewsFragmentInterface) : this() {
        this.view = view
    }

    constructor(view: SearchNewsFragmentInterface) : this() {
        this.searchView = view
    }

    fun getLatestNews(countryCode: String) {
        if (view.isNetworkAvailable()) {
            view.getLatestNews(countryCode)
        } else {
            view.hideProgressBar()
            view.showNoNetworkDialog()
        }
    }

    fun handleLatestNewsResponse(response: Response<NewsResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { newsResponse ->
                val articles = newsResponse.articles
                articles.removeAll { it.source!!.name.equals("[Removed]", true)}
                view.submitListToAdapter(articles)
            }
        } else {
            view.hideProgressBar()
            view.showInternalErrorDialog()
        }
    }

    fun searchForNews(query: String) {
        if (searchView.isNetworkAvailable()) {
            searchView.searchForNews(query)
        } else {
            searchView.hideProgressBar()
//            searchView.showNoNetworkDialog()
        }
    }

    fun handleSearchNewsResponse(response: Response<NewsResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { newsResponse ->
                val articles = newsResponse.articles
                articles.removeAll { it.source!!.name.equals("[Removed]", true)}
                if (articles.isEmpty()) {
                    searchView.showNoNewsFoundToast()
                }
                searchView.submitListToAdapter(articles)
            }
        } else {
            searchView.hideProgressBar()
//            searchView.showInternalErrorDialog()
        }
    }
}