package com.projectdelta.habbit.ui.base

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.projectdelta.habbit.R
import com.projectdelta.habbit.data.preference.PreferencesHelper
import com.projectdelta.habbit.di.PreferenceModule
import dagger.Component
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject


abstract class BaseThemedActivity : AppCompatActivity() {

    val preferences : PreferencesHelper by lazy {
        PreferencesHelper(
            applicationContext,
            getSharedPreferences(application.packageName + "_preferences", Context.MODE_PRIVATE)
        )
    }
        override fun onCreate(savedInstanceState: Bundle?) {
            applyAppTheme( preferences )
            Log.d("BaseThemedActivity", "onCreate: $preferences")
            super.onCreate(savedInstanceState)
        }

        companion object{
        fun AppCompatActivity.applyAppTheme( preferences: PreferencesHelper ){
            Log.d("BaseThemedActivity", "applyAppTheme get: ${preferences.getAppTheme()}")
            when( preferences.getAppTheme() ){
                resources.getString(R.string.theme_light) -> {
                    AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_NO )
                    setTheme( R.style.Theme_Habbit_Light )
                }
                resources.getString(R.string.theme_dark) -> {
                    AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_YES )
                    setTheme( R.style.Theme_Habbit_Dark )
                }
                else -> {
                    throw Exception("Invalid theme found")
                }
            }

        }
    }

}