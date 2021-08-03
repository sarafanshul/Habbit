package com.projectdelta.habbit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projectdelta.habbit.data.entities.Task
import com.projectdelta.habbit.databinding.LayoutRvSkipBinding

class RecyclerViewSkipAdapter():
	RecyclerView.Adapter< RecyclerViewSkipAdapter.LayoutViewHolder >( ) {
	lateinit var data : MutableList<Task>
	var today = 0L

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

		var streakString = when(val streak = data[position].skipTill - today){
			0L -> "Skipped today"
			1L -> "$streak day"
			else -> "$streak days"
		}
		if( data[position].isSkipAfterEnabled )
			streakString = "Missed today"

		holder.bind(data[position], streakString)
	}

	override fun getItemCount(): Int {
		if (this::data.isInitialized) return data.size
		return 0
	}

	fun set(_data: MutableList<Task>, _today: Long) {
		today = _today
		if( this::data.isInitialized && data == _data ) return
		data = _data
		notifyDataSetChanged()
	}

	interface OnSwipeRight{
		fun doWork(viewHolder: RecyclerView.ViewHolder): Unit
	}

	fun dataIsInitialized() = this::data.isInitialized

	fun insertData( task: Task , position: Int ){
		data.add(position , task)
		notifyItemInserted(position)
	}

	fun deleteData( position: Int ) : Task {
		val x = data.removeAt( position )
		notifyItemRemoved( position )
		return x
	}

}