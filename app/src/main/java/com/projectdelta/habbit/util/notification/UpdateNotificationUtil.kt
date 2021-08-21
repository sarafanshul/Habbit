package com.projectdelta.habbit.util.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.projectdelta.habbit.R
import com.projectdelta.habbit.data.model.entities.Task
import com.projectdelta.habbit.data.receiver.NotificationReceiver
import com.projectdelta.habbit.ui.main.MainActivity
import com.projectdelta.habbit.util.notification.Notifications.CHANNEL_UPDATE
import com.projectdelta.habbit.util.notification.Notifications.GROUP_KEY
import com.projectdelta.habbit.util.notification.Notifications.ID_UPDATES
import com.projectdelta.habbit.util.notification.Notifications.NOTIF_TITLE_MAX_LEN
import com.projectdelta.habbit.util.system.lang.chop
import com.projectdelta.habbit.util.system.lang.hash

object UpdateNotificationUtil {

	/**
	 * Creates a empty foreground notification
	 * @param mContext parent context
	 * @return Notification.builder object
	 */
	fun foregroundUpdateNotification(mContext: Context): NotificationCompat.Builder {
		return NotificationCompat.Builder(mContext, CHANNEL_UPDATE).apply {
			setContentTitle(mContext.getString(R.string.app_name))
			setSmallIcon(getIcon())
			setOngoing(true)
			setOnlyAlertOnce(true)
		}
	}

	/**
	 * Creates a group of notifications with given data
	 * @param mContext parent context
	 * @param data list of tasks to show notifications of
	 */
	fun showUpdateNotification(mContext: Context, data: List<Task>, NOTIFICATION_ID: Int) {
		if (data.isEmpty()) return  // don't fire for empty tasks

		with(NotificationManagerCompat.from(mContext)) {
			notify(
				ID_UPDATES,
				NotificationCompat.Builder(mContext, CHANNEL_UPDATE).apply {
					setContentTitle("Incomplete tasks")
					setContentText("${data.size} pending tasks for today!")
					setSmallIcon(getIcon())
					setLargeIcon(BitmapFactory.decodeResource(mContext.resources, getIcon()))
					setStyle(
						NotificationCompat.BigTextStyle().bigText(
							data.joinToString("\n") {
								it.taskName.chop(NOTIF_TITLE_MAX_LEN)
							}
						)
					)
					setGroupSummary(true)
					setContentIntent(getNotificationIntent(mContext))
					setGroup(GROUP_KEY)
					setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
					priority = NotificationCompat.PRIORITY_HIGH
					setAutoCancel(true)
				}.build()
			)
			data.forEach {
				notify(it.hash(), createNewDataNotification(mContext, it))
			}
		}
	}

	/**
	 * Creates a notification for a single task
	 * @param mContext context of parent
	 * @param task task to show notification of
	 * @return Notification object
	 * */
	private fun createNewDataNotification(mContext: Context, task: Task): Notification {
		return NotificationCompat.Builder(mContext, CHANNEL_UPDATE).apply {
			setContentTitle(task.taskName)
			setContentText(task.summary)

			setStyle(
				NotificationCompat.BigTextStyle().bigText(task.summary.chop(NOTIF_TITLE_MAX_LEN))
			)

			setSmallIcon(getIcon())
			setLargeIcon(BitmapFactory.decodeResource(mContext.resources, getIcon()))
			setGroup(GROUP_KEY)
			setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
			setContentIntent(getNotificationIntent(mContext))
			priority = NotificationCompat.PRIORITY_HIGH
			setAutoCancel(true)
			addAction(
				R.drawable.ic_done_black_24dp,
				"Completed!",
				NotificationReceiver.taskCompletedPendingBroadcast(
					mContext,
					task,
				)
			)
			addAction(
				R.drawable.ic_skip_next_black_24dp,
				"Skip?",
				NotificationReceiver.taskSkippedPendingBroadcast(
					mContext,
					task
				)
			)

		}.build()
	}

	private fun getIcon(): Int {
		return R.drawable.ic_kanji_gold_notification_new
	}

	private fun getNotificationIntent(mContext: Context): PendingIntent {
		val intent = Intent(mContext, MainActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
		}
		return PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
	}

}