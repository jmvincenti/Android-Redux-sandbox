package com.jmvincenti.core.api

import com.jmvincenti.core.model.News

interface CoreRemoteDataSource {
    fun getNews(accountId: String): GetNewsResponse
}

sealed class GetNewsResponse {
    data class Success(val news: List<News>) : GetNewsResponse()
    data class Error(val message: String) : GetNewsResponse()
}
