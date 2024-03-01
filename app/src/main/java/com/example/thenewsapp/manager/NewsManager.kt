package com.example.thenewsapp.manager

import com.example.thenewsapp.models.NewsResponse
import com.example.thenewsapp.ui.contracts.NewsFragmentInterface
import retrofit2.Response

class NewsManager(private var view: NewsFragmentInterface) {

    fun getLatestNews(countryCode: String) {
        if (view.isNetworkAvailable()) {
            view.getLatestNews(countryCode)
        } else {
            view.hideProgressBar()
            view.showNoNetworkDialog()
        }
    }

    fun handleResponse(response: Response<NewsResponse>) {
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
}