package com.jmvincenti.reduxsample.features.dashboard

sealed class DashboardCommand {
    object Refresh : DashboardCommand()
}
