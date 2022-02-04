package ru.edinros.agitator.core.repositories

import ru.edinros.agitator.core.local.dao.AppDao
import ru.edinros.agitator.core.remote.NetworkHandler
import ru.edinros.agitator.core.remote.RemoteApi
import ru.edinros.agitator.core.remote.safeCall
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val remote: RemoteApi,
    private val local: AppDao,
    private val networkHandler: NetworkHandler
) {
//    suspend fun fetchTaskList()= safeCall(networkHandler){remote.fetchTaskList()}

}