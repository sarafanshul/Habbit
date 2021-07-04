package com.projectdelta.habbit.util.view

import android.content.Context
import android.content.Intent
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
	 * Starts About activity
	 */
	fun about( context: Context ){

	}
}