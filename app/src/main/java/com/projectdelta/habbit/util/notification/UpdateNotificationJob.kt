package com.projectdelta.habbit.util.notification

import android.content.Context
import android.util.Log
import androidx.work.*
import com.projectdelta.habbit.data.service.UpdateNotificationService
import com.projectdelta.habbit.util.notification.Notifications.DEFAULT_UPDATE_INTERVAL
import java.util.concurrent.TimeUnit

class UpdateNotificationJob (private val context: Context, workerParams: WorkerParameters) :
	Worker(context, workerParams) {

	override fun doWork(): Result {
		Log.d("WorkManager" , "doWork Called")
		return if( UpdateNotificationService.start(context) ){
			Result.success()
		}else {
			Result.failure()
		}
	}

	companion object{
		private const val FLEX_INTERVAL = 5L
		private const val TAG = "UpdateNotification"

		/**
		 * Creates a periodic unique job Or Cancels job by [TAG] if [prefInterval] = 0
		 * Defaults : Tag = [TAG] ,
		 * Flex Interval = [FLEX_INTERVAL] ,
		 * @param context  Context of the parent activity
		 * @param policy Type of Periodic Work policy [ExistingPeriodicWorkPolicy.REPLACE] or [ExistingPeriodicWorkPolicy.KEEP]
		 * @param prefInterval Scheduled interval between two jobs
		* */
		fun setupTask( context: Context , policy : ExistingPeriodicWorkPolicy , prefInterval : Long ?= null ){
			Log.d("WorkManager" , "Setup Task Called , Args = $policy , $prefInterval")

			val interval = prefInterval ?: DEFAULT_UPDATE_INTERVAL
			if( interval > 0 ){
				val request = PeriodicWorkRequestBuilder<UpdateNotificationJob>(
					interval , TimeUnit.HOURS ,
					FLEX_INTERVAL , TimeUnit.MINUTES
				)
					.addTag( TAG )
					.build()
				WorkManager.getInstance(context).enqueueUniquePeriodicWork( TAG , policy , request )
			}else {
				WorkManager.getInstance(context).cancelAllWorkByTag(TAG)
			}
		}
	}
}