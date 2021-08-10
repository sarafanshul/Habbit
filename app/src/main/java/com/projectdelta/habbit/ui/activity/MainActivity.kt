package com.projectdelta.habbit.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import androidx.work.ExistingPeriodicWorkPolicy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.projectdelta.habbit.R
import com.projectdelta.habbit.data.entities.Task
import com.projectdelta.habbit.databinding.ActivityMainBinding
import com.projectdelta.habbit.ui.activity.editTask.EditTaskActivity
import com.projectdelta.habbit.ui.adapter.HomeViewPagerAdapter
import com.projectdelta.habbit.ui.base.BaseViewBindingActivity
import com.projectdelta.habbit.ui.fragment.DoneFragment
import com.projectdelta.habbit.ui.fragment.MenuFragment
import com.projectdelta.habbit.ui.fragment.SkipFragment
import com.projectdelta.habbit.ui.fragment.TodoFragment
import com.projectdelta.habbit.ui.viewModel.MainViewModel
import com.projectdelta.habbit.util.database.firebase.FirebaseUtil
import com.projectdelta.habbit.util.lang.*
import com.projectdelta.habbit.util.notification.Notifications.DEFAULT_UPDATE_INTERVAL
import com.projectdelta.habbit.util.notification.UpdateNotificationJob
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseViewBindingActivity<ActivityMainBinding>(){

	private val viewModel: MainViewModel by viewModels()
	lateinit var adapter : HomeViewPagerAdapter
	private lateinit var auth: FirebaseAuth

	@Inject
	lateinit var firebaseUtil: FirebaseUtil

	companion object{
		fun getInstance() = this
		private const val TAG = "MainActivity"
	}

	/**
	 * Alternative activity specific lambda for OnActivityResult .
	 * [For more](https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative)
	 * */
	private val startForResultTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
			result: ActivityResult ->
		if (result.resultCode == Activity.RESULT_OK) {
			Snackbar.make(binding.root , "Changes saved" , Snackbar.LENGTH_LONG).apply {
				anchorView = binding.mainFabCreate
			}.show()
		}
	}

	@SuppressLint("SimpleDateFormat")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		_binding = ActivityMainBinding.inflate(layoutInflater)
		auth = Firebase.auth

		setContentView( binding.root )

		getUserPermissions()

		setLayout()

		checkNotificationPreferences()

		cancelAllNotifications()

		// TODO (add feature for sync data on startup)
		// syncData()

	}

	@SuppressLint("SimpleDateFormat")
	private fun setLayout(){

		binding.mainToolbar.title = SimpleDateFormat("EEEE").format( Date() )
		binding.mainToolbar.subtitle = TimeUtil.getPastDateFromOffset(0)

		adapter = HomeViewPagerAdapter(supportFragmentManager)
		adapter.addFragment( TodoFragment() , "TODO" )
		adapter.addFragment( SkipFragment() , "SKIPPED" )
		adapter.addFragment( DoneFragment() , "DONE" )
		adapter.addFragment( MenuFragment() , "MENU" )
		binding.mainVp.adapter = adapter

		syncNavigationWithViewPager()

		viewModel.getAllTasks().observe(this , {data ->
			var undone = 0

			if( ! data.isNullOrEmpty() ){
				undone = data.tasksBeforeSkipTime(viewModel.getMSFromMidnight()).unfinishedTill( viewModel.getTodayFromEpoch() ).size
			}
			if( undone > 0 )
				binding.mainBottomAppBar.getOrCreateBadge(R.id.todo).number = undone
			else
				binding.mainBottomAppBar.removeBadge(R.id.todo)
		})

		binding.mainFabCreate.setOnClickListener {
			animateAndDoStuff {
				launchEditActivity( Task( viewModel.getMSfromEpoch() , "" , mutableListOf() , 0f ) , false )
			}
		}
	}

	private fun syncNavigationWithViewPager() {
		// Empty menu item for fab
		binding.mainBottomAppBar.menu.findItem(R.id.empty).isEnabled = false

		binding.mainVp.addOnPageChangeListener( object : ViewPager.OnPageChangeListener{
			override fun onPageScrolled(
				position: Int,
				positionOffset: Float,
				positionOffsetPixels: Int
			) {}

			override fun onPageSelected(position: Int) {
				binding.mainBottomAppBar.menu.let{
					when(position){
						0 -> it.findItem(R.id.todo).isChecked = true
						1 -> it.findItem(R.id.skip).isChecked = true
						2 -> it.findItem(R.id.done).isChecked = true
						3 -> it.findItem(R.id.more).isChecked = true
						else -> Throwable("$TAG , Unknown Item Clicked $position")
					}
				}
			}

			override fun onPageScrollStateChanged(state: Int) {}
		} )

		binding.mainBottomAppBar.setOnNavigationItemSelectedListener {
			when( it.itemId ){
				R.id.todo -> binding.mainVp.currentItem = 0
				R.id.skip -> binding.mainVp.currentItem = 1
				R.id.done -> binding.mainVp.currentItem = 2
				R.id.more -> binding.mainVp.currentItem = 3
				else -> Throwable("$TAG , Unknown Item Clicked ${it.itemId}")
			}
			true
		}
	}

	private fun animateAndDoStuff( stuff : () -> Unit ){
		binding.mainFabCreate.getCoordinates().let { coordinates ->
			binding.mainCircle.showRevealEffect(
				coordinates.x ,
				coordinates.y ,
				object : AnimatorListenerAdapter() {
					override fun onAnimationStart(animation: Animator?) {
						stuff()
					}
				}
			)
		}
	}

	fun launchEditActivity(task: Task , anim : Boolean = true) {
		Intent( this , EditTaskActivity::class.java ).apply {
			putExtra( "TASK" , task )
			if( !anim )
				addFlags( Intent.FLAG_ACTIVITY_NO_ANIMATION )
		}.also{
			startForResultTask.launch( it )
		}
	}

	/**
	 * If notifications enabled and "somehow ended" by system restart job
	 */
	private fun checkNotificationPreferences() {
		val interval = when( preferences.getNotificationInterval() ){
			"1 Hour" -> 1L
			"3 Hours" -> 3L
			"6 Hours" -> 6L
			"12 Hours" -> 12L
			else ->  DEFAULT_UPDATE_INTERVAL
		}
		if( preferences.isNotificationEnabled() )
			UpdateNotificationJob.setupTask( this , ExistingPeriodicWorkPolicy.KEEP , interval )
		else
			UpdateNotificationJob.setupTask( this , ExistingPeriodicWorkPolicy.REPLACE , 0 )
	}

	/**
	 * Cancels all notifications on startup because if we click on one item of group notification,
	 * Manager only removes clicked item from group.
	 */
	private fun cancelAllNotifications() {
		this.notificationManager.cancelAll()
	}

	@SuppressLint("ObsoleteSdkInt")
	private fun getUserPermissions() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if ( (! this.powerManager.isIgnoringBatteryOptimizations( packageName ))
				&& preferences.isNotificationEnabled()
			) {
				MaterialAlertDialogBuilder(this).apply {
					setTitle("Enable app features")
					setMessage("For using notification feature you need to turn off battery optimizations for this app , if you are using a Chinese ROM you need to enable Autostart feature too!")
					setPositiveButton("GO"){_ , _ ->
						startActivity( Intent( Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS ) )
					}
					setNeutralButton("CANCEL"){_,_->}
				}
			}
		}
	}

	override fun onRestart() {
		super.onRestart()
		if( binding.mainCircle.isVisible )
			binding.mainFabCreate.getCoordinates().let { coordinates ->
				binding.mainCircle.hideRevealEffect(
					coordinates.x ,
					coordinates.y ,
					1929
				)
			}
	}

}