package com.projectdelta.habbit.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class InsightsViewPagerAdapter (supportFragmentManager: FragmentManager) :
	FragmentPagerAdapter(supportFragmentManager , BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

	private val mFragmentList = ArrayList<Fragment>()
	val mFragmentTitleList = ArrayList<String>()

	override fun getCount() = mFragmentList.size

	override fun getItem(position: Int) = mFragmentList[position]

	override fun getPageTitle(position: Int) = mFragmentTitleList[position]

	fun addFragment(F: Fragment, T: String) {
		mFragmentList.add(F)
		mFragmentTitleList.add(T)
		notifyDataSetChanged()
	}
}