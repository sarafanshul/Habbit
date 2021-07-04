package com.projectdelta.habbit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projectdelta.habbit.data.entities.Task
import com.projectdelta.habbit.databinding.LayoutRvDoneBinding


class RecyclerViewDoneAdapter():
	RecyclerView.Adapter< RecyclerViewDoneAdapter.LayoutViewHolder >( ) {
	lateinit var data : List<Task>
	var today = 0L

	inner class LayoutViewHolder( private val binding: LayoutRvDoneBinding) : RecyclerView.ViewHolder( binding.root ){
		fun bind(T : Task , streakString : String){
			with(binding){
				binding.tasksTwId.text = T.taskName
				binding.tasksTwStreak.text = streakString
				binding.tasksTwRating.rating = T.importance
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayoutViewHolder {
		val binding = LayoutRvDoneBinding.inflate( LayoutInflater.from( parent.context ) , parent , false )
		return LayoutViewHolder( binding )
	}

	override fun onBindViewHolder(holder: LayoutViewHolder, position: Int) {
		var streak = 0
		var cur = today
		if( ! data[position].lastDayCompleted.isNullOrEmpty() ){
			for( i in data[position].lastDayCompleted.size - 1 downTo 0 )
				if(data[position].lastDayCompleted[i] == cur) {
					cur-- ;streak++
				}
		}
		val streakString = when( streak ){
			0 -> "No streak"
			1 -> "${streak} day"
			else -> "${streak} days"
		}
		holder.bind(data[position] , streakString)
	}

	override fun getItemCount(): Int {
		if( this::data.isInitialized ) return data.size
		return 0
	}

	fun set(_data : List<Task>, _today : Long){
		data = emptyList()
		today = _today
		data = _data
		notifyDataSetChanged()
	}

}