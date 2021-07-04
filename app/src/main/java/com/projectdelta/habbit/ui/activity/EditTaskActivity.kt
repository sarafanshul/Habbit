package com.projectdelta.habbit.ui.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.widget.TimePicker
import androidx.activity.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.projectdelta.habbit.R
import com.projectdelta.habbit.data.entities.Task
import com.projectdelta.habbit.databinding.ActivityEditTaskBinding
import com.projectdelta.habbit.util.lang.TimeUtil
import com.projectdelta.habbit.util.lang.capitalized
import com.projectdelta.habbit.util.lang.isOk
import com.projectdelta.habbit.util.setTint
import com.projectdelta.habbit.viewModel.EditTaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTaskActivity : AppCompatActivity() {

	lateinit var binding : ActivityEditTaskBinding
	private val viewModel : EditTaskViewModel by viewModels()
	private val REPEAT_DELAY = 50L
	private val TAG = "EditTaskActivity"
	var skipTime : Long = TimeUtil.getMSfromMidnight()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityEditTaskBinding.inflate( layoutInflater )

		val task : Task = intent.getSerializableExtra( "TASK" ) as Task

		setContentView( binding.root )

		if( task == null )
			resultCancel()

		setLayout(task)

		binding.eTaskFabSave.setOnClickListener {
			val newTask = getUpdatedTask( task )
			if( ! newTask.taskName.isOk() ){
				Snackbar.make( binding.root , "Please enter valid info!" , Snackbar.LENGTH_SHORT ).apply {
					anchorView = binding.eTaskFabSave
				}.show()
			}else {
				viewModel.updateTask(newTask)
				resultOk()
			}
		}

		binding.eTaskIvDelete.setOnClickListener {
			viewModel.delete(task)
			resultOk()
		}

	}

	private fun getUpdatedTask(task: Task ): Task {
		task.apply {
			taskName = binding.eTaskEtTask.text.toString().trim().capitalized()
			summary = binding.eTaskEtSummary.text.toString()

			if (binding.eTaskEtSkip.text.toString().isOk())
				skipTill = viewModel.getTodayFromEpoch() + (binding.eTaskEtSkip.text.toString()).toLong()

			importance = binding.eTaskRt.rating
			isNotificationEnabled = binding.eTaskSwNotification.isChecked
			isSkipAfterEnabled = binding.eTaskSwSkipAfter.isChecked
			skipAfter = skipTime
		}
		return task
	}

	private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

	private fun setLayout( task: Task ) {

		if ( task.taskName.isOk() )
			binding.eTaskEtTask.text = task.taskName.toEditable()

		if ( task.summary.isOk())
			binding.eTaskEtSummary.text = task.summary.toEditable()

		binding.eTaskRt.rating = task.importance

		binding.eTaskEtSkip.text =
			(if (task.skipTill - viewModel.getTodayFromEpoch() < 0L) -1 else task.skipTill - viewModel.getTodayFromEpoch()).toString()

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
		binding.eTaskIvTime.setTint(if (binding.eTaskSwSkipAfter.isChecked) R.color.wet_asphalt else R.color.light_grey)
		binding.eTaskSwSkipAfter.setOnCheckedChangeListener { _, isChecked ->
			binding.eTaskIvTime.isEnabled = isChecked
			binding.eTaskIvTime.setTint(if (isChecked) R.color.wet_asphalt else R.color.light_grey)
		}

		binding.eTaskIvTime.setOnClickListener {
			val timePickerLayout = layoutInflater.inflate(R.layout.layout_time_picker , null)
			val timePicker = timePickerLayout.findViewById<TimePicker>(R.id.alert_tp)
			timePicker.setIs24HourView(true)
			MaterialAlertDialogBuilder(this)
				.setView(timePickerLayout)
				.setPositiveButton("ADD"){_ , _ ->
					skipTime = TimeUtil.fromMinutesToMilliSeconds(timePicker.hour*60L + timePicker.minute)
				}
				.setNegativeButton("Cancel"){_ , _ -> }
				.create()
				.show()
		}
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
		binding.eTaskEtSkip.text = minOf( 30L , binding.eTaskEtSkip.text.toString().toInt() + 1L ).toString()
	}
	private fun decrement( ){
		binding.eTaskEtSkip.text = maxOf( -1L , binding.eTaskEtSkip.text.toString().toInt() - 1L ).toString()
	}
}