package com.projectdelta.habbit.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.projectdelta.habbit.data.model.entities.Day
import com.projectdelta.habbit.data.model.entities.Task
import com.projectdelta.habbit.util.constant.DatabaseConstants
import com.projectdelta.habbit.util.system.lang.Converters
import java.util.concurrent.Executors

@Database(
	entities = [
		Task::class,
		Day::class
	],
	version = 11,
	exportSchema = true
)
@TypeConverters(Converters::class)
abstract class TasksDatabase : RoomDatabase() {

	abstract fun tasksDao(): TasksDao

	companion object {
		// For Singleton instantiation
		@Volatile
		private var INSTANCE: TasksDatabase? = null

		fun getInstance(context: Context): TasksDatabase {
			val tempInstance = INSTANCE
			if (tempInstance != null)
				return tempInstance

			synchronized(this) {
				val instance = buildDatabase(context)
				INSTANCE = instance
				return instance
			}
		}

		private fun buildDatabase(context: Context): TasksDatabase {
			return Room.databaseBuilder(
				context.applicationContext,
				TasksDatabase::class.java,
				DatabaseConstants.DATABASE_NAME
			).addCallback(object : RoomDatabase.Callback() {
				override fun onCreate(db: SupportSQLiteDatabase) {
					super.onCreate(db)
					// pre-populate data
					// prepopulate works on a new thread so can return Null if database is not build while required
					Executors.newSingleThreadExecutor().execute {
						INSTANCE?.let {
							// Prepopulate data here if needed
						}
					}
				}
			}
			).fallbackToDestructiveMigration().build()
		}
	}

}