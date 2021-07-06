package com.projectdelta.habbit.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.viewpager.widget.ViewPager
import androidx.work.ExistingPeriodicWorkPolicy
import com.google.android.material.snackbar.Snackbar
import com.projectdelta.habbit.R
import com.projectdelta.habbit.ui.activity.EditTaskActivity
import com.projectdelta.habbit.adapter.HomeViewPagerAdapter
import com.projectdelta.habbit.data.TasksDatabase
import com.projectdelta.habbit.data.entities.Task
import com.projectdelta.habbit.databinding.ActivityMainBinding
import com.projectdelta.habbit.ui.fragment.DoneFragment
import com.projectdelta.habbit.ui.fragment.SkipFragment
import com.projectdelta.habbit.ui.fragment.TodoFragment
import com.projectdelta.habbit.util.*
import com.projectdelta.habbit.util.lang.*
import com.projectdelta.habbit.util.view.NavigationUtil
import com.projectdelta.habbit.util.notification.Notifications.DEFAULT_UPDATE_INTERVAL
import com.projectdelta.habbit.util.notification.UpdateNotificationJob
import com.projectdelta.habbit.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	lateinit var binding : ActivityMainBinding
	private val viewModel: MainViewModel by viewModels()
	lateinit var adapter : HomeViewPagerAdapter

	companion object{
		fun getInstance() = this
	}

	/**
	 * Alternative activity specific lambda for OnActivityResult .
	 * [For more](https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative)
	 * */
	private val startForResultTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
			result: ActivityResult ->
		if (result.resultCode == Activity.RESULT_OK) {
			if( this::binding.isInitialized )
				Snackbar.make(binding.root , "Changes saved" , Snackbar.LENGTH_LONG).apply {
					anchorView = binding.mainFabCreate
				}.show()
			else // binding not initialized use toast
				Toast.makeText(this , "Container updated" , Toast.LENGTH_LONG).show()
		}
	}

	@SuppressLint("SimpleDateFormat")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityMainBinding.inflate(layoutInflater)

		setTheme( R.style.Theme_Habbit )
		setContentView( binding.root )

		setLayout()

		initMenu()

		checkNotificationPreferences()

		cancelAllNotifications()

		binding.mainFabCreate.setOnClickListener {
			launchEditActivity( Task( viewModel.getMSfromEpoch() , "" , mutableListOf<Long>() , 0f ) )
		}

	}

	private fun initMenu() {

		binding.mainNavigation.setNavigationItemSelectedListener {
			when( it.itemId ){
				R.id.menu_insights -> NavigationUtil.insights(this)
				R.id.menu_settings -> NavigationUtil.settings(this)
				R.id.menu_about    -> NavigationUtil.about(this)
				else -> Throwable("404 Not found")
			}
			true
		}

		binding.mainBtnSignIn.setOnClickListener {
			this.toast("Test Button Hide It!")
		}
	}

	fun launchEditActivity(task: Task) {
		Intent( this , EditTaskActivity::class.java ).apply {
			putExtra( "TASK" , task )
		}.also{
			startForResultTask.launch( it )
		}
	}

	@SuppressLint("SimpleDateFormat")
	private fun setLayout(){

		binding.mainToolbar.title = SimpleDateFormat("EEEE").format( Date() )
		binding.mainToolbar.subtitle = TimeUtil.getPastDateFromOffset(0)

		adapter = HomeViewPagerAdapter(supportFragmentManager)
		adapter.addFragment( TodoFragment() , "TODO" )
		adapter.addFragment( SkipFragment() , "SKIPPED" )
		adapter.addFragment( DoneFragment() , "DONE" )
		binding.mainVp.adapter = adapter

		binding.mainTabs.setupWithViewPager(binding.mainVp)
		setTabIcons()

		viewModel.getAllTasks().observe(this , {data ->
			var undone = 0

			if( ! data.isNullOrEmpty() ){
				undone = data.tasksBeforeSkipTime(viewModel.getMSFromMidnight()).unfinishedTill( viewModel.getTodayFromEpoch() ).size
			}
			if( undone > 0 )
				binding.mainTabs.getTabAt(0)?.orCreateBadge?.number = undone
			else
				binding.mainTabs.getTabAt(0)?.removeBadge()
		})

		binding.mainVp.addOnPageChangeListener( object : ViewPager.OnPageChangeListener{
			override fun onPageScrolled(
				position: Int,
				positionOffset: Float,
				positionOffsetPixels: Int
			) {}

			override fun onPageSelected(position: Int) {
				for( i in 0 until binding.mainTabs.tabCount ){
					if( i == position )
						binding.mainTabs.getTabAt( i )!!.text = ""
					else
						binding.mainTabs.getTabAt( i )!!.text = adapter.mFragmentTitleList[i]
				}
			}

			override fun onPageScrollStateChanged(state: Int) {}
		} )
	}

	private fun setTabIcons() {
		binding.mainTabs.getTabAt(0)!!.setIcon( R.drawable.ic_adjust_black_24dp )
		binding.mainTabs.getTabAt(1)!!.setIcon( R.drawable.ic_skip_next_black_24dp )
		binding.mainTabs.getTabAt(2)!!.setIcon( R.drawable.ic_done_all_black_24dp )

		binding.mainTabs.getTabAt(0)!!.text = ""
	}

	/**
	 * If notifications enabled and "somehow ended" by system restart job
	 */
	private fun checkNotificationPreferences() {
		val sharedPref = this.getSharedPreferences(this.packageName + "_preferences", Context.MODE_PRIVATE)
		val interval = when(sharedPref.getString("NotificationInterval" , "")){
			"1 Hour" -> 1L
			"3 Hours" -> 3L
			"6 Hours" -> 6L
			"12 Hours" -> 12L
			else ->  DEFAULT_UPDATE_INTERVAL
		}
		if( sharedPref.getBoolean("NotificationsEnabled" , false) )
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
	protected open fun getUserPermissions() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (! this.powerManager.isIgnoringBatteryOptimizations( packageName ) ) {
				startActivity( Intent( Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS ) )
			}
		}
	}

	private fun test() {

		fun getRandomString(length: Int) : String {
			val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
			return (1..length)
				.map { allowedChars.random() }
				.joinToString("")
		}

		GlobalScope.launch(Dispatchers.IO) {
			val Dao = TasksDatabase.getInstance(this@MainActivity).tasksDao()
			val X = viewModel.getTodayFromEpoch()
			for( i in 0..10 ){
				val cur = Task( viewModel.getMSfromEpoch() + i , "Test : TODO/DONE MIX ${(i + 1)%10}" , mutableListOf() ,
					i + 0f , true , getRandomString((i + 1)*15) )
				for( d in 1 until i )
					cur.lastDayCompleted.add( X - d )
				cur.lastDayCompleted.reverse()
				launch { Dao.insertTask(cur) }
			}
//			for( i in 0..5 ){
//				val cur = Task( viewModel.getMSfromEpoch() + i , "Test : DONE ${i + 1}" , mutableListOf() ,
//					i + 0f, true , getRandomString((i + 1)*15)  )
//				for( d in 0 .. i )
//					cur.lastDayCompleted.add( X - d )
//				cur.lastDayCompleted.reverse()
//				launch { Dao.insertTask(cur) }
//			}
			for( i in 0..5 ){
				val cur = Task( viewModel.getMSfromEpoch() + i , "Test : SKIP ${i + 1}" , mutableListOf() , i + 0f  ,
					true , getRandomString((i + 1)*15)  ,skipTill = X + i )
				launch { Dao.insertTask(cur) }
			}
		}
	}
}