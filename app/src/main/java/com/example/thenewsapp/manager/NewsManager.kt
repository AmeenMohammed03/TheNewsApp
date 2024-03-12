package com.example.thenewsapp.manager

import com.example.thenewsapp.db.NewsData
import com.example.thenewsapp.models.Article
import com.example.thenewsapp.models.NewsResponse
import com.example.thenewsapp.ui.contracts.NewsFragmentInterface
import com.example.thenewsapp.ui.contracts.SearchNewsFragmentInterface
import com.google.gson.Gson
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
                view.saveDataInRoom(NewsData("latest", toJsonString(articles)))
            }
        } else {
            view.hideProgressBar()
            view.showInternalErrorDialog()
        }
    }

    fun searchForNews(query: String) {
        if (query.length >= 3) {
            searchView.showProgressBar()
            searchView.hideErrorText()
            searchView.searchForNews(query)
        } else {
            searchView.showErrorText()
        }
    }

    fun filterNews(newsJson: String, searchQuery: String): List<Article> {
        val articles = Gson().fromJson(newsJson, Array<Article>::class.java).toList()
        return articles.filter {
            it.title.contains(searchQuery, true)
        }
    }

    private fun toJsonString(obj: Any): String = Gson().toJson(obj)

}