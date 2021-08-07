package com.projectdelta.habbit.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.projectdelta.habbit.data.entities.Task
import com.projectdelta.habbit.util.lang.TimeUtil

abstract class BaseViewModel : ViewModel() {

	fun getTodayFromEpoch( ) = TimeUtil.getTodayFromEpoch()

	@Suppress("SpellCheckingInspection")
	fun getMSfromEpoch( ) = TimeUtil.getMSfromEpoch()

	fun getMSFromMidnight() = TimeUtil.getMSfromMidnight()

}