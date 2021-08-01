package com.projectdelta.habbit.data.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.projectdelta.habbit.R
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class PreferencesHelper @Inject constructor(
    private val context: Context
) {

    @Inject
    lateinit var prefs : SharedPreferences

    fun getAppTheme() = prefs.getString(context.resources.getString(R.string.id_app_theme) , context.resources.getString(R.string.theme_light) )

    fun getNotificationInterval() = prefs.getString(context.resources.getString( R.string.id_notification_interval ) , "")

    fun isNotificationEnabled() = prefs.getBoolean(context.resources.getString( R.string.id_notification_enabled ) , false)

}