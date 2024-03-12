package com.example.thenewsapp.ui.contracts

import com.example.thenewsapp.db.NewsData
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

    fun saveDataInRoom(data: NewsData)

//    fun getSearchNews(searchQuery: String, from: String, sortBy: String)

}

interface SearchNewsFragmentInterface {

    fun initUi()

    fun searchForNews(query: String)

    fun showProgressBar()

    fun hideProgressBar()

    fun showNoNetworkDialog()

    fun showInternalErrorDialog()

    fun isNetworkAvailable(): Boolean

    fun submitListToAdapter(articles: List<Article>)

    fun showNoNewsFoundToast()

    fun showErrorText()

    fun hideErrorText()

}