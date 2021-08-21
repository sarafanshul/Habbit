package com.projectdelta.habbit.ui.insight

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.projectdelta.habbit.R
import com.projectdelta.habbit.databinding.ActivityInsightsBinding
import com.projectdelta.habbit.ui.base.BaseViewBindingActivity
import com.projectdelta.habbit.ui.insight.adapter.InsightsViewPagerAdapter
import com.projectdelta.habbit.ui.insight.fragment.InsightsCalendarFragment
import com.projectdelta.habbit.ui.insight.fragment.InsightsListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InsightsActivity : BaseViewBindingActivity<ActivityInsightsBinding>() {

	lateinit var adapter: InsightsViewPagerAdapter

	private val tabIconsOutlined = listOf(
		R.drawable.ic_fact_check_black_outline_24dp,
		R.drawable.ic_date_range_black_outline_24dp
	)
	private val tabIconsFilled = listOf(
		R.drawable.ic_fact_check_black_filled_24dp,
		R.drawable.ic_date_range_black_filled_24dp
	)

	private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
		override fun onPageSelected(position: Int) {
			super.onPageSelected(position)
			for (i in 0 until binding.insightsTabs.tabCount) {
				if (i == position)
					binding.insightsTabs.getTabAt(i)!!.setIcon(tabIconsFilled[i])
				else
					binding.insightsTabs.getTabAt(i)!!.setIcon(tabIconsOutlined[i])
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		_binding = ActivityInsightsBinding.inflate(layoutInflater)

		setContentView(binding.root)

		setLayout()

	}

	private fun setLayout() {
		adapter = InsightsViewPagerAdapter(this)
		adapter.addFragment(InsightsListFragment())
		adapter.addFragment(InsightsCalendarFragment())

		binding.insightsVp.adapter = adapter

		TabLayoutMediator(binding.insightsTabs, binding.insightsVp) { tab, position ->
			tab.text = ""
			if (position != 0) tab.setIcon(tabIconsOutlined[position])
			else tab.setIcon(tabIconsFilled[position])
		}.attach()

		binding.insightsVp.registerOnPageChangeCallback(onPageChangeCallback)

	}

	override fun onDestroy() {
		binding.insightsVp.unregisterOnPageChangeCallback(onPageChangeCallback)
		super.onDestroy()
	}

}