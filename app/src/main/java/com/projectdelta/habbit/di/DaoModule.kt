package com.projectdelta.habbit.di

import android.app.Application
import com.projectdelta.habbit.data.TasksDao
import com.projectdelta.habbit.data.TasksDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

	@Singleton
	@Provides
	fun provideTaskDao( application: Application ) : TasksDao{
		return TasksDatabase.getInstance( application ).tasksDao()
	}
}