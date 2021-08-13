package com.projectdelta.habbit.ui.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.projectdelta.habbit.ui.activity.InsightsActivity
import com.projectdelta.habbit.ui.setting.SettingsActivity
import com.projectdelta.habbit.util.constant.HELP_AND_FEEDBACK
import com.projectdelta.habbit.util.constant.URL_PORTFOLIO

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
	 * Opens about
	 */
	fun about( context: Context ){
		context.startActivity(Intent( Intent.ACTION_VIEW , Uri.parse(URL_PORTFOLIO) ))
	}

	/**
	 * Opens help
	 */
	fun helpAndFeedback( context: Context ){
		context.startActivity(Intent( Intent.ACTION_VIEW , Uri.parse(HELP_AND_FEEDBACK) ))
	}
}