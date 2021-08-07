package com.projectdelta.habbit.ui.viewModel

import androidx.lifecycle.ViewModel
import com.projectdelta.habbit.repository.TasksRepository
import com.projectdelta.habbit.repository.TasksRepositoryImpl
import com.projectdelta.habbit.ui.base.BaseViewModel
import com.projectdelta.habbit.util.lang.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InsightsSharedViewModel @Inject constructor(
	private val repository: TasksRepositoryImpl
) : BaseViewModel() {

	val getAllDays = repository.getAllDays()

	suspend fun getDayRange( start : Long , end : Long ) = repository.getDayRange(start, end)

}