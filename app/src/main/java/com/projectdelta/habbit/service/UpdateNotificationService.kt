package com.projectdelta.habbit.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.projectdelta.habbit.data.TasksDatabase
import com.projectdelta.habbit.util.*
import com.projectdelta.habbit.util.lang.*
import com.projectdelta.habbit.util.notification.Notifications
import com.projectdelta.habbit.util.notification.UpdateNotificationUtil
import kotlinx.coroutines.*


class UpdateNotificationService(
): Service() {

	private lateinit var wakeLock: PowerManager.WakeLock
	private lateinit var ioScope: CoroutineScope

	private var updateJob : Job?= null

	companion object{

		private var instance : UpdateNotificationService? = null
		private const val DEFAULT_NOTIFICATION_ID : Int = 101

		/**
		 * Returns the status of the service.
		 *
		 * @param context the application context.
		 * @return true if the service is running, false otherwise.
		 */
		fun isRunning(context: Context): Boolean {
			return context.isServiceRunning(UpdateNotificationService::class.java)
		}

		/**
		 * Stops the service.
		 *
		 * @param context the application context.
		 */
		fun stop(context: Context) {
			context.stopService(Intent(context, UpdateNotificationService::class.java))
		}

		/**
		 * Starts the service. It will be started only if there isn't another instance already
		 * running.
		 *
		 * @param context the application context.
		 * @return true if service newly started, false otherwise
		 */
		fun start(context: Context ): Boolean {
			Log.d("UpdateNotificationService" , "Service Started")
			return if (!isRunning(context)) {
				val intent = Intent(context, UpdateNotificationService::class.java)
				ContextCompat.startForegroundService(context, intent)
				true
			} else {
				false
			}
		}
	}

	/**
	 * Method called when the service is created. It injects dagger dependencies and acquire
	 * the wake lock.
	 */
	override fun onCreate() {
		super.onCreate()

		ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
		wakeLock = acquireWakeLock(javaClass.name)

		startForeground(Notifications.ID_UPDATE_PROGRESS , UpdateNotificationUtil.foregroundUpdateNotification(this).build())
	}

	/**
	 * Method called when the service is destroyed. It destroys subscriptions and releases the wake
	 * lock.
	 */
	override fun onDestroy() {
		Log.d("UpdateNotificationService" , "onDestroy Called")
		updateJob?.cancel()
		ioScope?.cancel()
		if (wakeLock.isHeld) {
			wakeLock.release()
		}
		instance = null
		super.onDestroy()
	}

	/**
	 * This method needs to be implemented, but it's not used/needed.
	 */
	override fun onBind(intent: Intent?): IBinder? {
		return null
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		Log.d("UpdateNotificationService" , "onStartCommand 1")
		if (intent == null) return START_NOT_STICKY
		Log.d("UpdateNotificationService" , "onStartCommand 2")
		instance = this

		// Unsubscribe from any previous subscription if needed
		updateJob?.cancel()

		// Destroy service when completed or in case of an error.
		val handler = CoroutineExceptionHandler { _, exception ->
			Log.e("CoroutineExceptionHandler Error" ,exception.message!!)
			stopSelf(startId)
		}

		updateJob = ioScope.launch(handler) {
			updateTaskList( this@UpdateNotificationService )
		}

		updateJob?.invokeOnCompletion { stopSelf(startId) }

		return START_REDELIVER_INTENT
	}

	/**
	 * Creates notification in foreground service with given constraints
	 */
	private suspend fun updateTaskList( mContext: Context ) {

		val db = TasksDatabase.getInstance(mContext).tasksDao()

		Log.d("UpdateNotificationService", "updateTaskList")
		val handler = CoroutineExceptionHandler { _, exception ->
			Log.e("CoroutineExceptionHandler Exception", exception.message!!)
		}

		GlobalScope.launch {
			val data = async { db.getAllTasksOffline() }
			try {
			UpdateNotificationUtil.showUpdateNotification(
				this@UpdateNotificationService,
				data.await()
					.tasksBeforeSkipTime(TimeUtil.getMSfromMidnight())
					.unfinishedNotifyTill(TimeUtil.getTodayFromEpoch()),
				DEFAULT_NOTIFICATION_ID
				)
			}catch ( e : Exception ){
				Log.e("Exception Sending Notification" , e.message!! )
			}
		}
	}

}