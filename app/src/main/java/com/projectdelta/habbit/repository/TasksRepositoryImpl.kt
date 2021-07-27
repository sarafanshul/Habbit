package com.projectdelta.habbit.repository

import androidx.lifecycle.LiveData
import com.projectdelta.habbit.data.TasksDao
import com.projectdelta.habbit.data.entities.Day
import com.projectdelta.habbit.data.entities.Task
import com.projectdelta.habbit.util.lang.TimeUtil

class TasksRepositoryImpl( private val tasksDao: TasksDao) : TasksRepository{

    override fun insertTask(task : Task) = tasksDao.insertTask( task )

    override fun insertDay(day: Day) = tasksDao.insertDay(day)

    override fun getAllTasks( ) : LiveData<List<Task>> = tasksDao.getAllTasks()

    override fun getAllDays() : LiveData<List<Day>> = tasksDao.getAllDays()

    override fun getTaskById(id: Long): Task = tasksDao.getTaskById( id )

    override fun getTaskByIdLive(id : Long ) = tasksDao.getTaskByIdLive( id )

    override suspend fun getTask(taskName : String ) : List<Task> = tasksDao.getTask( taskName )

    override suspend fun getDay(id : Long) = tasksDao.getDay(id)

    override suspend fun getDayRange(start : Long, end : Long ) = tasksDao.getDayRange(start , end)

    override fun updateTask(task : Task) = tasksDao.updateTask( task )

    override fun updateDay(day : Day) = tasksDao.updateDay(day)

    override fun deleteTask(task : Task) = tasksDao.deleteTask( task )

    /**
     * Updates a task in task-database and add corresponding task id to day object and updates day-database
     */
    suspend fun markTaskCompleted( task: Task){
        val dayList = tasksDao.getDay(TimeUtil.getTodayFromEpoch())
        val day = when( dayList.size ) {
            0 -> Day(TimeUtil.getTodayFromEpoch(), mutableListOf(), mutableListOf())
            else -> dayList.first()
        }
        day.tasksID.add(task.id)
        day.tasksTitle.add(task.taskName)
        tasksDao.updateTask(task)
        tasksDao.insertDay(day)
    }
}