package com.jmvincenti.reduxsample.features.dashboard.state

import com.jmvincenti.core.model.Account
import com.jmvincenti.core.model.News
import com.jmvincenti.state.State

sealed class DashboardState : State {

    object Idle : DashboardState()

    data class Started(
        val account: Account,
        val dataState: DataState,
        val refreshState: RefreshState
    ) : DashboardState()
}

sealed class DataState : State {
    object Idle : DataState()
    object Empty : DataState()
    data class Data(val news: List<News>) : DataState()
}

sealed class RefreshState : State {
    object Idle : RefreshState()
    object Loading : RefreshState()
    object Success : RefreshState()
    data class Error(val errorMessage: String) : RefreshState()
}
