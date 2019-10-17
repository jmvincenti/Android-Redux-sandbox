package com.jmvincenti.reduxsample.features.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jmvincenti.core.api.CoreRemoteDataSource
import com.jmvincenti.core.storage.CoreLocalDataSource
import com.jmvincenti.reduxsample.features.dashboard.state.DashboardAction
import com.jmvincenti.reduxsample.features.dashboard.state.DashboardState
import com.jmvincenti.reduxsample.features.dashboard.state.DashboardStateStore
import com.jmvincenti.reduxsample.features.dashboard.state.middleware.ObserveAccountMiddleware
import com.jmvincenti.reduxsample.features.dashboard.state.middleware.ObserveNewsMiddleware
import com.jmvincenti.reduxsample.features.dashboard.state.middleware.RefreshNewsMiddleware
import com.jmvincenti.state.middleware.CrashReportMiddleware
import com.jmvincenti.state.middleware.VerboseLoggerMiddleware
import java.util.concurrent.Executor

class DashboardViewModel_WithMiddleWare(
    storeExecutor: Executor,
    taskExecutor: Executor,
    localDataSource: CoreLocalDataSource,
    remoteDataSource: CoreRemoteDataSource
) : ViewModel() {

    private val store = DashboardStateStore(
        executor = storeExecutor,
        middleware = listOf(
            // Logging
            VerboseLoggerMiddleware(),
            CrashReportMiddleware(),

            // DB
            ObserveAccountMiddleware(localDataSource),
            ObserveNewsMiddleware(localDataSource),

            // Logic
            RefreshNewsMiddleware(taskExecutor, remoteDataSource, localDataSource)
        )
    )

    val state: LiveData<DashboardState> = store.state

    override fun onCleared() {
        super.onCleared()

        store.dispatch(DashboardAction.ClearCommand)
    }

    fun onCommand(command: DashboardCommand) {
        when (command) {
            DashboardCommand.Refresh -> store.dispatch(DashboardAction.RefreshCommand)
        }
    }
}
