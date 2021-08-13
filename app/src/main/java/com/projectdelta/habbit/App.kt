package com.projectdelta.habbit

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.projectdelta.habbit.data.preference.PreferencesHelper
import com.projectdelta.habbit.util.notification.Notifications
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
open class App : Application() {

	@Inject
	lateinit var preferencesHelper : PreferencesHelper

	override fun onCreate() {
		super.onCreate()
		setupNotificationChannels()
		configureFirebase()
//		configureThemeMode()
	}

	/**
	 * Does not work remove or make it work
	 */
	private fun configureThemeMode() {
		if( preferencesHelper.isThemeDark() ){
			Log.d("App", "configureThemeMode: Night")
			AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_YES )
		}
		else {
			AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_NO )
		}
	}

	protected open fun configureFirebase() {

	}

	protected open fun setupNotificationChannels() {
		Notifications.setupNotificationChannels(this)
	}
}