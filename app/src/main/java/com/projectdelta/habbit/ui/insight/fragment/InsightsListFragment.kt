package com.projectdelta.habbit.ui.insight.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.projectdelta.habbit.databinding.FragmentInsightsListBinding
import com.projectdelta.habbit.ui.base.BaseViewBindingFragment
import com.projectdelta.habbit.ui.insight.InsightsSharedViewModel
import com.projectdelta.habbit.ui.insight.adapter.RecyclerViewListAdapter
import com.projectdelta.habbit.util.system.lang.TimeUtil
import com.projectdelta.habbit.util.system.lang.titlesToBulletList
import com.projectdelta.habbit.widget.adapter.RecyclerItemClickListenr

class InsightsListFragment : BaseViewBindingFragment<FragmentInsightsListBinding>() {

	@Suppress("unused")
	companion object {
		fun newInstance() = InsightsListFragment()
		private const val TAG = "InsightsListFragment"
	}

	private lateinit var adapter: RecyclerViewListAdapter

	private val viewModel: InsightsSharedViewModel by activityViewModels()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {

		_binding = FragmentInsightsListBinding.inflate(inflater, container, false)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setLayout()
	}

	private fun setLayout() {
		binding.insightsListRv.layoutManager = LinearLayoutManager(requireActivity())
		adapter = RecyclerViewListAdapter()

		binding.insightsListRv.adapter = adapter

		viewModel.getAllDaysPaged.observe(viewLifecycleOwner, { data ->
			adapter.submitData(viewLifecycleOwner.lifecycle, data)
		})

		binding.insightsListRv.addOnItemTouchListener(RecyclerItemClickListenr(
			context?.applicationContext!!,
			binding.insightsListRv,
			object : RecyclerItemClickListenr.OnItemClickListener {
				override fun onItemClick(view: View, position: Int) {
					adapter.getItemAt(position)?.let { data ->
						val title = when (data.tasksTitle.size) {
							1 -> "${data.tasksTitle.size} task completed on ${
								TimeUtil.getPastDateFromOffset(
									(TimeUtil.getTodayFromEpoch() - data.id).toInt()
								)
							}"
							else -> "${data.tasksTitle.size} tasks completed on ${
								TimeUtil.getPastDateFromOffset(
									(TimeUtil.getTodayFromEpoch() - data.id).toInt()
								)
							}"
						}
						val message = data.titlesToBulletList()
						MaterialDialog(requireActivity() , BottomSheet(LayoutMode.WRAP_CONTENT)).show{
							title(text = title)
							message(text = message)
							cornerRadius(16f)
						}
					}
				}

				override fun onItemLongClick(view: View?, position: Int) {}
			}
		))
	}

}