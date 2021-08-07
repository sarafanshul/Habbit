package com.projectdelta.habbit.ui.viewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

import com.projectdelta.habbit.data.entities.Task
import com.projectdelta.habbit.repository.TasksRepository
import com.projectdelta.habbit.repository.TasksRepositoryImpl
import com.projectdelta.habbit.ui.base.BaseViewModel
import com.projectdelta.habbit.util.lang.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel

import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	private val repository: TasksRepositoryImpl
	): BaseViewModel() {

	fun getAllTasks( ) : LiveData<List<Task>> = repository.getAllTasks()

}