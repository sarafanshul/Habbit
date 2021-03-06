package com.projectdelta.habbit.ui.navigation

import android.content.Context
import android.content.Intent
import com.projectdelta.habbit.ui.insight.InsightsActivity
import com.projectdelta.habbit.ui.setting.SettingsActivity
import com.projectdelta.habbit.ui.web.WebViewActivity
import com.projectdelta.habbit.util.constant.HELP_AND_FEEDBACK
import com.projectdelta.habbit.util.constant.URL_PORTFOLIO

object NavigationUtil {

	/**
	 * Starts Insights activity
	 */
	fun insights(context: Context) =
		context.startActivity(Intent(context, InsightsActivity::class.java))

	/**
	 * Starts Settings activity
	 */
	fun settings(context: Context) =
		context.startActivity(Intent(context, SettingsActivity::class.java))

	/**
	 * Opens about
	 */
	fun about(context: Context) {
		WebViewActivity.newIntent(context, URL_PORTFOLIO, 101, "About Habbit").also {
			context.startActivity(it)
		}
	}

	/**
	 * Opens help
	 */
	fun helpAndFeedback(context: Context) {
		WebViewActivity.newIntent(context, HELP_AND_FEEDBACK, 101, "About Habbit").also {
			context.startActivity(it)
		}
	}
}