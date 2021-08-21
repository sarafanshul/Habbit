package com.projectdelta.habbit.ui.main.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textview.MaterialTextView
import com.projectdelta.habbit.R
import com.projectdelta.habbit.data.NotFound
import com.projectdelta.habbit.databinding.DoneFragmentBinding
import com.projectdelta.habbit.ui.base.BaseViewBindingFragment
import com.projectdelta.habbit.ui.main.MainActivity
import com.projectdelta.habbit.ui.main.adapter.RecyclerViewDoneAdapter
import com.projectdelta.habbit.ui.main.viewModel.HomeSharedViewModel
import com.projectdelta.habbit.util.system.lang.completedTill
import com.projectdelta.habbit.util.system.lang.removeItemDecorations
import com.projectdelta.habbit.widget.adapter.RecyclerItemClickListenr
import com.projectdelta.habbit.widget.adapter.StatesRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DoneFragment : BaseViewBindingFragment<DoneFragmentBinding>() {

	@Suppress("unused")
	companion object {
		fun newInstance() = DoneFragment()
		private const val TAG = "DoneFragment"
	}

	private val viewModel: HomeSharedViewModel by activityViewModels()

	private lateinit var adapter: RecyclerViewDoneAdapter

	private lateinit var activity: MainActivity

	override fun onAttach(context: Context) {
		super.onAttach(context)
		this.activity = context as MainActivity
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = DoneFragmentBinding.inflate(inflater, container, false)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setRv()
	}

	private fun setRv() {
		binding.doneRv.layoutManager = LinearLayoutManager(requireActivity())
		adapter = RecyclerViewDoneAdapter()

		val emptyView: View =
			layoutInflater.inflate(R.layout.layout_empty_view, binding.doneRv, false)
		emptyView.findViewById<MaterialTextView>(R.id.empty_view_tw_string).text = NotFound.get()

		val statesAdapter =
			StatesRecyclerViewAdapter(
				adapter,
				emptyView,
				emptyView,
				emptyView
			)
		binding.doneRv.adapter = statesAdapter
		statesAdapter.state = StatesRecyclerViewAdapter.STATE_EMPTY

		val divider = DividerItemDecoration(binding.doneRv.context, DividerItemDecoration.VERTICAL)

		// https://stackoverflow.com/a/38909958
		binding.doneRv.viewTreeObserver.addOnPreDrawListener(
			object : ViewTreeObserver.OnPreDrawListener {
				override fun onPreDraw(): Boolean {
					binding.doneRv.viewTreeObserver.removeOnPreDrawListener(this)
					for (i in 0 until binding.doneRv.childCount) {
						binding.doneRv.getChildAt(i).apply {
							alpha = 0.0f
							animate().apply {
								alpha(1.0f)
								duration = 300
								startDelay = i * 50L
								start()
							}
						}
					}
					return true
				}
			}
		)

		binding.doneRv.addOnItemTouchListener(RecyclerItemClickListenr(requireActivity(),
			binding.doneRv,
			object : RecyclerItemClickListenr.OnItemClickListener {
				override fun onItemClick(view: View, position: Int) {
					activity.launchEditActivity(adapter.getItemAt(position))
				}

				override fun onItemLongClick(view: View?, position: Int) {

				}
			}
		))

		lifecycleScope.launch {
			viewModel.getAllTasksSorted().map { data ->
				data.completedTill(viewModel.getTodayFromEpoch())
			}.collect { data ->
				if (data.isNullOrEmpty()) {
					// remove decorations
					statesAdapter.state = StatesRecyclerViewAdapter.STATE_EMPTY
					binding.doneRv.removeItemDecorations()
				} else {
					// add decoration
					statesAdapter.state = StatesRecyclerViewAdapter.STATE_NORMAL
					binding.doneRv.addItemDecoration(divider)
					// update adapter
					adapter.set(viewModel.getTodayFromEpoch())
					adapter.submitList(data)
				}
			}
		}
	}

	override fun onDestroyView() {
		binding.doneRv.adapter = null
		super.onDestroyView()
	}
}