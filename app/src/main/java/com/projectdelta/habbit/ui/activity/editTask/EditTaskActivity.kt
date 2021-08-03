package com.projectdelta.habbit.ui.activity.editTask

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.util.TypedValue
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.viewModels
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.projectdelta.habbit.R
import com.projectdelta.habbit.constant.COLLAPSING_TOOLBAR_VISIBILITY_THRESHOLD
import com.projectdelta.habbit.data.entities.Task
import com.projectdelta.habbit.databinding.ActivityEditTaskBinding
import com.projectdelta.habbit.ui.activity.editTask.state.CollapsingToolbarState
import com.projectdelta.habbit.ui.activity.editTask.state.EditTaskInteractionState
import com.projectdelta.habbit.ui.base.BaseViewBindingActivity
import com.projectdelta.habbit.ui.viewModel.EditTaskViewModel
import com.projectdelta.habbit.util.TodoCallback
import com.projectdelta.habbit.util.lang.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTaskActivity : BaseViewBindingActivity<ActivityEditTaskBinding>() {

	private val viewModel : EditTaskViewModel by viewModels()
	private var skipTime : Long = -1

	companion object{
		private const val REPEAT_DELAY = 50L
		private const val TAG = "EditTaskActivity"
		private const val NULL_DISPLAY_ET = "-"
		private const val DEFAULT_TITLE = "New task!"
		private const val ERROR_SAVE_WARNING = "Please enter a valid title!"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		_binding = ActivityEditTaskBinding.inflate( layoutInflater )

		val task : Task = intent.getSerializableExtra( "TASK" ) as Task

		setContentView( binding.root )

		if( task == null )
			resultCancel()

		setLayout(task)
		subscribeObservers()

	}

	private fun getUpdatedTask(task: Task ): Task {
		task.apply {
			taskName = binding.noteTitle.text.toString().trim().capitalized()
			summary = binding.eTaskEtSummary.text.toString().trim()

			if (binding.eTaskEtSkip.text.toString().isOk())
				skipTill = viewModel.getTodayFromEpoch() + (getValueTV().toString()).toLong()

			importance = binding.eTaskRt.rating
			isNotificationEnabled = binding.eTaskSwNotification.isChecked
			isSkipAfterEnabled = binding.eTaskSwSkipAfter.isChecked
			skipAfter = skipTime
		}
		return task
	}

	private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

	private fun setLayout( task: Task ) {

		binding.noteTitle.disableContentInteraction()
		displayDefaultToolBar()
		transitionToExpandedMode()
		addHistory( task )

		binding.appBar.addOnOffsetChangedListener(
			AppBarLayout.OnOffsetChangedListener{ _, offset ->
				if( offset < COLLAPSING_TOOLBAR_VISIBILITY_THRESHOLD ){
					if( viewModel.isEditingTitle() ){
						viewModel.exitEditState()
						displayDefaultToolBar()
					}
					viewModel.setCollapsingToolbarState( CollapsingToolbarState.Collapsed() )
				}else{
					viewModel.setCollapsingToolbarState( CollapsingToolbarState.Expanded() )
				}
			}
		)

		binding.toolbarPrimaryIcon.setOnClickListener {
			this.hideKeyboard()
			if( viewModel.checkEditState() ){
				viewModel.setInteractionTitleState(EditTaskInteractionState.DefaultState())
				viewModel.setInteractionBodyState( EditTaskInteractionState.DefaultState() )
				displayDefaultToolBar()
			}else {
				resultCancel()
			}
		}

		binding.toolbarSecondaryIcon.setOnClickListener {
			this.hideKeyboard()
			if( viewModel.checkEditState() ){
				viewModel.setInteractionTitleState(EditTaskInteractionState.DefaultState())
				viewModel.setInteractionBodyState( EditTaskInteractionState.DefaultState() )
				displayDefaultToolBar()
			}else {
				MaterialAlertDialogBuilder(this).apply{
					setTitle("Delete this task!")
					setPositiveButton("Delete"){ _ , _ ->
						viewModel.delete( task )
						resultOk()
					}
					setNeutralButton("Cancel"){_ , _ -> }
					create()
				}.show()
			}
		}

		binding.eTaskFabSave.setOnClickListener {
			val newTask = getUpdatedTask( task )
			if( ! newTask.taskName.isOk() ){
				Snackbar.make( binding.root , ERROR_SAVE_WARNING , Snackbar.LENGTH_SHORT ).apply {
					anchorView = binding.eTaskFabSave
				}.show()
			}else {
				viewModel.updateTask(newTask)
				resultOk()
			}
		}


		binding.noteTitle.setOnClickListener {
			if (!viewModel.isEditingTitle()) {
				viewModel.setInteractionTitleState(EditTaskInteractionState.EditState())
			}
		}

		binding.eTaskEtSummary.setOnClickListener {
			if( !viewModel.isEditingBody() ){
				viewModel.setInteractionBodyState( EditTaskInteractionState.EditState() )
			}
		}

		if (task.taskName.isOk())
			binding.noteTitle.text = task.taskName.toEditable()

		if (task.summary.isOk())
			binding.eTaskEtSummary.text = task.summary.toEditable()

		binding.eTaskRt.rating = task.importance

		setValueTV((if (task.skipTill - viewModel.getTodayFromEpoch() < 0L) -1 else task.skipTill - viewModel.getTodayFromEpoch()).toInt())

		binding.eTaskSwNotification.isChecked = task.isNotificationEnabled

		binding.eTaskBtnSkipAdd.setOnClickListener { increment() }
		binding.eTaskBtnSkipAdd.setOnLongClickListener {
			val handler = Handler(Looper.myLooper()!!)
			val runnable: Runnable = object : Runnable {
				override fun run() {
					handler.removeCallbacks(this)
					if (binding.eTaskBtnSkipAdd.isPressed) {
						increment()
						handler.postDelayed(this, REPEAT_DELAY)
					}
				}
			}
			handler.postDelayed(runnable, 0)
			true
		}
		binding.eTaskBtnSkipSub.setOnClickListener { decrement() }
		binding.eTaskBtnSkipSub.setOnLongClickListener {
			val handler = Handler(Looper.myLooper()!!)
			val runnable: Runnable = object : Runnable {
				override fun run() {
					handler.removeCallbacks(this)
					if (binding.eTaskBtnSkipSub.isPressed) {
						decrement()
						handler.postDelayed(this, REPEAT_DELAY)
					}
				}
			}
			handler.postDelayed(runnable, 0)
			true
		}


		binding.eTaskSwSkipAfter.isChecked = task.isSkipAfterEnabled
		binding.eTaskIvTime.isEnabled = task.isSkipAfterEnabled
		binding.eTaskIvTime.setTint(if (binding.eTaskSwSkipAfter.isChecked) getColorFromAttr(R.attr.colorSecondary) else R.color.light_grey)
		binding.eTaskSwSkipAfter.setOnCheckedChangeListener { _, isChecked ->
			binding.eTaskIvTime.isEnabled = isChecked
			binding.eTaskIvTime.setTint(if (isChecked) getColorFromAttr(R.attr.colorSecondary) else R.color.light_grey)
		}

		binding.eTaskIvTime.setOnClickListener {
			if( skipTime == -1L )
				skipTime = TimeUtil.getMSfromMidnight()
			val timePickerLayout = layoutInflater.inflate(R.layout.layout_time_picker, null)
			val timePicker = timePickerLayout.findViewById<TimePicker>(R.id.alert_tp)
			timePicker.apply {
				hour = (TimeUtil.fromMillisecondsToHour(skipTime)).toInt()
				minute = ((skipTime / (60*1000)) - TimeUtil.fromMillisecondsToHour(skipTime) * 60 ).toInt()
				setIs24HourView(true)
			}
			MaterialAlertDialogBuilder(this)
				.setView(timePickerLayout)
				.setPositiveButton("SAVE") { _, _ ->
					skipTime =
						TimeUtil.fromMinutesToMilliSeconds(timePicker.hour * 60L + timePicker.minute)
				}
				.setNegativeButton("Cancel") { _, _ -> }
				.create()
				.show()
		}
	}

	private fun addHistory( task: Task ) {
		viewModel.getTaskByIdLive( task.id ).observe( this , observeLive@{ data ->
			if( data.isNullOrEmpty() || data.first().lastDayCompleted.isNullOrEmpty() )
				return@observeLive

			binding.eTaskTvHistory.text = data.first().toDateBulletList( )
		} )
	}

	private fun subscribeObservers(){

		viewModel.collapsingToolbarState.observe( this , { state ->

			when(state){
				is CollapsingToolbarState.Expanded -> {
					transitionToExpandedMode()
				}
				is CollapsingToolbarState.Collapsed -> {
					transitionToCollapsedMode()
				}
			}
		} )

		viewModel.titleInteractionState.observe( this , { state ->

			when(state){
				is EditTaskInteractionState.DefaultState -> {
					binding.noteTitle.disableContentInteraction()
				}
				is EditTaskInteractionState.EditState -> {
					displayEditStateToolbar()
					binding.noteTitle.enableContentInteraction()
					this.showKeyboard()
				}
			}
		})

		/**
		 * TODO(Link Summary edit text with listener here)
		 */
		viewModel.bodyInteractionState.observe(this , {state ->
			when( state ) {
				is EditTaskInteractionState.EditState -> {
					displayEditStateToolbar()
					binding.eTaskEtSummary.enableContentInteraction()
					this.showKeyboard()
				}
				is EditTaskInteractionState.DefaultState -> {
					binding.eTaskEtSummary.disableContentInteraction()
				}
			}
		})

	}

	private fun transitionToExpandedMode() {
		binding.noteTitle.fadeIn()
		displayToolbarTitle( binding.toolBarTitle , null , true )
	}

	private fun transitionToCollapsedMode() {
		binding.noteTitle.fadeOut()
		displayToolbarTitle( binding.toolBarTitle , getToolbarTitle() , true )
	}
	private fun getToolbarTitle() = if( binding.noteTitle.text.toString().isOk() ) binding.noteTitle.text.toString().capitalized() else DEFAULT_TITLE

	@SuppressLint("UseCompatLoadingForDrawables")
	private fun displayDefaultToolBar() {
		binding.toolbarPrimaryIcon.setImageDrawable(
			resources.getDrawable(
				R.drawable.ic_baseline_arrow_back_24,
				theme
			)
		)

		binding.toolbarSecondaryIcon.setImageDrawable(
			resources.getDrawable(
				R.drawable.ic_delete_black_24dp,
				theme
			)
		)
	}

	@SuppressLint("UseCompatLoadingForDrawables")
	private fun displayEditStateToolbar(){
		binding.toolbarPrimaryIcon.setImageDrawable(
			resources.getDrawable(
				R.drawable.ic_baseline_close_24,
				theme
			)
		)

		binding.toolbarSecondaryIcon.setImageDrawable(
			resources.getDrawable(
				R.drawable.ic_done_black_24dp,
				theme
			)
		)
	}

	private fun resultOk(){
		setResult( Activity.RESULT_OK)
		finish()
	}

	private fun resultCancel(){
		setResult( Activity.RESULT_CANCELED )
		finish()
	}

	private fun increment( ){
		setValueTV( minOf( 30 , getValueTV() + 1 ) )
	}

	private fun decrement( ){
		setValueTV( maxOf( -1 , getValueTV() - 1 ) )
	}

	private fun getValueTV() : Int{
		if( binding.eTaskEtSkip.text.toString() == NULL_DISPLAY_ET ) return -1
		return binding.eTaskEtSkip.text.toString().toInt()
	}

	private fun setValueTV( X : Int ){
		if( X == -1 ) binding.eTaskEtSkip.text = NULL_DISPLAY_ET
		else binding.eTaskEtSkip.text = X.toString()
	}

	private fun displayToolbarTitle(textView: TextView, title: String?, useAnimation: Boolean) {
		if(title != null){
			showToolbarTitle(textView, title, useAnimation)
		}
		else{
			hideToolbarTitle(textView, useAnimation)
		}
	}

	private fun hideToolbarTitle(textView: TextView, animation: Boolean){
		if(animation){
			textView.fadeOut(
				object: TodoCallback {
					override fun execute() {
						textView.text = ""
					}
				}
			)
		}
		else{
			textView.text = ""
			textView.gone()
		}
	}

	private fun showToolbarTitle( textView: TextView, title: String, animation: Boolean){
		textView.text = title
		if(animation){
			textView.fadeIn()
		}
		else{
			textView.visible()
		}
	}
}