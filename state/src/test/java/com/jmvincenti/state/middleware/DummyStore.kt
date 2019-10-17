package com.jmvincenti.state.middleware

import com.jmvincenti.state.*
import java.util.concurrent.Executor

data class DummyState(val isInit: Boolean = false, val isTested: Boolean = false) : State

sealed class DummyAction : Action {
    object Init : DummyAction()
    object CrashIfNotInit : DummyAction()
}

val crashingReducer: Reducer<DummyState, DummyAction> = { state, action ->
    when (action) {
        DummyAction.Init -> DummyState(isInit = true)
        DummyAction.CrashIfNotInit -> {
            check(state.isInit) { "State not initialized" }
            state.copy(isTested = true)
        }
    }
}

class CrashingStore : BaseStateStore<DummyState, DummyAction>(
    initialState = DummyState(false)
) {

    override val reducers: List<Reducer<DummyState, DummyAction>> = listOf(crashingReducer)

    override val middleware: List<Middleware<DummyState, DummyAction>> =
        listOf(
            VerboseLoggerMiddleware(),
            CrashReportMiddleware()
        )

    override val executor: Executor = CurrentThreadExecutor()
}

class CurrentThreadExecutor : Executor {
    override fun execute(r: Runnable) {
        r.run()
    }
}
