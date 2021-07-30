package com.projectdelta.habbit.ui.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.projectdelta.habbit.constant.URL_PORTFOLIO
import com.projectdelta.habbit.ui.activity.InsightsActivity
import com.projectdelta.habbit.ui.setting.SettingsActivity

object NavigationUtil {

	/**
	 * Starts Insights activity
	 */
	fun insights( context: Context ) = context.startActivity( Intent( context , InsightsActivity::class.java ) )

	/**
	 * Starts Settings activity
	 */
	fun settings( context: Context ) = context.startActivity( Intent( context , SettingsActivity::class.java ) )

	/**
	 * Opens portfolio
	 */
	fun about( context: Context ){
		context.startActivity(Intent( Intent.ACTION_VIEW , Uri.parse(URL_PORTFOLIO) ))
	}
}