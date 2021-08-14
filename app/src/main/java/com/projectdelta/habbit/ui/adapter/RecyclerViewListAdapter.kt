package com.projectdelta.habbit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.projectdelta.habbit.data.model.entities.Day
import com.projectdelta.habbit.data.model.entities.Task
import com.projectdelta.habbit.databinding.LayoutRvListBinding
import com.projectdelta.habbit.util.lang.TimeUtil
import com.projectdelta.habbit.util.lang.titlesToBulletList

class RecyclerViewListAdapter:
	PagingDataAdapter< Day , RecyclerViewListAdapter.LayoutViewHolder> ( DIFF_CALLBACK ){

	companion object{
		val DIFF_CALLBACK : DiffUtil.ItemCallback<Day> = object : DiffUtil.ItemCallback<Day>(){
			override fun areItemsTheSame(oldItem: Day, newItem: Day): Boolean {
				return oldItem.equals( newItem )
			}

			override fun areContentsTheSame(oldItem: Day, newItem: Day): Boolean {
				return oldItem.tasksID.size == newItem.tasksID.size &&
						oldItem.tasksTitle.size == newItem.tasksTitle.size
			}
		}
	}

	inner class LayoutViewHolder( private val binding: LayoutRvListBinding) : RecyclerView.ViewHolder(binding.root){

		fun bind( D : Day ){
			with(binding){
				tvDate.text = TimeUtil.getPastDateFromOffset((TimeUtil.getTodayFromEpoch() - D.id).toInt())
				tvData.text = D.titlesToBulletList(maxLength = 40 ,maxLine = 5)
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayoutViewHolder {
		val binding = LayoutRvListBinding.inflate( LayoutInflater.from(parent.context) , parent , false )
		return LayoutViewHolder( binding )
	}

	override fun onBindViewHolder(holder: LayoutViewHolder, position: Int) {
		getItem(position)?.let { holder.bind(it) }
	}

	fun getItemAt(position: Int) = getItem(position)
}