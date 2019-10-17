package com.jmvincenti.reduxsample.features.dashboard.state.middleware

import com.jmvincenti.core.model.Account
import com.jmvincenti.core.storage.CoreLocalDataSource
import com.jmvincenti.reduxsample.features.dashboard.state.DashboardAction
import com.jmvincenti.reduxsample.features.dashboard.state.DashboardState
import com.jmvincenti.state.Middleware
import com.jmvincenti.state.Next
import com.jmvincenti.state.Store

class ObserveAccountMiddleware(
    private val localDataSource: CoreLocalDataSource
) : Middleware<DashboardState, DashboardAction> {

    private var observer: AccountObserver? = null

    override fun invoke(
        action: DashboardAction,
        store: Store<DashboardState, DashboardAction>,
        next: Next<DashboardAction>
    ) {
        when (action) {
            is DashboardAction.InitCommand -> {
                next(action)
                observeAccount(store)
            }

            is DashboardAction.ClearCommand -> {
                next(action)
                observer?.stopObserve()
            }

            else -> next(action)
        }
    }

    private fun observeAccount(store: Store<DashboardState, DashboardAction>) {
        observer?.stopObserve()
        observer = AccountObserver(
            localDataSource = localDataSource,
            callback = { store.dispatch(DashboardAction.AccountUpdate(it)) }
        ).also { it.startObserve() }
    }
}

private class AccountObserver(
    private val localDataSource: CoreLocalDataSource,
    private val callback: (Account) -> Unit
) {
    fun startObserve() {
        localDataSource.accountLiveData.observeForever(callback)
    }

    fun stopObserve() {
        localDataSource.accountLiveData.removeObserver(callback)
    }
}
