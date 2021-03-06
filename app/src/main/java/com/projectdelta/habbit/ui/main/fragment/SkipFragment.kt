package com.projectdelta.habbit.ui.main.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.projectdelta.habbit.R
import com.projectdelta.habbit.data.NotFound
import com.projectdelta.habbit.databinding.SkipFragmentBinding
import com.projectdelta.habbit.ui.base.BaseViewBindingFragment
import com.projectdelta.habbit.ui.main.MainActivity
import com.projectdelta.habbit.ui.main.adapter.RecyclerViewSkipAdapter
import com.projectdelta.habbit.ui.main.viewModel.HomeSharedViewModel
import com.projectdelta.habbit.util.constant.ICON_SIZE_DP
import com.projectdelta.habbit.util.system.lang.convertDrawableToBitmap
import com.projectdelta.habbit.util.system.lang.dpToPx
import com.projectdelta.habbit.util.system.lang.removeItemDecorations
import com.projectdelta.habbit.util.system.lang.skippedTill
import com.projectdelta.habbit.widget.adapter.CustomItemTouchHelperCallback
import com.projectdelta.habbit.widget.adapter.RecyclerItemClickListenr
import com.projectdelta.habbit.widget.adapter.StatesRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SkipFragment : BaseViewBindingFragment<SkipFragmentBinding>() {

	@Suppress("unused")
	companion object {
		fun newInstance() = SkipFragment()
		private const val TAG = "SkipFragment"
	}

	private val viewModel: HomeSharedViewModel by activityViewModels()
	lateinit var adapter: RecyclerViewSkipAdapter
	private lateinit var activity: MainActivity

	override fun onAttach(context: Context) {
		super.onAttach(context)
		this.activity = context as MainActivity
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = SkipFragmentBinding.inflate(inflater, container, false)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setRv()
	}

	private fun setRv() {
		binding.skipRv.layoutManager = LinearLayoutManager(activity)

		adapter = RecyclerViewSkipAdapter()

		val emptyView: View =
			layoutInflater.inflate(R.layout.layout_empty_view, binding.skipRv, false)
		emptyView.findViewById<MaterialTextView>(R.id.empty_view_tw_string).text = NotFound.get()

		val statesAdapter =
			StatesRecyclerViewAdapter(
				adapter,
				emptyView,
				emptyView,
				emptyView
			)
		binding.skipRv.adapter = statesAdapter
		statesAdapter.state = StatesRecyclerViewAdapter.STATE_EMPTY

		val divider = DividerItemDecoration(binding.skipRv.context, DividerItemDecoration.VERTICAL)

		// https://stackoverflow.com/a/38909958
		binding.skipRv.viewTreeObserver.addOnPreDrawListener(
			object : ViewTreeObserver.OnPreDrawListener {
				override fun onPreDraw(): Boolean {
					binding.skipRv.viewTreeObserver.removeOnPreDrawListener(this)
					for (i in 0 until binding.skipRv.childCount) {
						binding.skipRv.getChildAt(i).apply {
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

		binding.skipRv.addOnItemTouchListener(RecyclerItemClickListenr(context?.applicationContext!!,
			binding.skipRv,
			object : RecyclerItemClickListenr.OnItemClickListener {
				override fun onItemClick(view: View, position: Int) {
					(activity).launchEditActivity(adapter.getItemAt(position))
				}

				override fun onItemLongClick(view: View?, position: Int) {

				}
			}
		))

		val itemTouchHelper = ItemTouchHelper(CustomItemTouchHelperCallback.Builder().apply {
			flagEndHelper(ItemTouchHelper.END)
			iconSize(ICON_SIZE_DP.dpToPx * 1.3f)
			leftBackgroundColor(Color.parseColor("#ff6961"))
			cornerRadius(12.dpToPx.toInt())
			leftIcon(
				convertDrawableToBitmap(
					ContextCompat.getDrawable(
						context?.applicationContext!!,
						R.drawable.ic_delete_black_24dp
					)!!
				)
			)
			rightBackgroundColor(Color.parseColor("#B33F40"))
			rightIcon(
				convertDrawableToBitmap(
					ContextCompat.getDrawable(
						context?.applicationContext!!,
						R.drawable.ic_delete_black_24dp
					)!!
				)
			)
			onSwipeListener(object : CustomItemTouchHelperCallback.OnSwipeListener {
				override fun onSwipeLeftToRight(vh: RecyclerView.ViewHolder?) {
					object : RecyclerViewSkipAdapter.OnSwipeRight {
						override fun doWork(viewHolder: RecyclerView.ViewHolder) {
							val x = adapter.getItemAt(viewHolder.bindingAdapterPosition)
							viewModel.delete(x)
							Snackbar.make(
								requireActivity().findViewById(R.id.main_cl),
								"A task deleted!",
								Snackbar.LENGTH_LONG
							).apply {
								anchorView = requireActivity().findViewById(R.id.main_fab_create)
								setActionTextColor(requireActivity().getColor(R.color.md_blue_A400))
								setBackgroundTint(requireActivity().getColor(R.color.md_grey_900))
								setTextColor(requireActivity().getColor(R.color.md_white_1000_54))
								setAction("Undo") {
									viewModel.insertTask(x)
								}
							}.show()
						}
					}.doWork(vh!!)
				}

				override fun onSwipeRightToLeft(vh: RecyclerView.ViewHolder?) {}
			})
		}.build())

		lifecycleScope.launch {
			viewModel.getAllTasksSorted().map { data ->
				data.skippedTill(viewModel.getTodayFromEpoch(), viewModel.getMSFromMidnight())
			}.collect { data ->
				if (data.isNullOrEmpty()) {
					// remove decorations
					statesAdapter.state = StatesRecyclerViewAdapter.STATE_EMPTY
					binding.skipRv.removeItemDecorations()
					itemTouchHelper.attachToRecyclerView(null)
				} else {
					// add decoration
					statesAdapter.state = StatesRecyclerViewAdapter.STATE_NORMAL
					binding.skipRv.addItemDecoration(divider)
					itemTouchHelper.attachToRecyclerView(binding.skipRv)
					// update adapter
					adapter.set(viewModel.getTodayFromEpoch())
					adapter.submitList(data)
				}
			}
		}
	}

	override fun onDestroyView() {
		binding.skipRv.adapter = null
		super.onDestroyView()
	}
}