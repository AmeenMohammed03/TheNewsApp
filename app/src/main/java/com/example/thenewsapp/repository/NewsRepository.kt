package com.example.thenewsapp.repository

import com.example.thenewsapp.api.RetrofitInstance

class NewsRepository {

    suspend fun getHeadlines(countryCode: String) =
        RetrofitInstance.api.getHeadlines(countryCode)

    suspend fun searchNews(searchQuery: String) =
        RetrofitInstance.api.searchForNews(searchQuery)
}