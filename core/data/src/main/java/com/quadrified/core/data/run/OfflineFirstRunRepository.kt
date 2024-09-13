package com.quadrified.core.data.run

import com.quadrified.core.data.networking.get
import com.quadrified.core.database.dao.RunPendingSyncDao
import com.quadrified.core.database.mappers.toRun
import com.quadrified.core.domain.SessionStorage
import com.quadrified.core.domain.run.LocalRunDataSource
import com.quadrified.core.domain.run.RemoteRunDataSource
import com.quadrified.core.domain.run.Run
import com.quadrified.core.domain.run.RunId
import com.quadrified.core.domain.run.RunRepository
import com.quadrified.core.domain.run.SyncRunScheduler
import com.quadrified.core.domain.util.DataError
import com.quadrified.core.domain.util.EmptyResult
import com.quadrified.core.domain.util.Result
import com.quadrified.core.domain.util.asEmptyDataResult
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Implementation of "RunRepository" from "core/domain/run"
// Fetching => First we save all data in local
// Posting => Do any operation in localDB and then do remote calls
class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val applicationScope: CoroutineScope,
    private val runPendingSyncDao: RunPendingSyncDao,
    private val sessionStorage: SessionStorage,
    private val syncRunScheduler: SyncRunScheduler,
    private val client: HttpClient
) : RunRepository {
    /**
     * LocalDB to be single source of truth
     * Fetching data from remote and pushing it to localDB and display it on UI
     * NEVER FETCHING AND SHOWING DIRECTLY FROM API
     */

    // From LocalDB
    override fun getRuns(): Flow<List<Run>> {
        // getRuns() interface from "core.domain.run"
        // getRuns() implementation in "RoomLocalRunDataSource" from "core/database"
        return localRunDataSource.getRuns()
    }

    // From Remote
    override suspend fun fetchRuns(): EmptyResult<DataError> {
        return when (val result = remoteRunDataSource.getRuns()) {
            is Result.Error -> result.asEmptyDataResult()
            is Result.Success -> {
                // Different from VM where fetchRuns is called
                // applicationScope lives longer
                applicationScope.async {
                    // Fetching runs and updating it in localDB
                    // Triggers getRuns() to keep the data up to date
                    localRunDataSource.upsertRuns(result.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun upsertRun(run: Run, mapPicture: ByteArray): EmptyResult<DataError> {
        val localResult = localRunDataSource.upsertRun(run)
        if (localResult !is Result.Success) {
            return localResult.asEmptyDataResult()
        }

        // id => inserted from localDB
        val runWithId = run.copy(id = localResult.data)
        val remoteResult = remoteRunDataSource.postRun(
            run = runWithId, mapPicture = mapPicture
        )

        return when (remoteResult) {
            is Result.Error -> {
                applicationScope.launch {
                    syncRunScheduler.scheduleSync(
                        type = SyncRunScheduler.SyncType.CreateRun(
                            run = runWithId, mapPictureBytes = mapPicture
                        )
                    )
                }.join()
                Result.Success(Unit)
            }

            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsertRun(remoteResult.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun deleteRun(id: RunId) {
        localRunDataSource.deleteRun(id)

        // For an entity that was created and deleted LOCALLY
        // Because delete sync is faster and after delete the locally created run will be synced
        // This results in a run that was deleted LOCALLY and then again synced up to REMOTE
        val isPendingSync = runPendingSyncDao.getRunPendingSyncEntity(id) != null

        if (isPendingSync) {
            runPendingSyncDao.deleteRunPendingSyncEntity(id)
            return
        }

        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteRun(id)
        }.await()

        if (remoteResult is Result.Error) {
            applicationScope.launch {
                syncRunScheduler.scheduleSync(
                    type = SyncRunScheduler.SyncType.DeleteRun(
                        runId = id
                    )
                )
            }.join()
        }
    }

    override suspend fun deleteAllRuns() {
        localRunDataSource.deleteAllRuns()
    }

    override suspend fun syncPendingRuns() {
        // To create independent coroutines
        withContext(Dispatchers.IO) {
            val userId = sessionStorage.get()?.userId ?: return@withContext

            // Runs that were created LOCALLY but were never synced REMOTELY
            val createdRuns = async {
                runPendingSyncDao.getAllRunPendingSyncEntities(userId)
            }

            // Runs that were deleted LOCALLY but were never synced REMOTELY
            val deletedRuns = async {
                runPendingSyncDao.getAllDeletedRunSyncEntities(userId)
            }

            val createJobs = createdRuns.await().map {
                launch {
                    val run = it.run.toRun()
                    when (remoteRunDataSource.postRun(
                        run, it.mapPictureBytes
                    )) {
                        is Result.Error -> Unit
                        is Result.Success -> {
                            applicationScope.launch {
                                // To delete the pending run after sync from localDB
                                runPendingSyncDao.deleteRunPendingSyncEntity(it.runId)
                            }.join()
                        }
                    }
                }
            }

            val deleteJobs = deletedRuns.await().map {
                launch {
                    when (remoteRunDataSource.deleteRun(
                        it.runId,
                    )) {
                        is Result.Error -> Unit
                        is Result.Success -> {
                            applicationScope.launch {
                                // To delete the pending deleted run after sync from localDB
                                runPendingSyncDao.deleteDeletedRunSyncEntity(it.runId)
                            }.join()
                        }
                    }
                }
            }

            // join() => waits till all coroutines are resolved
            createJobs.forEach { it.join() }
            deleteJobs.forEach { it.join() }
        }
    }

    override suspend fun logout(): EmptyResult<DataError.Network> {
        val result = client.get<Unit>(
            route = "/logout"
        ).asEmptyDataResult()

        client.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>()
            .firstOrNull()
            ?.clearToken()

        return result
    }
}