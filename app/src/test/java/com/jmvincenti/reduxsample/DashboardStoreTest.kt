package com.jmvincenti.reduxsample

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jmvincenti.core.model.Account
import com.jmvincenti.core.model.News
import com.jmvincenti.reduxsample.features.dashboard.state.*
import com.jmvincenti.reduxsample.features.dashboard.state.middleware.ObserveAccountMiddleware
import com.jmvincenti.reduxsample.features.dashboard.state.middleware.ObserveNewsMiddleware
import com.jmvincenti.reduxsample.features.dashboard.state.middleware.RefreshNewsMiddleware
import com.jmvincenti.reduxsample.util.FailingRemoteDataSource
import com.jmvincenti.reduxsample.util.InMemoryDB
import com.jmvincenti.state.middleware.ActionInLoggerMiddleware
import com.jmvincenti.state.middleware.ActionOutLoggerMiddleware
import com.jmvincenti.state.middleware.CrashReportMiddleware
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.concurrent.Executor

class DashboardStoreTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val singleThreadExecutor = Executor { it.run() }

    @Test
    fun `cached news with failing refresh should return news`() {

        // Init cached news
        val localDataSource = InMemoryDB()
        val cachedNews = listOf(
            News("Id1", "Title1"),
            News("Id2", "Title2")
        )
        localDataSource.saveNews(cachedNews)

        // Init remote source
        val remoteDataSource = FailingRemoteDataSource()

        // Update account
        val account = Account("AccountId")
        localDataSource.setAccount(account)

        // Init store
        val store = DashboardStateStore(
            executor = singleThreadExecutor,
            middleware = listOf(
                ActionInLoggerMiddleware(),
                CrashReportMiddleware(),
                ObserveAccountMiddleware(localDataSource),
                ObserveNewsMiddleware(localDataSource),
                RefreshNewsMiddleware(singleThreadExecutor, remoteDataSource, localDataSource),
                ActionOutLoggerMiddleware()
            )
        )
        store.dispatch(DashboardAction.InitCommand)

        // Expected state
        val expectedState = DashboardState.Started(
            account = account,
            dataState = DataState.Data(cachedNews),
            refreshState = RefreshState.Error("Mocked failing task")
        )

        assertEquals(expectedState, store.currentState)
    }
}

