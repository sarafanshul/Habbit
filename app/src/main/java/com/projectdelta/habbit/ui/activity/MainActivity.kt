package com.projectdelta.habbit.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import androidx.work.ExistingPeriodicWorkPolicy
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.projectdelta.habbit.R
import com.projectdelta.habbit.ui.activity.editTask.EditTaskActivity
import com.projectdelta.habbit.ui.adapter.HomeViewPagerAdapter
import com.projectdelta.habbit.data.entities.Task
import com.projectdelta.habbit.databinding.ActivityMainBinding
import com.projectdelta.habbit.ui.base.BaseViewBindingActivity
import com.projectdelta.habbit.ui.fragment.DoneFragment
import com.projectdelta.habbit.ui.fragment.SkipFragment
import com.projectdelta.habbit.ui.fragment.TodoFragment
import com.projectdelta.habbit.util.lang.*
import com.projectdelta.habbit.ui.navigation.NavigationUtil
import com.projectdelta.habbit.util.notification.Notifications.DEFAULT_UPDATE_INTERVAL
import com.projectdelta.habbit.util.notification.UpdateNotificationJob
import com.projectdelta.habbit.ui.viewModel.MainViewModel
import com.projectdelta.habbit.util.database.firebase.FirebaseUtil
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
		private const val APPBAR_ANIMATION_DURATION = 100L
		private const val ANIMATION_RESET_DELAY = 700L
		private const val EXPLODE_ANIMATION_DURATION = 150L
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

	private val startForResultSignIn = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
			result: ActivityResult ->
		if (result.resultCode == Activity.RESULT_OK) {
			val task = GoogleSignIn.getSignedInAccountFromIntent( result.data )
			if(task.isSuccessful) {
				// Google Sign In was successful, authenticate with Firebase
				val account = task.getResult(ApiException::class.java)!!
				Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
				this.toast("Sign In successful!")
				firebaseAuthWithGoogle(account.idToken!!)
			} else {
				// Google Sign In failed, update UI appropriately
				this.toast("Unable to sign in.")
			}
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

		initMenu()

		checkNotificationPreferences()

		cancelAllNotifications()

		// TODO (add feature for sync data on startup)
		// syncData()

		setupUser(auth.currentUser)
	}

	private fun signIn() {
		val signInIntent = firebaseUtil.googleSignInClient.signInIntent
		startForResultSignIn.launch(signInIntent)
	}

	private fun firebaseAuthWithGoogle(idToken: String) {
		val credential = GoogleAuthProvider.getCredential(idToken, null)
		auth.signInWithCredential(credential)
			.addOnCompleteListener(this) { task ->
				if (task.isSuccessful) {
					// Sign in success, update UI with the signed-in user's information
					Log.d(TAG, "signInWithCredential:success")
					val user = auth.currentUser
					setupUser(user)
				} else {
					// If sign in fails, display a message to the user.
					Log.w(TAG, "signInWithCredential:failure", task.exception)
					setupUser(null)
				}
			}
	}

	private fun setupUser(user: FirebaseUser?) {
		val headerView : View = binding.mainNavigation.getHeaderView(0)
		if( user == null ) {
			binding.mainBtnSignIn.text = resources.getString(R.string.sign_in)
			binding.mainBtnSignIn.setOnClickListener {
				signIn()
			}
			headerView.findViewById<MaterialTextView>(R.id.header_name).text = getString(R.string.guest)
			headerView.findViewById<MaterialTextView>(R.id.header_email).text = ""
			headerView.findViewById<ShapeableImageView>(R.id.header_image).setImageDrawable(getDrawable(R.drawable.ic_guest_user))
		}else {
			Log.d(TAG, "setupUser: ${user.displayName} @ ${user.email}")
			binding.mainBtnSignIn.text = ""
			headerView.findViewById<MaterialTextView>(R.id.header_name).text = user.displayName?.split(" ")?.joinToString(" ") { it.capitalized() }
			headerView.findViewById<MaterialTextView>(R.id.header_email).text = user.email
			Glide.with(this)
				.load( user.photoUrl )
				.into(headerView.findViewById<ShapeableImageView>(R.id.header_image))
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

		binding.mainFabCreate.setOnClickListener {
			animateAndDoStuff {
				launchEditActivity( Task( viewModel.getMSfromEpoch() , "" , mutableListOf<Long>() , 0f ) , false )
			}
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
	}

	private fun setTabIcons() {
		binding.mainTabs.getTabAt(0)!!.setIcon( R.drawable.ic_adjust_black_24dp )
		binding.mainTabs.getTabAt(1)!!.setIcon( R.drawable.ic_skip_next_black_24dp )
		binding.mainTabs.getTabAt(2)!!.setIcon( R.drawable.ic_done_all_black_24dp )

		binding.mainTabs.getTabAt(0)!!.text = ""
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
		setupUser(auth?.currentUser)
	}

}