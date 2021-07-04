package com.projectdelta.habbit.ui.setting

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.work.ExistingPeriodicWorkPolicy
import com.projectdelta.habbit.R
import com.projectdelta.habbit.databinding.SettingsActivityBinding
import com.projectdelta.habbit.util.notification.UpdateNotificationJob
import com.projectdelta.habbit.util.notification.Notifications.DEFAULT_UPDATE_INTERVAL

class SettingsActivity : AppCompatActivity() ,
	SharedPreferences.OnSharedPreferenceChangeListener {

	lateinit var binding : SettingsActivityBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = SettingsActivityBinding.inflate( layoutInflater )

		setContentView( binding.root )

		if (savedInstanceState == null) {
			supportFragmentManager
				.beginTransaction()
				.replace(binding.settings.id , SettingsFragment() )
				.commit()
		}
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}

	override fun onResume() {
		super.onResume()
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
	}

	override fun onPause() {
		super.onPause()
		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
	}

	override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
		when( key ){
			"NotificationsEnabled" -> {
				if( sharedPreferences?.getBoolean("NotificationsEnabled" , false)!! ){
					val interval = when(sharedPreferences.getString("NotificationInterval" , "")){
						"1 Hour" -> 1L
						"3 Hours" -> 3L
						"6 Hours" -> 6L
						"12 Hours" -> 12L
						else ->  DEFAULT_UPDATE_INTERVAL
					}
					UpdateNotificationJob.setupTask( this , ExistingPeriodicWorkPolicy.REPLACE , interval )
				}
				else {
					UpdateNotificationJob.setupTask( this , ExistingPeriodicWorkPolicy.REPLACE , 0L )
				}
			}
			"NotificationInterval" -> {
				val interval = when(sharedPreferences?.getString("NotificationInterval" , "")){
					"1 Hour" -> 1L
					"3 Hours" -> 3L
					"6 Hours" -> 6L
					"12 Hours" -> 12L
					else -> DEFAULT_UPDATE_INTERVAL
				}
				UpdateNotificationJob.setupTask( this , ExistingPeriodicWorkPolicy.REPLACE , interval )
			}
			else -> {}
		}
	}

	class SettingsFragment : PreferenceFragmentCompat() {
		override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
			setPreferencesFromResource(R.xml.root_preferences, rootKey)
		}
	}
}