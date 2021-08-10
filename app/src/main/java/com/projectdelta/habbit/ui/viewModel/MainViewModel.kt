package com.projectdelta.habbit.ui.viewModel


import androidx.lifecycle.LiveData
import com.projectdelta.habbit.data.entities.Task
import com.projectdelta.habbit.repository.TasksRepositoryImpl
import com.projectdelta.habbit.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	private val repository: TasksRepositoryImpl
	): BaseViewModel() {

	fun getAllTasks( ) : LiveData<List<Task>> = repository.getAllTasks()

}