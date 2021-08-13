package com.projectdelta.habbit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.projectdelta.habbit.data.model.entities.Task
import com.projectdelta.habbit.databinding.LayoutRvTodoBinding
import com.projectdelta.habbit.util.lang.isOk

class RecyclerViewTodoAdapter : ListAdapter<Task, RecyclerViewTodoAdapter.LayoutViewHolder>(DIFF_CALLBACK) {

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

	inner class LayoutViewHolder( private val binding: LayoutRvTodoBinding , ) : RecyclerView.ViewHolder( binding.root ){

		fun bind(T : Task , streakString : String){
			with(binding){
				tasksTwId.text = T.taskName
				tasksTwId.isSelected = true
				tasksTwStreak.text = streakString
				tasksTwRating.rating = T.importance
				tasksTwSummary.text = if(T.summary.isOk()) T.summary else "Tap to add summary!"
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayoutViewHolder {
		val binding = LayoutRvTodoBinding.inflate( LayoutInflater.from( parent.context ) , parent , false )
		return LayoutViewHolder( binding )
	}

	override fun onBindViewHolder(holder: LayoutViewHolder, position: Int) {
		var streak = 0
		var cur = today
		if( ! getItem(position).lastDayCompleted.isNullOrEmpty() ){
			for( i in getItem(position).lastDayCompleted.size - 1 downTo 0 )
				if(getItem(position).lastDayCompleted[i] + 1 == cur) {
					cur-- ;streak++
				}
		}

		val streakString = when( streak ){
			0 -> "No streak"
			1 -> "$streak day"
			else -> "$streak days"
		}

		holder.bind(getItem(position) , streakString)
	}

	fun getItemAt(position: Int): Task = getItem(position)

	fun set( _today : Long){
		today = _today
	}

	interface OnSwipeRight {
		fun doWork(viewHolder: RecyclerView.ViewHolder): Unit
	}

	interface OnSwipeLeft{
		fun doWork(viewHolder: RecyclerView.ViewHolder): Unit
	}


}