package com.projectdelta.habbit.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.projectdelta.habbit.R
import com.projectdelta.habbit.data.local.TasksDatabase
import com.projectdelta.habbit.databinding.FragmentInsightsListBinding
import com.projectdelta.habbit.ui.activity.InsightsActivity
import com.projectdelta.habbit.ui.adapter.EndlessRecyclerViewScrollListener
import com.projectdelta.habbit.ui.adapter.RecyclerItemClickListenr
import com.projectdelta.habbit.ui.adapter.RecyclerViewListAdapter
import com.projectdelta.habbit.ui.adapter.StatesRecyclerViewAdapter
import com.projectdelta.habbit.ui.base.BaseViewBindingFragment
import com.projectdelta.habbit.ui.viewModel.InsightsSharedViewModel
import com.projectdelta.habbit.util.NotFound
import com.projectdelta.habbit.util.lang.TimeUtil
import com.projectdelta.habbit.util.lang.titlesToBulletList
import com.projectdelta.habbit.util.view.LinearLayoutManagerWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class InsightsListFragment : BaseViewBindingFragment<FragmentInsightsListBinding>() {
	companion object{
		fun newInstance() = InsightsListFragment()
		private const val TAG = "InsightsListFragment"
	}

	private lateinit var adapter : RecyclerViewListAdapter

	private val viewModel : InsightsSharedViewModel by activityViewModels()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

		_binding = FragmentInsightsListBinding.inflate(inflater , container , false )

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

		viewModel.getAllDaysPaged.observe(viewLifecycleOwner , {data ->
			adapter.submitData(viewLifecycleOwner.lifecycle ,data)
		})

		binding.insightsListRv.addOnItemTouchListener( RecyclerItemClickListenr(
			context?.applicationContext!!,
			binding.insightsListRv,
			object: RecyclerItemClickListenr.OnItemClickListener{
				override fun onItemClick(view: View, position: Int) {
					adapter.getItemAt(position)?.let{ data ->
						val title = when( data.tasksTitle.size ){
							1 -> "${data.tasksTitle.size} task completed on ${TimeUtil.getPastDateFromOffset((TimeUtil.getTodayFromEpoch() - data.id).toInt())}"
							else -> "${data.tasksTitle.size} tasks completed on ${TimeUtil.getPastDateFromOffset((TimeUtil.getTodayFromEpoch() - data.id).toInt())}"
						}
						val message = data.titlesToBulletList()
						MaterialAlertDialogBuilder(requireActivity()).apply {
							setTitle(title)
							setMessage(message)
							create()
						}.show()
					}
				}

				override fun onItemLongClick(view: View?, position: Int) {}
			}
		))
	}

}