package com.projectdelta.habbit.data.repository

import androidx.lifecycle.LiveData
import com.projectdelta.habbit.data.model.entities.Day
import com.projectdelta.habbit.data.model.entities.Task
import kotlinx.coroutines.flow.Flow

interface TasksRepository {

	fun insertTask(task: Task)

	fun insertDay(day: Day)

	fun getAllTasks(): LiveData<List<Task>>

	fun getAllDays(): Flow<List<Day>>

	fun getTaskById(id: Long): Task

	suspend fun getTask(taskName: String): List<Task>

	suspend fun getDay(id: Long): List<Day>

	suspend fun getDayRange(start: Long, end: Long): List<Day>

	fun updateTask(task: Task)

	fun updateDay(day: Day)

	fun deleteTask(task: Task)

}