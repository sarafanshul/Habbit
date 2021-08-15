package com.projectdelta.habbit.data.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.projectdelta.habbit.data.model.entities.Day
import com.projectdelta.habbit.data.model.entities.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDao {

	@Insert( onConflict = OnConflictStrategy.REPLACE )
	fun insertTask(task : Task )

	@Insert( onConflict = OnConflictStrategy.REPLACE )
	fun insertDay(day : Day)

	@Query("SELECT * FROM task")
	fun getAllTasks( ) : LiveData<List<Task>>

	@Query("SELECT * FROM task")
	fun getAllTasksOffline( ) : List<Task>

	@Query("SELECT * FROM day ORDER BY id ASC")
	fun getAllDays() : Flow<List<Day>>

	@Query("SELECT * FROM day ORDER BY id DESC")
	fun getAllDaysPaged() : PagingSource<Int , Day>

	@Query("SELECT * FROM day")
	fun getAllDaysOffline() : List<Day>

	@Query( "SELECT * FROM task WHERE taskName = :taskName")
	suspend fun getTask( taskName : String ) : List<Task>

	@Query( "SELECT * FROM task WHERE id = :id")
	fun getTaskById( id : Long ) : Task

	@Query( "SELECT * FROM task WHERE id = :id")
	fun getTaskByIdLive( id : Long ) : LiveData<List<Task>>

	@Query("SELECT * FROM day WHERE id = :id")
	suspend fun getDay( id : Long ) : List<Day>

	/**
	 * Returns [Day] in Range [start , end]
	 * @param start inclusive
	 * @param end inclusive
	 * @return [List]
	 */
	@Query( "SELECT * FROM Day WHERE ID BETWEEN :start AND :end" )
	suspend fun getDayRange( start : Long , end : Long ) : List<Day>

	@Update
	fun updateTask( task : Task )

	@Update
	fun updateDay( day : Day )

	@Delete
	fun deleteTask( task : Task )

}