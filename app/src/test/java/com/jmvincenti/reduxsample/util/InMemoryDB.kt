package com.jmvincenti.reduxsample.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jmvincenti.core.model.Account
import com.jmvincenti.core.model.News
import com.jmvincenti.core.storage.CoreLocalDataSource

class InMemoryDB : CoreLocalDataSource {

    private val _accountLiveData = MutableLiveData<Account>()
    override val accountLiveData: LiveData<Account>
        get() = _accountLiveData

    override fun setAccount(account: Account) {
        _accountLiveData.postValue(account)
    }

    private val _newsLiveData = MutableLiveData<List<News>>()
    override val newsLiveData: LiveData<List<News>>
        get() = _newsLiveData

    override fun saveNews(news: List<News>) {
        _newsLiveData.postValue(news)
    }
}
