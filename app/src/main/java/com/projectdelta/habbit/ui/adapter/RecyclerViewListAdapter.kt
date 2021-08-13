package com.projectdelta.habbit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projectdelta.habbit.data.model.entities.Day
import com.projectdelta.habbit.databinding.LayoutRvListBinding
import com.projectdelta.habbit.util.lang.TimeUtil
import com.projectdelta.habbit.util.lang.titlesToBulletList

class RecyclerViewListAdapter():
	RecyclerView.Adapter<RecyclerViewListAdapter.LayoutViewHolder> ( ){

	lateinit var data : MutableList<Day>

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
		holder.bind( data[position] )
	}

	override fun getItemCount(): Int {
		if( this::data.isInitialized ) return data.size
		return 0
	}


	fun addAll( addData : MutableList<Day> ) {
		if (this::data.isInitialized) {
			val curSize = itemCount
			data.addAll(addData)
			notifyItemRangeInserted(curSize, data.size - 1)
		}
		else {
			data = addData
			notifyDataSetChanged()
		}
	}

	fun dataIsInitialized() = this::data.isInitialized
}