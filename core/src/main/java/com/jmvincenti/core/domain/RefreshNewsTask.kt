package com.jmvincenti.core.domain

import com.jmvincenti.core.api.CoreRemoteDataSource
import com.jmvincenti.core.api.GetNewsResponse
import com.jmvincenti.core.storage.CoreLocalDataSource
import java.util.concurrent.Executor

class RefreshNewsTask(
    private val executor: Executor,
    private val remoteDataSource: CoreRemoteDataSource,
    private val localDataSource: CoreLocalDataSource,
    private val accountId: String,
    callback: (GetNewsResponse) -> Unit
) {

    private var callback: ((GetNewsResponse) -> Unit)? = callback

    fun execute() {
        executor.execute {
            val response = remoteDataSource.getNews(accountId)
            if (response is GetNewsResponse.Success) {
                localDataSource.saveNews(response.news)
            }
            callback?.invoke(response)
        }
    }

    fun cancel() {
        callback = null
    }
}
