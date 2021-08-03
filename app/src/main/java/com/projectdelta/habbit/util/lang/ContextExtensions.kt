package com.projectdelta.habbit.util.lang

import android.annotation.SuppressLint
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
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.PowerManager
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.projectdelta.habbit.R

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
 * Display a Dark toast in this context.
 *
 * @param text the text to display.
 * @param duration the duration of the toast. Defaults to short.
 */
@SuppressLint("ResourceAsColor")
fun Context.darkToast(text: String?, duration: Int = Toast.LENGTH_SHORT, block: (Toast) -> Unit = {}): Toast {
	return Toast.makeText(this, text.orEmpty(), duration).apply {
		view?.background?.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN)
		view?.findViewById<TextView>(android.R.id.message)?.setTextColor(Color.WHITE)
	}.also {
		block(it)
		it.show()
	}
}

/**
 * Display a Customizable toast in this context.
 *
 * @param text the text to display.
 * @param duration the duration of the toast. Defaults to short.
 */
fun Context.customToast(text: String?,
	duration: Int = Toast.LENGTH_SHORT,
	background : Int = Color.GREEN,
	textColor : Int = Color.RED,
	block: (Toast) -> Unit = {}
): Toast {
	return Toast.makeText(this, text.orEmpty(), duration).apply {
		view?.background?.setColorFilter(background, PorterDuff.Mode.SRC_IN)
		view?.findViewById<TextView>(android.R.id.message)?.setTextColor(textColor)
	}.also {
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

/**
 * Returns if a network connection is available or not. [For more info](https://stackoverflow.com/a/58605532)
 */
@SuppressLint("ObsoleteSdkInt")
fun Context.isOnline(): Boolean {
	val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		val n = cm.activeNetwork
		if (n != null) {
			val nc = cm.getNetworkCapabilities(n)
			//It will check for both wifi and cellular network
			return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
		}
		return false
	} else {
		val netInfo = cm.activeNetworkInfo
		return netInfo != null && netInfo.isConnectedOrConnecting
	}
}

/**
 * Returns the theme resources
 */

fun Context.getColorFromAttr(
	@AttrRes attrColor : Int ,
	typedValue: TypedValue = TypedValue(),
	resolveRefs: Boolean = true
) : Int{
	theme.resolveAttribute(attrColor , typedValue , resolveRefs)
	return typedValue.resourceId
}