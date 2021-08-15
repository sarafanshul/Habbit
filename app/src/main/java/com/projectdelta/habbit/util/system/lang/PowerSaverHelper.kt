package com.projectdelta.habbit.util.system.lang

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat


/**
 * Helper util for requesting WhiteList from battery optimizations and doze mode
 *
 * Manifest : `<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />`
 *
 * Usage : `PowerSaverHelper.prepareIntentForWhiteListingOfBatteryOptimization(this)?.let { startActivity(it) }`
 *
 * [Refer](https://stackoverflow.com/a/60089847)
 */
object PowerSaverHelper {
    enum class WhiteListedInBatteryOptimizations {
        WHITE_LISTED, NOT_WHITE_LISTED, ERROR_GETTING_STATE, IRRELEVANT_OLD_ANDROID_API
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun getIfAppIsWhiteListedFromBatteryOptimizations(context: Context, packageName: String = context.packageName): WhiteListedInBatteryOptimizations {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return WhiteListedInBatteryOptimizations.IRRELEVANT_OLD_ANDROID_API
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager?
            ?: return WhiteListedInBatteryOptimizations.ERROR_GETTING_STATE
        return if (pm.isIgnoringBatteryOptimizations(packageName)) WhiteListedInBatteryOptimizations.WHITE_LISTED else WhiteListedInBatteryOptimizations.NOT_WHITE_LISTED
    }

    //@TargetApi(VERSION_CODES.M)
    @SuppressLint("BatteryLife", "InlinedApi", "ObsoleteSdkInt")
    @RequiresPermission(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
    fun prepareIntentForWhiteListingOfBatteryOptimization(context: Context, packageName: String = context.packageName, alsoWhenWhiteListed: Boolean = false): Intent? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return null
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) == PackageManager.PERMISSION_DENIED)
            return null
        val appIsWhiteListedFromPowerSave: WhiteListedInBatteryOptimizations = getIfAppIsWhiteListedFromBatteryOptimizations(context, packageName)
        var intent: Intent? = null
        when (appIsWhiteListedFromPowerSave) {
            WhiteListedInBatteryOptimizations.WHITE_LISTED -> if (alsoWhenWhiteListed) intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            WhiteListedInBatteryOptimizations.NOT_WHITE_LISTED -> intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).setData(Uri.parse("package:$packageName"))
            WhiteListedInBatteryOptimizations.ERROR_GETTING_STATE, WhiteListedInBatteryOptimizations.IRRELEVANT_OLD_ANDROID_API -> {
            }
        }
        return intent
    }
}