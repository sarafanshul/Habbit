package com.projectdelta.habbit

import android.app.Application
import com.google.firebase.ktx.Firebase
import com.projectdelta.habbit.util.notification.Notifications
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class App : Application() {

	override fun onCreate() {
		super.onCreate()

		setupNotificationChannels()
		configureFirebase()
	}

	protected open fun configureFirebase() {

	}

	protected open fun setupNotificationChannels() {
		Notifications.setupNotificationChannels(this)
	}
}