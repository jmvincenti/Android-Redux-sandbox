package com.jmvincenti.reduxsample.features.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.jmvincenti.core.api.CoreRemoteDataSource
import com.jmvincenti.core.domain.RefreshNewsTask
import com.jmvincenti.core.model.Account
import com.jmvincenti.core.model.News
import com.jmvincenti.core.storage.CoreLocalDataSource
import com.jmvincenti.reduxsample.features.dashboard.state.DashboardAction
import com.jmvincenti.reduxsample.features.dashboard.state.DashboardState
import com.jmvincenti.reduxsample.features.dashboard.state.DashboardStateStore
import com.jmvincenti.state.middleware.CrashReportMiddleware
import com.jmvincenti.state.middleware.VerboseLoggerMiddleware
import java.util.concurrent.Executor

class DashboardViewModel_NoMiddleWare(
    storeExecutor: Executor,
    private val taskExecutor: Executor,
    private val remoteDataSource: CoreRemoteDataSource,
    private val localDataSource: CoreLocalDataSource
) : ViewModel() {

    private val store = DashboardStateStore(
        executor = storeExecutor,
        middleware = listOf(
            VerboseLoggerMiddleware(),
            CrashReportMiddleware()
        )
    )

    val state: LiveData<DashboardState> = store.state

    private val accountObserver = Observer<Account> { account ->
        val currentAccountId = (store.currentState as? DashboardState.Started)?.account?.id

        store.dispatch(DashboardAction.AccountUpdate(account))
        if (currentAccountId != account.id) {
            refreshNews()
        }
    }

    private val newsObserver = Observer<List<News>> { news ->
        store.dispatch(DashboardAction.DataUpdate(news))
    }

    init {
        localDataSource.accountLiveData.observeForever(accountObserver)
        localDataSource.newsLiveData.observeForever(newsObserver)
    }

    override fun onCleared() {
        super.onCleared()

        task?.cancel()
        localDataSource.accountLiveData.removeObserver(accountObserver)
        localDataSource.newsLiveData.removeObserver(newsObserver)
    }

    fun onCommand(command: DashboardCommand) {
        when (command) {
            DashboardCommand.Refresh -> refreshNews()
        }
    }

    var task: RefreshNewsTask? = null

    private fun refreshNews() {
        val currentState = store.currentState
        if (currentState !is DashboardState.Started) {
            return
        }

        store.dispatch(DashboardAction.RefreshStarted)

        task?.cancel()
        task = RefreshNewsTask(
            executor = taskExecutor,
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource,
            accountId = currentState.account.id,
            callback = { result -> store.dispatch(DashboardAction.RefreshEnded(result)) }
        ).also { it.execute() }
    }
}
