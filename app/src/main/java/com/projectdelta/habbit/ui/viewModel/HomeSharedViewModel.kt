package com.projectdelta.habbit.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projectdelta.habbit.data.entities.Task
import com.projectdelta.habbit.repository.TasksRepository
import com.projectdelta.habbit.repository.TasksRepositoryImpl
import com.projectdelta.habbit.util.lang.TimeUtil
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
) : ViewModel() {

	private var _data : LiveData<List<Task>> ?= null
	val data
		get() = _data!!

	init {
		_data = repository.getAllTasks()
	}

	fun getToday() = TimeUtil.getTodayFromEpoch()

	fun getMSFromMidnight() = TimeUtil.getMSfromMidnight()

	fun notifyTaskDone(task : Task) {
		viewModelScope.launch(Dispatchers.IO) {
			// get fresh instance of task
			val curTask = withContext(Dispatchers.IO) {
				repository.getTaskById(task.id)
			}
			curTask.lastDayCompleted.add( getToday() )
			repository.markTaskCompleted( curTask )
		}
	}

	fun notifyTaskSkipped(task: Task) {
		viewModelScope.launch(Dispatchers.IO) {
			val cur = withContext(Dispatchers.IO) {
				repository.getTaskById(task.id)
			}
			cur.skipTill = getToday()
			repository.updateTask(cur)
		}
	}

	fun delete( task: Task ) = viewModelScope.launch (Dispatchers.IO){
		repository.deleteTask( task )
	}

}