package com.projectdelta.habbit.ui.setting

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.*
import androidx.work.ExistingPeriodicWorkPolicy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.projectdelta.habbit.R
import com.projectdelta.habbit.data.preference.PreferencesHelper
import com.projectdelta.habbit.databinding.SettingsActivityBinding
import com.projectdelta.habbit.ui.base.BaseViewBindingActivity
import com.projectdelta.habbit.util.database.DatabaseUtil
import com.projectdelta.habbit.util.database.SyncUtil
import com.projectdelta.habbit.util.system.lang.darkToast
import com.projectdelta.habbit.util.system.lang.isOnline
import com.projectdelta.habbit.util.system.lang.toast
import com.projectdelta.habbit.util.notification.Notifications.DEFAULT_UPDATE_INTERVAL
import com.projectdelta.habbit.util.notification.UpdateNotificationJob
import com.projectdelta.habbit.widget.CustomTextPreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : BaseViewBindingActivity<SettingsActivityBinding>() ,
	SharedPreferences.OnSharedPreferenceChangeListener {

	companion object{
		private const val TAG = "SettingsActivity"
	}

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
		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
		super.onPause()
	}

	override fun onDestroy() {
		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
		super.onDestroy()
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
			resources.getString(R.string.id_app_theme) -> {
				recreateAndChangeTheme()
			}
		}
	}

	/**
	 * Creates a smooth transition between themes , replacement of [Activity.recreate()]
	 *
	 * [See More](https://stackoverflow.com/a/57023051/11718077)
	 */
	private fun recreateAndChangeTheme() {
		finish()
		startActivity(Intent(this, javaClass))
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
	}

	@AndroidEntryPoint
	class SettingsFragment : PreferenceFragmentCompat() {

		@Inject lateinit var syncUtil : SyncUtil
		@Inject lateinit var preferencesHelper: PreferencesHelper
		private val databaseUtil : DatabaseUtil = DatabaseUtil()

		override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
			setPreferencesFromResource(R.xml.root_preferences, rootKey)

			toggleCloudPreferences()
			togglePreferences()

			findPreference<Preference>(resources.getString(R.string.id_sync_now))?.setOnPreferenceClickListener { _ ->
				requireActivity().let { syncUtil.syncNow(it) }
				true
			}

			findPreference<CustomTextPreference>(resources.getString(R.string.id_sign_out))?.setOnPreferenceClickListener { _ ->
				if( Firebase.auth.currentUser != null )
					MaterialAlertDialogBuilder(requireActivity()).apply {
						setTitle("Sign out of Habbit?")
						setPositiveButton("BYE!"){_ , _ ->
							Firebase.auth.signOut()
							toggleCloudPreferences()
						}
						setNeutralButton("CANCEL"){_ , _ ->}
						create()
					}.show()
				true
			}

			findPreference<CustomTextPreference>(resources.getString(R.string.id_delete_all_data_cloud))?.setOnPreferenceClickListener { _ ->
				requireActivity().let {
					if (it.isOnline()) {
						MaterialAlertDialogBuilder(it).apply {
							setTitle("Are you sure , This is an irreversible process.")
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
						setTitle("Are you sure , This is an irreversible process.")
						setPositiveButton("Delete") { _, _ -> databaseUtil.nukeLocal(it) }
						create()
					}.show()
				}
				true
			}

//			findPreference<ListPreference>(getString(R.string.id_app_theme))?.setOnPreferenceChangeListener { preference, newValue ->
//				togglePreferences()
//				true
//			}
		}

		private fun togglePreferences() {
			Log.d(TAG, "togglePreferences: Fired")
			findPreference<ListPreference>(getString(R.string.id_app_theme))?.summary =
				preferencesHelper.getAppTheme()
		}

		private fun toggleCloudPreferences() {
			if( Firebase.auth.currentUser == null ){
				findPreference<CustomTextPreference>(resources.getString(R.string.id_sign_out))?.isEnabled = false
				findPreference<CustomTextPreference>(resources.getString(R.string.id_delete_all_data_cloud))?.isEnabled = false
				findPreference<SwitchPreferenceCompat>(resources.getString(R.string.id_sync))?.isChecked = false

			}
			else{
				findPreference<CustomTextPreference>(resources.getString(R.string.id_sign_out))?.isEnabled = true
				findPreference<CustomTextPreference>(resources.getString(R.string.id_delete_all_data_cloud))?.isEnabled = true
				findPreference<SwitchPreferenceCompat>(resources.getString(R.string.id_sync))?.isChecked = true
			}
		}
	}
}