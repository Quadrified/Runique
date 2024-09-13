package com.quadrified.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quadrified.core.domain.SessionStorage
import com.quadrified.core.domain.run.RunRepository
import com.quadrified.core.domain.run.SyncRunScheduler
import com.quadrified.run.presentation.run_overview.mapper.toRunUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class RunOverviewViewModel(
    private val runRepository: RunRepository,
    private val syncRunScheduler: SyncRunScheduler,
    private val applicationScope: CoroutineScope,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    var state by mutableStateOf(RunOverviewState())
        private set

    init {
        // Periodic fetching of Run data via WorkManager Workers
        viewModelScope.launch {
            syncRunScheduler.scheduleSync(
                type = SyncRunScheduler.SyncType.FetchRuns(30.minutes)
            )
        }

        runRepository.getRuns().onEach { runs ->
            val runsUi = runs.map { it.toRunUi() }
            state = state.copy(runs = runsUi)
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            // Syncing pending runs before fetching remote
            runRepository.syncPendingRuns()

            // Fetch from remote
            runRepository.fetchRuns()
        }
    }

    fun onAction(action: RunOverviewAction) {
        when (action) {
            RunOverviewAction.OnLogoutClick -> Unit
            RunOverviewAction.OnStartClick -> Unit
            is RunOverviewAction.DeleteRun -> {
                viewModelScope.launch {
                    runRepository.deleteRun(action.runUi.id)
                }
            }

            else -> Unit
        }
    }

    private fun logout() {
        applicationScope.launch {
            // Cancelling All Schedulers
            syncRunScheduler.cancelAllSyncs()

            // Deleting localDB
            runRepository.deleteAllRuns()

            // Invalidating Token in API
            runRepository.logout()

            // Clearing Tokens in SessionStorage
            sessionStorage.set(null)
        }
    }
}