package com.projectdelta.habbit.ui.main.viewModel


import androidx.lifecycle.LiveData
import com.projectdelta.habbit.data.model.entities.Task
import com.projectdelta.habbit.data.repository.TasksRepositoryImpl
import com.projectdelta.habbit.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	private val repository: TasksRepositoryImpl
) : BaseViewModel() {

	fun getAllTasks(): LiveData<List<Task>> = repository.getAllTasks()

	fun getAllTasksSorted() = repository.getAllTasksSorted()

}