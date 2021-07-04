package com.projectdelta.habbit.di

import android.content.Context
import com.projectdelta.habbit.App
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * For component info [Refer](https://developer.android.com/training/dependency-injection/hilt-android#generated-components)
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

	@Singleton
	@Provides
	fun provideApplication( @ApplicationContext app : Context ) : App {
		return  app as App
	}


}