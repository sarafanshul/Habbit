package com.projectdelta.habbit.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.projectdelta.habbit.data.local.TasksDao
import com.projectdelta.habbit.data.model.entities.Day
import com.projectdelta.habbit.data.model.entities.Task
import com.projectdelta.habbit.util.system.lang.TimeUtil
import kotlinx.coroutines.flow.Flow

class TasksRepositoryImpl( private val tasksDao: TasksDao) : TasksRepository{

    // ============ Task Table functions ===========

    override fun insertTask(task : Task) = tasksDao.insertTask( task )

    override fun insertDay(day: Day) = tasksDao.insertDay(day)

    override fun getAllTasks( ) : LiveData<List<Task>> = tasksDao.getAllTasks()

    fun getAllTasksOffline() : List<Task> = tasksDao.getAllTasksOffline()

    override fun getTaskById(id: Long): Task = tasksDao.getTaskById( id )

    fun getTaskByIdLive(id : Long ) = tasksDao.getTaskByIdLive( id )

    override suspend fun getTask(taskName : String ) : List<Task> = tasksDao.getTask( taskName )

    override fun updateTask(task : Task) = tasksDao.updateTask( task )

    override fun deleteTask(task : Task) = tasksDao.deleteTask( task )

    // ============ Day Table functions ===========

    override fun getAllDays() : Flow<List<Day>> = tasksDao.getAllDays()

    fun getAllDaysPaged() : PagingSource<Int , Day> = tasksDao.getAllDaysPaged()

    override suspend fun getDay(id : Long) = tasksDao.getDay(id)

    fun getAllDayOffline( ): List<Day> = tasksDao.getAllDaysOffline()

    override suspend fun getDayRange(start : Long, end : Long ) = tasksDao.getDayRange(start , end)

    override fun updateDay(day : Day) = tasksDao.updateDay(day)

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

    fun getAllDaysDataPaged() = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { getAllDaysPaged() }
    ).liveData


}