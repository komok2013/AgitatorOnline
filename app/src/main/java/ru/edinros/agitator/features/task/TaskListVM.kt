package ru.edinros.agitator.features.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.edinros.agitator.core.repositories.TaskRepository
import timber.log.Timber
import javax.inject.Inject


sealed class TaskFetcherStatus {
    object Progress : TaskFetcherStatus()
    data class Error(val errorMessage: String) : TaskFetcherStatus()
    object Success : TaskFetcherStatus()

}

@HiltViewModel
class TaskListVM @Inject constructor(private val repository: TaskRepository) : ViewModel() {
    val list = repository.getTaskListFlow().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _refreshTaskStatus = MutableStateFlow<TaskFetcherStatus>(TaskFetcherStatus.Progress)
    val refreshTaskStatus = _refreshTaskStatus.asStateFlow()
    private fun refreshTask() = viewModelScope.launch {
        repository.fetchAndSaveTask().collect {
            _refreshTaskStatus.value = it
        }
    }

    init {
        refreshTask()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("$this cleared")
    }

}