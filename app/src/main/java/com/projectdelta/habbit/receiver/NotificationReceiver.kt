package com.projectdelta.habbit.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.projectdelta.habbit.data.TasksDatabase
import com.projectdelta.habbit.data.entities.Task
import com.projectdelta.habbit.repository.TasksRepositoryImpl
import com.projectdelta.habbit.util.lang.TimeUtil
import com.projectdelta.habbit.util.lang.hash
import com.projectdelta.habbit.util.lang.notificationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.projectdelta.habbit.BuildConfig.APPLICATION_ID as ID

class NotificationReceiver : BroadcastReceiver() {

	override fun onReceive(context: Context , intent: Intent) {
		when( intent.action ){
			ACTION_TASK_COMPLETED -> notifyTaskCompleted( context , intent.getSerializableExtra("TASK") as Task )
			ACTION_TASK_SKIPPED -> notifyTaskSkipped( context , intent.getSerializableExtra("TASK") as Task )
		}
	}

	private fun notifyTaskSkipped(context: Context, task: Task) {
		task.skipTill = TimeUtil.getTodayFromEpoch()
		GlobalScope.launch { TasksDatabase.getInstance( context ).tasksDao().updateTask( task ) }
		dismissNotification( context , task.hash() )
	}

	private fun notifyTaskCompleted( context: Context , task: Task ) {
		task.lastDayCompleted.add( TimeUtil.getTodayFromEpoch() )
		GlobalScope.launch(Dispatchers.IO) {
			TasksRepositoryImpl(TasksDatabase.getInstance( context ).tasksDao())
				.markTaskCompleted( task )
		}
		dismissNotification( context , task.hash() )
	}

	private fun dismissNotification(context: Context, ID: Int) {
		context.notificationManager.cancel(ID)
	}

	companion object{
		private const val NAME = "NotificationReceiver"
		private const val ACTION_TASK_COMPLETED = "$ID.$NAME.TASK_COMPLETED"
		private const val ACTION_TASK_SKIPPED = "$ID.$NAME.TASK_SKIPPED"

		internal fun taskCompletedPendingBroadcast(
			context: Context,
			task: Task,
		): PendingIntent{
			val intent = Intent(context , NotificationReceiver::class.java).apply {
				action = ACTION_TASK_COMPLETED
				putExtra("TASK" , task)
			}
			return PendingIntent.getBroadcast(context , task.hash() , intent , PendingIntent.FLAG_UPDATE_CURRENT)
		}

		internal fun taskSkippedPendingBroadcast(
			context: Context,
			task: Task
		):PendingIntent{
			val intent = Intent(context , NotificationReceiver::class.java).apply {
				action = ACTION_TASK_SKIPPED
				putExtra("TASK" , task)
			}
			return PendingIntent.getBroadcast(context , task.hash() , intent , PendingIntent.FLAG_UPDATE_CURRENT)
		}

	}
}