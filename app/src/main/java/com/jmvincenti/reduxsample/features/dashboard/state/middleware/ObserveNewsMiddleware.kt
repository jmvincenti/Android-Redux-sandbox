package com.jmvincenti.reduxsample.features.dashboard.state.middleware

import com.jmvincenti.core.model.News
import com.jmvincenti.core.storage.CoreLocalDataSource
import com.jmvincenti.reduxsample.features.dashboard.state.DashboardAction
import com.jmvincenti.reduxsample.features.dashboard.state.DashboardState
import com.jmvincenti.state.Middleware
import com.jmvincenti.state.Next
import com.jmvincenti.state.Store

class ObserveNewsMiddleware(
    private val localDataSource: CoreLocalDataSource
) : Middleware<DashboardState, DashboardAction> {

    private var observer: NewsObserver? = null

    override fun invoke(
        action: DashboardAction,
        store: Store<DashboardState, DashboardAction>,
        next: Next<DashboardAction>
    ) {
        val currentAccountId = (store.currentState as? DashboardState.Started)?.account?.id
        when {
            action is DashboardAction.AccountUpdate && currentAccountId != action.account.id -> {
                next(action)
                observeNews(action.account.id, store)
            }

            action is DashboardAction.ClearCommand -> {
                next(action)
                observer?.stopObserve()
            }

            else -> next(action)
        }
    }

    private fun observeNews(accountId: String, store: Store<DashboardState, DashboardAction>) {
        observer?.stopObserve()
        observer = NewsObserver(
            localDataSource = localDataSource,
            accountId = accountId,
            callback = { store.dispatch(DashboardAction.DataUpdate(it)) }
        ).also { it.startObserve() }
    }
}

private class NewsObserver(
    private val localDataSource: CoreLocalDataSource,
    private val accountId: String,
    private val callback: (List<News>) -> Unit
) {
    fun startObserve() {
        localDataSource.newsLiveData.observeForever(callback)
    }

    fun stopObserve() {
        localDataSource.newsLiveData.removeObserver(callback)
    }
}
