package com.jmvincenti.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.Executor
import kotlin.properties.Delegates

abstract class BaseStateStore<S : State, A : Action>(
    initialState: S
) : Store<S, A> {

    abstract val reducers: List<Reducer<S, A>>
    abstract val middleware: List<Middleware<S, A>>
    abstract val executor: Executor

    private var _state = MutableLiveData<S>()

    override val currentState: S
        get() = _currentState

    private var _currentState: S by Delegates.observable(initialState) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            _state.postValue(newValue)
        }
    }

    override val state: LiveData<S> = _state

    override fun dispatch(action: A) {
        executor.execute {
            applyMiddleware(action)
        }
    }

    private fun applyReducers(action: A) {
        var state = _currentState
        for (reducer in reducers) {
            state = reducer(state, action)
        }

        _currentState = state
    }

    private fun applyMiddleware(action: A) {
        val chain = next(0)
        chain(action)
    }

    private fun next(index: Int): Next<A> {
        if (index == middleware.size) {
            // Last link of the chain. Apply reducers
            return { action -> applyReducers(action) }
        }

        return { action ->
            middleware[index](
                action,
                this@BaseStateStore,
                next(index + 1)
            )
        }
    }
}
