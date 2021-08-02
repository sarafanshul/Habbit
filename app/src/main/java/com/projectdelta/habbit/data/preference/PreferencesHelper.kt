package com.projectdelta.habbit.data.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.projectdelta.habbit.R
import dagger.Component
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.internal.ComponentEntryPoint
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class PreferencesHelper @Inject constructor(
    private val context: Context ,
    private val prefs : SharedPreferences
) {

    fun getAppTheme() = prefs.getString(context.resources.getString(R.string.id_app_theme) , context.resources.getString(R.string.theme_light) )

    fun getNotificationInterval() = prefs.getString(context.resources.getString( R.string.id_notification_interval ) , "")

    fun isNotificationEnabled() = prefs.getBoolean(context.resources.getString( R.string.id_notification_enabled ) , false)

}