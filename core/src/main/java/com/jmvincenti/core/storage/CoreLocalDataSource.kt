package com.jmvincenti.core.storage

import androidx.lifecycle.LiveData
import com.jmvincenti.core.model.Account
import com.jmvincenti.core.model.News

interface CoreLocalDataSource {

    val accountLiveData: LiveData<Account>
    fun setAccount(account: Account)

    val newsLiveData: LiveData<List<News>>
    fun saveNews(news: List<News>)
}
