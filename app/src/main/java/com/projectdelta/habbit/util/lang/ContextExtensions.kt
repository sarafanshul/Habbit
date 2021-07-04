package com.projectdelta.habbit.util.lang

import android.app.ActivityManager
import android.app.KeyguardManager
import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.ConnectivityManager
import android.os.PowerManager
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * Display a toast in this context.
 *
 * @param resource the text resource.
 * @param duration the duration of the toast. Defaults to short.
 */
fun Context.toast(@StringRes resource: Int, duration: Int = Toast.LENGTH_SHORT, block: (Toast) -> Unit = {}): Toast {
	return toast(getString(resource), duration, block)
}

/**
 * Display a toast in this context.
 *
 * @param text the text to display.
 * @param duration the duration of the toast. Defaults to short.
 */
fun Context.toast(text: String?, duration: Int = Toast.LENGTH_SHORT, block: (Toast) -> Unit = {}): Toast {
	return Toast.makeText(this, text.orEmpty(), duration).also {
		block(it)
		it.show()
	}
}

/**
 * Helper method to create a notification builder.
 *
 * @param id the channel id.
 * @param block the function that will execute inside the builder.
 * @return a notification to be displayed or updated.
 */
fun Context.notificationBuilder(channelId: String, block: (NotificationCompat.Builder.() -> Unit)? = null): NotificationCompat.Builder {
	val builder = NotificationCompat.Builder(this, channelId)
	if (block != null) {
		builder.block()
	}
	return builder
}

/**
 * Helper method to create a notification.
 *
 * @param id the channel id.
 * @param block the function that will execute inside the builder.
 * @return a notification to be displayed or updated.
 */
fun Context.notification(channelId: String, block: (NotificationCompat.Builder.() -> Unit)?): Notification {
	val builder = notificationBuilder(channelId, block)
	return builder.build()
}

/**
 * Checks if the give permission is granted.
 *
 * @param permission the permission to check.
 * @return true if it has permissions.
 */
fun Context.hasPermission(permission: String) = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

/**
 * Converts to dp.
 */
val Int.pxToDp: Int
	get() = (this / Resources.getSystem().displayMetrics.density).toInt()

/**
 * Converts to px.
 */
val Int.dpToPx: Float
	get() = (this * Resources.getSystem().displayMetrics.density)

/**
 * Converts to px and takes into account LTR/RTL layout.
 */
val Float.dpToPxEnd: Float
	get() = (
			this * Resources.getSystem().displayMetrics.density *
					if (Resources.getSystem().isLTR) 1 else -1
			)


val Resources.isLTR
	get() = configuration.layoutDirection == View.LAYOUT_DIRECTION_LTR

val Context.notificationManager: NotificationManager
	get() = getSystemService()!!

val Context.connectivityManager: ConnectivityManager
	get() = getSystemService()!!

val Context.powerManager: PowerManager
	get() = getSystemService()!!

val Context.keyguardManager: KeyguardManager
	get() = getSystemService()!!

/**
 * Convenience method to acquire a partial wake lock.
 */
fun Context.acquireWakeLock(tag: String): PowerManager.WakeLock {
	val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "$tag:WakeLock")
	wakeLock.acquire()
	return wakeLock
}

/**
 * Function used to send a local broadcast asynchronous
 *
 * @param intent intent that contains broadcast information
 */
fun Context.sendLocalBroadcast(intent: Intent) {
	LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
}

/**
 * Function used to send a local broadcast synchronous
 *
 * @param intent intent that contains broadcast information
 */
fun Context.sendLocalBroadcastSync(intent: Intent) {
	LocalBroadcastManager.getInstance(this).sendBroadcastSync(intent)
}

/**
 * Function used to register local broadcast
 *
 * @param receiver receiver that gets registered.
 */
fun Context.registerLocalReceiver(receiver: BroadcastReceiver, filter: IntentFilter) {
	LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
}

/**
 * Function used to unregister local broadcast
 *
 * @param receiver receiver that gets unregistered.
 */
fun Context.unregisterLocalReceiver(receiver: BroadcastReceiver) {
	LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
}

/**
 * Returns true if the given service class is running.
 */
fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
	val className = serviceClass.name
	val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
	@Suppress("DEPRECATION")
	return manager.getRunningServices(Integer.MAX_VALUE)
		.any { className == it.service.className }
}