package com.example.thenewsapp.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.thenewsapp.models.NewsResponse
import com.example.thenewsapp.repository.NewsRepository
import com.example.thenewsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(app: Application, val newsRepository: NewsRepository) : AndroidViewModel(app) {

    val headlines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headlinesPage = 1
    private var headlinesResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    private var searchNewsResponse: NewsResponse? = null
    private var newSearchQuery: String? = null
    private var oldSearchQuery: String? = null

    init {
        getHeadlines("us")
    }

    fun getHeadlines(countryCode: String) = viewModelScope.launch {
        fetchNewsInternet(countryCode, false)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        fetchNewsInternet(searchQuery, true)
    }

    private fun handleHeadlinesResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        return handleResponse(response, false)
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        return handleResponse(response, true)
    }

    private fun handleResponse(response: Response<NewsResponse>, isSearch: Boolean): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (isSearch) {
                    if (searchNewsResponse == null || newSearchQuery != oldSearchQuery) {
                        searchNewsPage = 1
                        oldSearchQuery = newSearchQuery
                        searchNewsResponse = resultResponse
                    } else {
                        searchNewsPage++
                        val oldArticles = searchNewsResponse?.articles
                        val newArticles = resultResponse.articles
                        oldArticles?.addAll(newArticles)
                    }
                    return Resource.Success(searchNewsResponse ?: resultResponse)
                } else {
                    headlinesPage++
                    if (headlinesResponse == null) {
                        headlinesResponse = resultResponse
                    } else {
                        val oldArticles = headlinesResponse?.articles
                        val newArticles = resultResponse.articles
                        oldArticles?.addAll(newArticles)
                    }
                    // Filter out removed articles
                    filterRemovedArticles(headlinesResponse)
                    return Resource.Success(headlinesResponse ?: resultResponse)
                }
            }
        }
        return Resource.Error(response.message())
    }

    // Function to filter out removed articles
    private fun filterRemovedArticles(response: NewsResponse?) {
        response?.articles?.removeAll { it.source?.name == "[Removed]" }
    }

    private fun internetConnection(context: Context): Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
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

    //This function handles the network call to fetch headlines
    private suspend fun fetchNewsInternet(countryCode: String, isSearch: Boolean) {
        if (isSearch) {
            searchNews.postValue(Resource.Loading())
        } else {
            headlines.postValue(Resource.Loading())
        }
        try {
            if (internetConnection(this.getApplication())) {
                val response = newsRepository.getHeadlines(countryCode, headlinesPage)
                if (isSearch) {
                    searchNews.postValue(handleSearchNewsResponse(response))
                } else {
                    headlines.postValue(handleHeadlinesResponse(response))
                }
            } else {
                if (isSearch) {
                    searchNews.postValue(Resource.Error("No internet connection"))
                } else {
                    headlines.postValue(Resource.Error("No internet connection"))
                }
            }
        } catch (t: Throwable) {
            val errorMessage = when (t) {
                is IOException -> "Unable to connect"
                else -> "No signal"
            }
            if (isSearch) {
                searchNews.postValue(Resource.Error(errorMessage))
            } else {
                headlines.postValue(Resource.Error(errorMessage))
            }
        }
    }
}