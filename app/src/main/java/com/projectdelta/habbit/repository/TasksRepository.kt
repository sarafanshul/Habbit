package com.projectdelta.habbit.repository

import androidx.lifecycle.LiveData
import com.projectdelta.habbit.data.TasksDao
import com.projectdelta.habbit.data.entities.Day
import com.projectdelta.habbit.data.entities.Task
import com.projectdelta.habbit.util.lang.TimeUtil

class TasksRepository(private val tasksDao: TasksDao) {

	val getAllTasks : LiveData<List<Task>> = tasksDao.getAllTasks()

	fun insertTask(task : Task ) = tasksDao.insertTask( task )

	fun insertDay( day: Day ) = tasksDao.insertDay(day)

	fun getAllTasks( ) : LiveData<List<Task>> = tasksDao.getAllTasks()

	fun getAllDays() : LiveData<List<Day>> = tasksDao.getAllDays()

	fun getTaskById(id: Long): Task = tasksDao.getTaskById( id )

	suspend fun getTask( taskName : String ) : List<Task> = tasksDao.getTask( taskName )

	suspend fun getDay(id : Long) = tasksDao.getDay(id)

	fun updateTask( task : Task ) = tasksDao.updateTask( task )

	fun updateDay( day : Day ) = tasksDao.updateDay(day)

	fun deleteTask( task : Task ) = tasksDao.deleteTask( task )

	/**
	 * updates a task in task-database and add corresponding task id to day object and updates day-database
	 */
	suspend fun markTaskCompleted( task: Task ){
		val dayList = tasksDao.getDay(TimeUtil.getTodayFromEpoch())
		val day = when( dayList.size ){
			0 -> Day( TimeUtil.getTodayFromEpoch() , mutableListOf() )
			else -> dayList.first()
		}
		day.tasks.add(task.id)
		tasksDao.updateTask(task)
		tasksDao.insertDay(day)
	}

}