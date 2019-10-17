package com.jmvincenti.state.middleware

import android.util.Log
import com.jmvincenti.state.*

class ActionOutLoggerMiddleware<S : State, A : Action> : Middleware<S, A> {
    override fun invoke(action: A, store: Store<S, A>, next: Next<A>) {
        Log.d(
            "Logger",
            "  [M]x[R] > Action    : ${action.javaClass.simpleName} ($action)"
        )
        next(action)
    }
}

