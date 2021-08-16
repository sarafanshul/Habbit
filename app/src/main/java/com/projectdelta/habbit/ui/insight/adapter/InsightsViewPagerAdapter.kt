package com.projectdelta.habbit.ui.insight.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class InsightsViewPagerAdapter (fragmentActivity: FragmentActivity) :
	FragmentStateAdapter(fragmentActivity) {

	private val mFragmentList = ArrayList<Fragment>()

	override fun getItemCount(): Int = mFragmentList.size

	override fun createFragment(position: Int): Fragment = mFragmentList[position]

	fun addFragment(F: Fragment) {
		mFragmentList.add(F)
		notifyDataSetChanged()
	}
}