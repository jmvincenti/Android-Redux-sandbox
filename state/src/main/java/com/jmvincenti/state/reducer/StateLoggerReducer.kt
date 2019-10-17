package com.jmvincenti.state.reducer

import android.util.Log
import com.jmvincenti.state.Action
import com.jmvincenti.state.Reducer
import com.jmvincenti.state.State

class StateLoggerReducer<S : State, A : Action> : Reducer<S, A> {
    override fun invoke(state: S, action: A): S {
        Log.d(
            "Logger",
            "  [M] [R]x> State     : ${state.javaClass.simpleName} ($state)"
        )
        return state
    }
}
