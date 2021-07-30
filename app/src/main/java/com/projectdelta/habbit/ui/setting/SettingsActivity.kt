package com.projectdelta.habbit.ui.setting

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.work.ExistingPeriodicWorkPolicy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.projectdelta.habbit.R
import com.projectdelta.habbit.databinding.SettingsActivityBinding
import com.projectdelta.habbit.util.SyncUtil
import com.projectdelta.habbit.util.database.DatabaseUtil
import com.projectdelta.habbit.util.lang.darkToast
import com.projectdelta.habbit.util.lang.isOnline
import com.projectdelta.habbit.util.lang.toast
import com.projectdelta.habbit.util.notification.UpdateNotificationJob
import com.projectdelta.habbit.util.notification.Notifications.DEFAULT_UPDATE_INTERVAL
import com.projectdelta.habbit.util.view.CustomTextPreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() ,
	SharedPreferences.OnSharedPreferenceChangeListener {

	private var _binding : SettingsActivityBinding ?= null
	private val binding : SettingsActivityBinding
		get() = _binding!!


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		_binding = SettingsActivityBinding.inflate( layoutInflater )

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

	override fun onDestroy() {
		super.onDestroy()
		_binding = null
	}

	override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
		when (key) {
			resources.getString(R.string.id_notification_enabled) -> {
				if (sharedPreferences?.getBoolean(
						resources.getString(R.string.id_notification_enabled),
						false
					)!!
				) {
					val interval = when (sharedPreferences.getString(
						resources.getString(R.string.id_notification_interval),
						""
					)) {
						"1 Hour" -> 1L
						"3 Hours" -> 3L
						"6 Hours" -> 6L
						"12 Hours" -> 12L
						else -> DEFAULT_UPDATE_INTERVAL
					}
					UpdateNotificationJob.setupTask(
						this,
						ExistingPeriodicWorkPolicy.REPLACE,
						interval
					)
				} else {
					UpdateNotificationJob.setupTask(this, ExistingPeriodicWorkPolicy.REPLACE, 0L)
				}
			}
			resources.getString(R.string.id_notification_interval) -> {
				val interval = when (sharedPreferences?.getString(
					resources.getString(R.string.id_notification_interval),
					""
				)) {
					"1 Hour" -> 1L
					"3 Hours" -> 3L
					"6 Hours" -> 6L
					"12 Hours" -> 12L
					else -> DEFAULT_UPDATE_INTERVAL
				}
				UpdateNotificationJob.setupTask(this, ExistingPeriodicWorkPolicy.REPLACE, interval)
			}
			resources.getString(R.string.id_sync_on_startup) -> {
				this.toast("Coming Soon...")
				sharedPreferences?.edit()
					?.putBoolean(resources.getString(R.string.id_sync_on_startup), false)?.apply()
			}
		}
	}

	@AndroidEntryPoint
	class SettingsFragment : PreferenceFragmentCompat() {

		@Inject lateinit var syncUtil : SyncUtil
		private val databaseUtil : DatabaseUtil = DatabaseUtil()

		override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
			setPreferencesFromResource(R.xml.root_preferences, rootKey)

			toggleCloudPreferences()

			findPreference<Preference>(resources.getString(R.string.id_sync_now))?.setOnPreferenceClickListener { _ ->
				requireActivity().let { syncUtil.syncNow(it) }
				true
			}

			findPreference<CustomTextPreference>(resources.getString(R.string.id_sign_out))?.setOnPreferenceClickListener { _ ->
				if( Firebase.auth.currentUser != null )
					Firebase.auth.signOut()
				toggleCloudPreferences()
				true
			}

			findPreference<CustomTextPreference>(resources.getString(R.string.id_delete_all_data_cloud))?.setOnPreferenceClickListener { _ ->
				requireActivity().let {
					if (it.isOnline()) {
						MaterialAlertDialogBuilder(it).apply {
							setTitle("Are you sure! , This is an irreversible process.")
							setPositiveButton("Delete") { _, _ -> databaseUtil.nukeCloud(it) }
							create()
						}.show()
					} else {
						it.darkToast("No network connection available")
					}
				}
				true
			}

			findPreference<CustomTextPreference>(resources.getString(R.string.id_delete_all_data_local))?.setOnPreferenceClickListener { _ ->
				requireActivity().let {
					MaterialAlertDialogBuilder(it).apply {
						setTitle("Are you sure! , This is an irreversible process.")
						setPositiveButton("Delete") { _, _ -> databaseUtil.nukeLocal(it) }
						create()
					}.show()
				}
				true
			}
		}

		private fun toggleCloudPreferences() {
			if( Firebase.auth.currentUser == null ){
				findPreference<CustomTextPreference>(resources.getString(R.string.id_sign_out))?.isEnabled = false
				findPreference<CustomTextPreference>(resources.getString(R.string.id_delete_all_data_cloud))?.isEnabled = false

			}else{
				findPreference<CustomTextPreference>(resources.getString(R.string.id_sign_out))?.isEnabled = true
				findPreference<CustomTextPreference>(resources.getString(R.string.id_delete_all_data_cloud))?.isEnabled = true
			}
		}
	}
}