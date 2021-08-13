package com.projectdelta.habbit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.projectdelta.habbit.data.model.entities.Task
import com.projectdelta.habbit.databinding.LayoutRvSkipBinding

class RecyclerViewSkipAdapter():
	ListAdapter< Task , RecyclerViewSkipAdapter.LayoutViewHolder >( DIFF_CALLBACK ) {

	private var today = 0L

	companion object{
		val DIFF_CALLBACK : DiffUtil.ItemCallback<Task> = object :DiffUtil.ItemCallback<Task>(){
			override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
				return oldItem.equals( newItem )
			}

			override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
				return oldItem.taskName == newItem.taskName &&
						oldItem.summary == newItem.summary &&
						oldItem.importance == newItem.importance
			}
		}
	}

	inner class LayoutViewHolder(private val binding: LayoutRvSkipBinding) :
		RecyclerView.ViewHolder(binding.root) {

		fun bind(T: Task, streakString: String) {
			with(binding) {
				tasksTwId.text = T.taskName
				tasksTwId.isSelected = true
				tasksTwStreak.text = streakString
				tasksTwRating.rating = T.importance
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayoutViewHolder {
		val binding =
			LayoutRvSkipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return LayoutViewHolder(binding)
	}

	override fun onBindViewHolder(holder: LayoutViewHolder, position: Int) {

		var streakString = when(val streak = getItem(position).skipTill - today){
			0L -> "Skipped today"
			1L -> "$streak day"
			else -> "$streak days"
		}
		if( getItem(position).isSkipAfterEnabled )
			streakString = "Missed today"

		holder.bind(getItem(position), streakString)
	}

	fun set( _today: Long) {
		today = _today
	}

	interface OnSwipeRight{
		fun doWork(viewHolder: RecyclerView.ViewHolder): Unit
	}

	fun getItemAt(position: Int ) : Task = getItem(position)

}