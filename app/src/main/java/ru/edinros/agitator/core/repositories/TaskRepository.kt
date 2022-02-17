package ru.edinros.agitator.core.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import ru.edinros.agitator.core.local.dao.AppDao
import ru.edinros.agitator.core.remote.NetworkHandler
import ru.edinros.agitator.core.remote.RemoteApi
import ru.edinros.agitator.core.remote.safeCall
import ru.edinros.agitator.features.task.TaskFetcherStatus
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val remote: RemoteApi,
    private val local: AppDao,
    private val networkHandler: NetworkHandler
) {

    fun getTaskListFlow() = local.getTaskListFlow()

    fun fetchAndSaveTask() = flow <TaskFetcherStatus>{
        safeCall(networkHandler = networkHandler){remote.fetchTasks()}.fold(
            {emit(TaskFetcherStatus.Error(it.errorMsg()))},
            {
                when(it.ok){
                    true -> {
                        delay(2000)
                        local.insertTasks(it.result.map { e->e.toEntity() })
                        emit(TaskFetcherStatus.Success)

                    }
                    false -> emit(TaskFetcherStatus.Error("Сервер вернул флаг false"))
                }
            }
        )
    }.onStart { emit(TaskFetcherStatus.Progress) }.flowOn(Dispatchers.IO)
}