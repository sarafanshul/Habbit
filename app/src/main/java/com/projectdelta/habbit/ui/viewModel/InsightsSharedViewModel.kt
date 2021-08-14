package com.projectdelta.habbit.ui.viewModel

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.projectdelta.habbit.data.repository.TasksRepositoryImpl
import com.projectdelta.habbit.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class InsightsSharedViewModel @Inject constructor(
	private val repository: TasksRepositoryImpl
) : BaseViewModel() {

	val getAllDays = repository.getAllDays()

	val getAllDaysPaged = repository.getAllDaysDataPaged().cachedIn(viewModelScope)

	suspend fun getDayRange( start : Long , end : Long ) = repository.getDayRange(start, end)

}