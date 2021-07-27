package com.projectdelta.habbit.ui.viewModel

import androidx.lifecycle.ViewModel
import com.projectdelta.habbit.repository.TasksRepository
import com.projectdelta.habbit.repository.TasksRepositoryImpl
import com.projectdelta.habbit.util.lang.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InsightsSharedViewModel @Inject constructor(
	private val repository: TasksRepositoryImpl
) : ViewModel() {

	val getAllDays = repository.getAllDays()

	fun getToday() = TimeUtil.getTodayFromEpoch()

	fun getMSFromMidnight() = TimeUtil.getMSfromMidnight()

	suspend fun getDayRange( start : Long , end : Long ) = repository.getDayRange(start, end)

}