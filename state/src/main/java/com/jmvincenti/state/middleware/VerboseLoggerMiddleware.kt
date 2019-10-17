package com.jmvincenti.state.middleware

import android.util.Log
import com.jmvincenti.state.*

class VerboseLoggerMiddleware<S : State, A : Action> : Middleware<S, A> {
    override fun invoke(action: A, store: Store<S, A>, next: Next<A>) {
        log("---------------")
        log("--> Prev state: ${store.currentState.javaClass.simpleName} (${store.currentState})")
        log("--> Action    : ${action.javaClass.simpleName} ($action)")
        next(action)
        log("--> New state : ${store.currentState.javaClass.simpleName} (${store.currentState})")
        log("---------------")
    }

    private fun log(msg: String) {
        Log.d("Logger", msg)
    }
}

