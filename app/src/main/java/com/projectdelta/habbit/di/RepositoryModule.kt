package com.projectdelta.habbit.di

import com.projectdelta.habbit.data.TasksDao
import com.projectdelta.habbit.repository.TasksRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

	@Singleton
	@Provides
	fun provideTasksRepository( dao: TasksDao ):TasksRepository{
		return TasksRepository(dao)
	}
}