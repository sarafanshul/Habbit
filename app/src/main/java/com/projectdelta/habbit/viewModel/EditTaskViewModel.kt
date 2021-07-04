package com.projectdelta.habbit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projectdelta.habbit.data.entities.Task
import com.projectdelta.habbit.repository.TasksRepository
import com.projectdelta.habbit.util.lang.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
	private val repository: TasksRepository
) : ViewModel() {

	fun getTaskById( id : Long ) : Task = repository.getTaskById( id )

	fun updateTask( task: Task ) = viewModelScope.launch(Dispatchers.IO) {
		repository.insertTask( task )
	}

	fun getTodayFromEpoch( ) = TimeUtil.getTodayFromEpoch()

	fun delete( task: Task ) = viewModelScope.launch (Dispatchers.IO){
		repository.deleteTask( task )
	}
}