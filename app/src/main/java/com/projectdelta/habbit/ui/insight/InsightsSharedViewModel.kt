package com.projectdelta.habbit.ui.insight

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.projectdelta.habbit.data.repository.TasksRepositoryImpl
import com.projectdelta.habbit.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InsightsSharedViewModel @Inject constructor(
	private val repository: TasksRepositoryImpl
) : BaseViewModel() {

	fun getAllDays() = repository.getAllDays()

	val getAllDaysPaged = repository.getAllDaysDataPaged().cachedIn(viewModelScope)

	suspend fun getDayRange(start: Long, end: Long) = repository.getDayRange(start, end)

}