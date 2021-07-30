package com.projectdelta.habbit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projectdelta.habbit.data.entities.Task
import com.projectdelta.habbit.databinding.LayoutRvTodoBinding
import com.projectdelta.habbit.util.lang.isOk

class RecyclerViewTodoAdapter():
	RecyclerView.Adapter< RecyclerViewTodoAdapter.LayoutViewHolder >( ) {
	lateinit var data : MutableList<Task>
	var today = 0L

	inner class LayoutViewHolder( private val binding: LayoutRvTodoBinding ) : RecyclerView.ViewHolder( binding.root ){
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
		if( ! data[position].lastDayCompleted.isNullOrEmpty() ){
			for( i in data[position].lastDayCompleted.size - 1 downTo 0 )
				if(data[position].lastDayCompleted[i] + 1 == cur) {
					cur-- ;streak++
				}
		}

		val streakString = when( streak ){
			0 -> "No streak"
			1 -> "$streak day"
			else -> "$streak days"
		}

		holder.bind(data[position] , streakString)
	}

	override fun getItemCount(): Int {
		if( this::data.isInitialized ) return data.size
		return 0
	}

	fun set( _data : MutableList<Task> , _today : Long){
		today = _today
		if( this::data.isInitialized && data == _data ) return
		data = _data
		notifyDataSetChanged()
	}

	fun dataIsInitialized() = this::data.isInitialized

	interface OnSwipeRight {
		fun doWork(viewHolder: RecyclerView.ViewHolder): Unit
	}

	interface OnSwipeLeft{
		fun doWork(viewHolder: RecyclerView.ViewHolder): Unit
	}
}