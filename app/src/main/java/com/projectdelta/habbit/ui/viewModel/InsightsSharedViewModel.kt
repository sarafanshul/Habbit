package com.projectdelta.habbit.ui.viewModel

import com.projectdelta.habbit.data.repository.TasksRepositoryImpl
import com.projectdelta.habbit.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InsightsSharedViewModel @Inject constructor(
	private val repository: TasksRepositoryImpl
) : BaseViewModel() {

	val getAllDays = repository.getAllDays()

	suspend fun getDayRange( start : Long , end : Long ) = repository.getDayRange(start, end)

}