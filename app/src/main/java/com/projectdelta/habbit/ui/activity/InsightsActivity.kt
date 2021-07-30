package com.projectdelta.habbit.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.projectdelta.habbit.R
import com.projectdelta.habbit.ui.adapter.InsightsViewPagerAdapter
import com.projectdelta.habbit.databinding.ActivityInsightsBinding
import com.projectdelta.habbit.ui.fragment.InsightsCalendarFragment
import com.projectdelta.habbit.ui.fragment.InsightsListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InsightsActivity : AppCompatActivity() {

	lateinit var binding : ActivityInsightsBinding
	lateinit var adapter : InsightsViewPagerAdapter

	companion object{
		val tabIconsOutlined = listOf( R.drawable.ic_fact_check_black_outline_24dp , R.drawable.ic_date_range_black_outline_24dp )
		val tabIconsFilled = listOf( R.drawable.ic_fact_check_black_filled_24dp , R.drawable.ic_date_range_black_filled_24dp )
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityInsightsBinding.inflate( layoutInflater )

		setContentView( binding.root )

		/**
		 * set viewpager here with two tabs
		 * 1) recent -> infinite loading loading RecyclerView
		 * 2) Calender with markings
		 */

		setLayout()

	}

	private fun setLayout() {
		adapter = InsightsViewPagerAdapter( supportFragmentManager )
		adapter.addFragment( InsightsListFragment() , "" )
		adapter.addFragment( InsightsCalendarFragment() , "" )

		binding.insightsVp.adapter = adapter

		binding.insightsTabs.setupWithViewPager(binding.insightsVp)
		setTabIcons()

		binding.insightsVp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
			override fun onPageScrolled(
				position: Int,
				positionOffset: Float,
				positionOffsetPixels: Int
			) {}

			override fun onPageSelected(position: Int) {
				for( i in 0 until binding.insightsTabs.tabCount ){
					if( i == position )
						binding.insightsTabs.getTabAt( i )!!.setIcon( tabIconsFilled[i] )
					else
						binding.insightsTabs.getTabAt( i )!!.setIcon( tabIconsOutlined[i] )
				}
			}

			override fun onPageScrollStateChanged(state: Int) {}
		} )

	}

	private fun setTabIcons() {
		for (i in 0 until binding.insightsTabs.tabCount)
			binding.insightsTabs.getTabAt(i)!!.setIcon(tabIconsOutlined[i])
		binding.insightsTabs.getTabAt(0)!!.setIcon(tabIconsFilled[0])
	}

}