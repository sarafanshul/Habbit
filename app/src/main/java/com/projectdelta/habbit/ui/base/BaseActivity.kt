package com.projectdelta.habbit.ui.base

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.projectdelta.habbit.R
import com.projectdelta.habbit.data.preference.PreferencesHelper
import com.projectdelta.habbit.di.PreferenceModule
import dagger.hilt.android.EntryPointAccessors


abstract class BaseActivity : AppCompatActivity() {

    /**
     * Injects dependencies in classes not supported by Hilt
     * [Refer](https://developer.android.com/training/dependency-injection/hilt-android#not-supported)
     */
    private val hiltEntryPoint : PreferenceModule.PreferenceHelperProviderEntryPoint by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            PreferenceModule.PreferenceHelperProviderEntryPoint::class.java
        )
    }

    protected lateinit var preferences: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {

        preferences = hiltEntryPoint.preferenceHelper()

        applyAppTheme( preferences )
        Log.d("BaseActivity", "onCreate: $preferences")
        super.onCreate(savedInstanceState)
    }

    companion object{
        fun AppCompatActivity.applyAppTheme( preferences: PreferencesHelper ){
            Log.d("BaseActivity", "applyAppTheme get: ${preferences.getAppTheme()}")
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