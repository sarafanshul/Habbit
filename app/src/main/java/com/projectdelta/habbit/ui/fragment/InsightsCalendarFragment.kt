package com.projectdelta.habbit.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.projectdelta.habbit.R
import com.projectdelta.habbit.databinding.FragmentInsightsCalendarBinding
import com.projectdelta.habbit.ui.activity.InsightsActivity
import com.projectdelta.habbit.ui.base.BaseViewBindingFragment
import com.projectdelta.habbit.ui.viewModel.InsightsSharedViewModel
import com.projectdelta.habbit.util.NotFound
import com.projectdelta.habbit.util.lang.TimeUtil
import com.projectdelta.habbit.util.lang.titlesToBulletList
import dagger.hilt.android.AndroidEntryPoint
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class InsightsCalendarFragment : BaseViewBindingFragment<FragmentInsightsCalendarBinding>() {

	companion object{
		fun newInstance() = InsightsCalendarFragment()
		private const val TAG = "InsightsCalendarFragment"
		private const val MAX_CALENDAR_DOTS = 7
	}

	private val viewModel : InsightsSharedViewModel by activityViewModels()

	private lateinit var activity : InsightsActivity
	override fun onAttach(activity: Activity) {
		super.onAttach(activity)
		this.activity = activity as InsightsActivity
	}

	@SuppressLint("FieldSiteTargetOnQualifierAnnotation")
	@field:[Inject Named("COLORS_ARRAY")]
	lateinit var COLORS : IntArray

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {

		_binding = FragmentInsightsCalendarBinding.inflate(inflater , container , false )

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setLayout()
	}

	private fun setLayout() {
		val calendar = Calendar.getInstance()
		val initialDate = CalendarDate( calendar.time )

		calendar.set(2021 , Calendar.JANUARY , 1)
		val minDate = CalendarDate( calendar.time )

		calendar.set( Calendar.getInstance().get(Calendar.YEAR) + 1 , Calendar.JANUARY , 1 )
		val maxDate = CalendarDate( calendar.time )


		binding.insightsCalV.setupCalendar(
			initialDate, minDate, maxDate,
			selectionMode = CalendarView.SelectionMode.NONE,
			firstDayOfWeek = Calendar.MONDAY,
			showYearSelectionView = true,
		)

		binding.insightsCalV.setDateCellBackgroundRes(R.drawable.custom_calendar_drawable)
		binding.insightsCalV.setDateCellBackgroundTintRes(R.color.custom_date_cell_background_color)

		viewModel.getAllDays.observe(viewLifecycleOwner , {X ->
			if( X.isNullOrEmpty() ) return@observe
			val data = X.sortedBy { it.id }
			binding.insightsCalV.datesIndicators = data.flatMap { day ->
				day.tasksID.take(MAX_CALENDAR_DOTS).map {
					object: CalendarView.DateIndicator{
						override val color: Int
							get() = COLORS[ (it % COLORS.size).toInt() ]
						override val date: CalendarDate
							get() = CalendarDate( TimeUtil.daysToMilliseconds( day.id ) )
					}
				}
			}

			binding.insightsCalV.onDateClickListener = { date ->
				val cur = data.binarySearchBy( TimeUtil.millisecondsToDays(date.timeInMillis) + 1 ){ it.id }
				if( cur >= 0 ){
					val title = when( data[cur].tasksTitle.size ){
						1 -> "${data[cur].tasksTitle.size} task completed on ${TimeUtil.getMonth( date.month + 1 )} ${date.dayOfMonth}"
						else -> "${data[cur].tasksTitle.size} tasks completed on ${TimeUtil.getMonth( date.month + 1 )} ${date.dayOfMonth}"
					}
					val message = data[cur].titlesToBulletList()
					MaterialAlertDialogBuilder(activity).apply{
						setTitle( title )
						setMessage(message)
						create()
					}.show()
				}
				else {
					val title = "No tasks completed on ${TimeUtil.getMonth( date.month + 1 )} ${date.dayOfMonth}"
					val message = NotFound.get()
					MaterialAlertDialogBuilder(activity).apply{
						setTitle( title )
						setMessage(message)
						create()
					}.show()
				}
			}
		})

	}

}