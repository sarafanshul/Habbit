package com.projectdelta.habbit.ui.insight.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.projectdelta.habbit.R
import com.projectdelta.habbit.data.NotFound
import com.projectdelta.habbit.data.model.entities.Day
import com.projectdelta.habbit.databinding.FragmentInsightsCalendarBinding
import com.projectdelta.habbit.ui.base.BaseViewBindingFragment
import com.projectdelta.habbit.ui.insight.InsightsSharedViewModel
import com.projectdelta.habbit.util.system.lang.TimeUtil
import com.projectdelta.habbit.util.system.lang.darkToast
import com.projectdelta.habbit.util.system.lang.titlesToBulletList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class InsightsCalendarFragment : BaseViewBindingFragment<FragmentInsightsCalendarBinding>() {

	companion object {
		fun newInstance() = InsightsCalendarFragment()
		private const val TAG = "InsightsCalendarFragment"
		private const val MAX_CALENDAR_DOTS = 7
	}

	private val viewModel: InsightsSharedViewModel by activityViewModels()

	@SuppressLint("FieldSiteTargetOnQualifierAnnotation")
	@field:[Inject Named("COLORS_ARRAY")]
	lateinit var COLORS: IntArray

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {

		_binding = FragmentInsightsCalendarBinding.inflate(inflater, container, false)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setLayout()
	}

	private fun setLayout() {
		val calendar = Calendar.getInstance()
		val initialDate = CalendarDate(calendar.time)

		calendar.add(Calendar.YEAR, -1)
		val minDate = CalendarDate(calendar.time)

		calendar.add(Calendar.YEAR, 2)
		val maxDate = CalendarDate(calendar.time)


		binding.insightsCalV.setupCalendar(
			initialDate, minDate, maxDate,
			selectionMode = CalendarView.SelectionMode.NONE,
			firstDayOfWeek = Calendar.MONDAY,
			showYearSelectionView = true,
		)

		binding.insightsCalV.setDateCellBackgroundRes(R.drawable.custom_calendar_drawable)
		binding.insightsCalV.setDateCellBackgroundTintRes(R.color.custom_date_cell_background_color)

		try {
			lifecycleScope.launch {
				viewModel.getAllDays().map { data ->
					Pair(data, data.flatMap { day ->
						day.tasksID.take(MAX_CALENDAR_DOTS).map {
							object : CalendarView.DateIndicator {
								override val color: Int
									get() = COLORS[(it % COLORS.size).toInt()]
								override val date: CalendarDate
									get() = CalendarDate(TimeUtil.daysToMilliseconds(day.id))
							}
						}
					})
				}.collect { data ->
					binding.insightsCalV.datesIndicators = data.second
					updateCalendar(data.first)
				}
			}
		} catch (e: Exception) {
			Log.e(TAG, "setLayout: Error occurred while parsing Flow", e)
			requireActivity().darkToast("Error occurred! , Please try again")
		}
	}

	private fun updateCalendar(data: List<Day>) {
		if (data.isNullOrEmpty()) return

		binding.insightsCalV.onDateClickListener = { date ->
			val cur =
				data.binarySearchBy(TimeUtil.millisecondsToDays(date.timeInMillis) + 1) { it.id }
			if (cur >= 0) {
				val title = when (data[cur].tasksTitle.size) {
					1 -> "${data[cur].tasksTitle.size} task completed on ${TimeUtil.getMonth(date.month + 1)} ${date.dayOfMonth}"
					else -> "${data[cur].tasksTitle.size} tasks completed on ${
						TimeUtil.getMonth(
							date.month + 1
						)
					} ${date.dayOfMonth}"
				}
				val message = data[cur].titlesToBulletList()
				MaterialDialog(requireActivity() , BottomSheet(LayoutMode.WRAP_CONTENT)).show {
					title(text = title)
					message(text = message)
					cornerRadius(16f)
				}
			} else {
				val title =
					"No tasks completed on ${TimeUtil.getMonth(date.month + 1)} ${date.dayOfMonth}"
				val message = NotFound.get()
				MaterialAlertDialogBuilder(requireActivity()).apply {
					setTitle(title)
					setMessage(message)
					create()
				}.show()
			}
		}
	}

}