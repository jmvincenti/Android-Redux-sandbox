package com.jmvincenti.reduxsample.features.dashboard.state

import com.jmvincenti.state.BaseStateStore
import com.jmvincenti.state.Middleware
import com.jmvincenti.state.Reducer
import java.util.concurrent.Executor

class DashboardStateStore(
    override val middleware: List<Middleware<DashboardState, DashboardAction>>,
    override val executor: Executor
) : BaseStateStore<DashboardState, DashboardAction>(
    initialState = DashboardState.Idle
) {
    override val reducers: List<Reducer<DashboardState, DashboardAction>> = dashBoardReducers
}
