package com.projectdelta.habbit.widget

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.projectdelta.habbit.R
import com.projectdelta.habbit.util.system.lang.getColorFromAttr

class ThemedSwipeRefreshLayout @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs) {
	init {
		// Background
		setProgressBackgroundColorSchemeColor(context.getColorFromAttr(R.attr.colorPrimary))
		// This updates the progress arrow color
		setColorSchemeColors(context.getColorFromAttr(R.attr.colorOnPrimary))
	}
}