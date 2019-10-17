package com.jmvincenti.state.middleware

import android.util.Log
import com.jmvincenti.state.*

class CrashReportMiddleware<S : State, A : Action> : Middleware<S, A> {
    override fun invoke(action: A, store: Store<S, A>, next: Next<A>) {
        try {
            next(action)
        } catch (e: Exception) {
            // Log to this crashlytics ?
            Log.e(
                "CrashRepor",
                "Error when dispatching:\n" +
                        "    action: $action\n" +
                        "    to state: ${store.currentState}"
            )
            e.printStackTrace()
            throw e
        }
    }
}
