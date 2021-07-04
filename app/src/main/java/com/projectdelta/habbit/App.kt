package com.projectdelta.habbit

import android.app.Application
import com.projectdelta.habbit.util.notification.Notifications
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class App : Application() {

	override fun onCreate() {
		super.onCreate()

		setupNotificationChannels()

	}

	protected open fun setupNotificationChannels() {
		Notifications.setupNotificationChannels(this)
	}
}