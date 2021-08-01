package com.projectdelta.habbit.ui.base

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.projectdelta.habbit.R
import com.projectdelta.habbit.data.preference.PreferencesHelper
import javax.inject.Inject

abstract class BaseThemedActivity : AppCompatActivity() {

    @Inject
    lateinit var preferences : PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        applyAppTheme( preferences )
        super.onCreate(savedInstanceState, persistentState)
    }

    companion object{
        fun AppCompatActivity.applyAppTheme( preferences: PreferencesHelper ){
            val resIds = mutableListOf<Int>()
            when( preferences.getAppTheme() ){
                resources.getString(R.string.theme_light) -> {
                    resIds += R.style.Theme_Habbit_Light
                }
                resources.getString(R.string.theme_dark) -> {
                    resIds += R.style.Theme_Habbit_Dark
                }
                else -> {
                    throw Exception("Invalid theme found")
                }
            }
            resIds.forEach {
                setTheme( it )
            }
        }
    }

}