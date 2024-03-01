package com.example.thenewsapp.ui.contracts

import com.example.thenewsapp.models.Article

interface NewsFragmentInterface {

    fun initUi()

    fun getLatestNews(countryCode: String)

    fun showProgressBar()

    fun hideProgressBar()

    fun showNoNetworkDialog()

    fun showInternalErrorDialog()

    fun isNetworkAvailable(): Boolean

    fun submitListToAdapter(articles: List<Article>)

//    fun getSearchNews(searchQuery: String, from: String, sortBy: String)

}