package com.jmvincenti.state

import androidx.lifecycle.LiveData

interface Action

interface State

interface Store<S : State, A : Action> {
    val currentState: S
    val state: LiveData<S>
    fun dispatch(action: A)
}

typealias Reducer<S, A> = (S, A) -> S

typealias Middleware<S, A> = (A, Store<S, A>, Next<A>) -> Unit
typealias Next<A> = (A) -> Unit
