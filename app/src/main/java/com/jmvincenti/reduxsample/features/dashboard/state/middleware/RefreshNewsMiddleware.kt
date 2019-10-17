package com.jmvincenti.reduxsample.features.dashboard.state.middleware

import com.jmvincenti.core.api.CoreRemoteDataSource
import com.jmvincenti.core.domain.RefreshNewsTask
import com.jmvincenti.core.storage.CoreLocalDataSource
import com.jmvincenti.reduxsample.features.dashboard.state.DashboardAction
import com.jmvincenti.reduxsample.features.dashboard.state.DashboardState
import com.jmvincenti.state.Middleware
import com.jmvincenti.state.Next
import com.jmvincenti.state.Store
import java.util.concurrent.Executor

class RefreshNewsMiddleware(
    private val taskExecutor: Executor,
    private val remoteDataSource: CoreRemoteDataSource,
    private val localDataSource: CoreLocalDataSource
) : Middleware<DashboardState, DashboardAction> {
    private var task: RefreshNewsTask? = null

    override fun invoke(
        action: DashboardAction,
        store: Store<DashboardState, DashboardAction>,
        next: Next<DashboardAction>
    ) {
        val currentAccountId = (store.currentState as? DashboardState.Started)?.account?.id
        when {
            action == DashboardAction.RefreshCommand &&
                    currentAccountId != null -> {

                next(DashboardAction.RefreshStarted)
                refreshNews(currentAccountId, store)
            }

            action is DashboardAction.AccountUpdate &&
                    currentAccountId != action.account.id -> {

                next(action)
                next(DashboardAction.RefreshStarted)
                refreshNews(action.account.id, store)
            }

            action == DashboardAction.ClearCommand ->
                task?.cancel()

            else -> next(action)
        }
    }

    private fun refreshNews(accountId: String, store: Store<DashboardState, DashboardAction>) {
        task?.cancel()
        task = RefreshNewsTask(
            executor = taskExecutor,
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource,
            accountId = accountId,
            callback = {
                store.dispatch(DashboardAction.RefreshEnded(it))
            }
        ).also { it.execute() }
    }
}
