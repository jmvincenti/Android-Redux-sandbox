package com.jmvincenti.state.middleware

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class StoreTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Test
    fun `not crash the store`() {
        val store = CrashingStore()
        store.dispatch(DummyAction.Init)
        store.dispatch(DummyAction.CrashIfNotInit)
        assert(store.currentState == DummyState(isInit = true, isTested = true))
    }

    @Test(expected = java.lang.IllegalStateException::class)
    fun `crash the store`() {
        val store = CrashingStore()
        store.dispatch(DummyAction.CrashIfNotInit)
    }
}
