package com.projectdelta.habbit.ui.main.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.projectdelta.habbit.data.model.entities.Task
import com.projectdelta.habbit.data.repository.TasksRepositoryImpl
import com.projectdelta.habbit.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Shared ViewModel for fragments
 * @param repository : injected!
 */
@HiltViewModel
class HomeSharedViewModel @Inject constructor(
	private val repository: TasksRepositoryImpl
) : BaseViewModel() {

	private var _data: LiveData<List<Task>>? = null
	val data
		get() = _data!!

	init {
		_data = repository.getAllTasks()
	}

	fun getAllTasksSorted() = repository.getAllTasksSorted()

	fun notifyTaskDone(task: Task) {
		viewModelScope.launch(Dispatchers.IO) {
			// get fresh instance of task
			val curTask = withContext(Dispatchers.IO) {
				repository.getTaskById(task.id)
			}
			curTask.lastDayCompleted.add(getTodayFromEpoch())
			repository.markTaskCompleted(curTask)
		}
	}

	fun notifyTaskSkipped(task: Task) {
		viewModelScope.launch(Dispatchers.IO) {
			val cur = withContext(Dispatchers.IO) {
				repository.getTaskById(task.id)
			}
			cur.skipTill = getTodayFromEpoch()
			repository.updateTask(cur)
		}
	}

	fun delete(task: Task) = viewModelScope.launch(Dispatchers.IO) {
		repository.deleteTask(task)
	}

	fun insertTask(task: Task) = viewModelScope.launch(Dispatchers.IO) {
		repository.insertTask(task)
	}

}