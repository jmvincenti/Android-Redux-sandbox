package com.jmvincenti.reduxsample.util

import com.jmvincenti.core.api.CoreRemoteDataSource
import com.jmvincenti.core.api.GetNewsResponse

class FailingRemoteDataSource : CoreRemoteDataSource {
    override fun getNews(accountId: String): GetNewsResponse {
        return GetNewsResponse.Error("Mocked failing task")
    }
}
