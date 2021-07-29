package com.projectdelta.habbit.repository

import androidx.lifecycle.LiveData
import com.projectdelta.habbit.data.TasksDao
import com.projectdelta.habbit.data.entities.Day
import com.projectdelta.habbit.data.entities.Task
import com.projectdelta.habbit.util.lang.TimeUtil

interface TasksRepository {

	fun insertTask(task : Task )

	fun insertDay( day: Day )

	fun getAllTasks( ): LiveData<List<Task>>

	fun getAllDays(): LiveData<List<Day>>

	fun getTaskById(id: Long): Task

	suspend fun getTask( taskName : String ): List<Task>

	suspend fun getDay(id : Long): List<Day>

	suspend fun getDayRange( start : Long , end : Long ): List<Day>

	fun updateTask( task : Task )

	fun updateDay( day : Day )

	fun deleteTask( task : Task )

}