package com.jmvincenti.reduxsample.features.dashboard.state

import com.jmvincenti.core.api.GetNewsResponse
import com.jmvincenti.core.model.Account
import com.jmvincenti.core.model.News
import com.jmvincenti.state.Action

sealed class DashboardAction : Action {
    data class AccountUpdate(val account: Account) : DashboardAction()

    data class DataUpdate(val news: List<News>) : DashboardAction()

    object RefreshStarted : DashboardAction()
    data class RefreshEnded(val response: GetNewsResponse) : DashboardAction()

    // With middleware
    object RefreshCommand : DashboardAction()

    object InitCommand : DashboardAction()
    object ClearCommand : DashboardAction()
}
