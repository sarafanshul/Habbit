package com.projectdelta.habbit.viewModel

import androidx.lifecycle.ViewModel
import com.projectdelta.habbit.repository.TasksRepository
import com.projectdelta.habbit.util.lang.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InsightsSharedViewModel @Inject constructor(
	private val repository: TasksRepository
) : ViewModel() {

	fun getToday() = TimeUtil.getTodayFromEpoch()

	fun getMSFromMidnight() = TimeUtil.getMSfromMidnight()

	suspend fun getDayRange( start : Long , end : Long ) = repository.getDayRange(start, end)

}