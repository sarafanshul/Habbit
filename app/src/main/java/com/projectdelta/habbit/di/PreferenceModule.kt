package com.projectdelta.habbit.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.projectdelta.habbit.data.preference.PreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {

    @Singleton
    @Provides
    fun providesPreferenceHelper( application: Application , prefs : SharedPreferences ) : PreferencesHelper {
        return PreferencesHelper(application , prefs )
    }

    @Singleton
    @Provides
    fun providesSharedPreference(application: Application) : SharedPreferences {
        return application.getSharedPreferences(application.packageName + "_preferences", Context.MODE_PRIVATE)
    }

    @EntryPoint
    @Singleton
    @InstallIn(SingletonComponent::class)
    interface PreferenceHelperProviderEntryPoint{
        fun preferenceHelper() : PreferencesHelper
    }
}