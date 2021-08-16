package com.projectdelta.habbit.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeViewPagerAdapter(fragmentActivity: FragmentActivity) :
	FragmentStateAdapter(fragmentActivity) {

	private val mFragmentList = ArrayList<Fragment>()
	private val mFragmentTitleList = ArrayList<String>()

	override fun getItemCount(): Int  = mFragmentList.size

	override fun createFragment(position: Int): Fragment = mFragmentList[position]

	fun addFragment(F: Fragment, T: String) {
		mFragmentList.add(F)
		mFragmentTitleList.add(T)
		notifyDataSetChanged()
	}
}