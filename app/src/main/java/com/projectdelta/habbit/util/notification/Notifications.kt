package com.projectdelta.habbit.util.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object Notifications {

	@Suppress("unused")
	var pendingIntentRequestCode: Int =
		0 // increment pendingIntentRequestCode for new pending intent
	const val CHANNEL_ID = "Habbit_N1"
	const val CHANNEL_UPDATE = "Habbit_U0"
	const val CHANNEL_NAME = "Habbit"
	const val GROUP_KEY = "HABBIT_NOTIFICATION_GROUPx01"
	const val ID_UPDATES = 201
	const val DEFAULT_UPDATE_INTERVAL = 1L
	const val ID_UPDATE_PROGRESS = 301

	@Suppress("SpellCheckingInspection")
	const val NOTIF_TITLE_MAX_LEN = 45

	fun setupNotificationChannels(context: Context) {
		createUpdateNotificationChannel(context)
	}

	private fun createUpdateNotificationChannel(context: Context) {
		val channel = NotificationChannel(
			CHANNEL_UPDATE,
			CHANNEL_NAME,
			NotificationManager.IMPORTANCE_LOW
		).apply {
			enableLights(true)
			description = ""
		}
		val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		manager.createNotificationChannel(channel)
	}
}