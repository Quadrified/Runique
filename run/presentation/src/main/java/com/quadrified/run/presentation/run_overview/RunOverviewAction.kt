package com.quadrified.run.presentation.run_overview

import com.quadrified.run.presentation.run_overview.model.RunUi

sealed interface RunOverviewAction {
    data object OnStartClick : RunOverviewAction
    data object OnLogoutClick : RunOverviewAction
    data object OnAnalyticsClick : RunOverviewAction

    data class DeleteRun(val runUi: RunUi) : RunOverviewAction
}