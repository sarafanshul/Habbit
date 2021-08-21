package com.projectdelta.habbit.di

import android.app.Application
import com.projectdelta.habbit.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DrawableModule {

	@Singleton
	@Provides
	@Named("COLORS_ARRAY")
	fun provideColorArray(application: Application): IntArray {
		return application.resources.getIntArray(R.array.calendar_color)
	}
}