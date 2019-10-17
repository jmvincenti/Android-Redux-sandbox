package com.jmvincenti.reduxsample.features.dashboard.state

import com.jmvincenti.core.api.GetNewsResponse
import com.jmvincenti.state.Reducer
import com.jmvincenti.state.reducer.StateLoggerReducer

val dashBoardReducers: List<Reducer<DashboardState, DashboardAction>>
    get() = listOf(
        accountUpdateReducer,
        dashboardStateReducer,
        StateLoggerReducer()
    )

val accountUpdateReducer: Reducer<DashboardState, DashboardAction> = { oldState, action ->
    val currentAccountId = (oldState as? DashboardState.Started)?.account?.id

    when (action) {
        is DashboardAction.AccountUpdate -> {
            if (currentAccountId != action.account.id) {
                DashboardState.Started(
                    account = action.account,
                    dataState = DataState.Idle,
                    refreshState = RefreshState.Idle
                )

            } else {
                oldState.copy(account = action.account)
            }
        }

        else -> oldState
    }
}

val dashboardStateReducer: Reducer<DashboardState, DashboardAction> = { oldState, action ->
    when (oldState) {
        is DashboardState.Started -> oldState.copy(
            dataState = dataStateReducer(oldState.dataState, action),
            refreshState = refreshStateReducer(oldState.refreshState, action)
        )

        else -> oldState
    }
}

val dataStateReducer: Reducer<DataState, DashboardAction> = { oldState, action ->
    when (action) {
        is DashboardAction.DataUpdate ->
            if (action.news.isEmpty()) {
                DataState.Empty
            } else {
                DataState.Data(action.news)
            }

        else -> oldState
    }
}

val refreshStateReducer: Reducer<RefreshState, DashboardAction> = { oldState, action ->
    when (action) {
        DashboardAction.RefreshStarted ->
            RefreshState.Loading

        is DashboardAction.RefreshEnded -> {
            when (action.response) {
                is GetNewsResponse.Success -> RefreshState.Success
                is GetNewsResponse.Error -> RefreshState.Error(action.response.message)
            }
        }

        else -> oldState
    }
}
