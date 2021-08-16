package com.projectdelta.habbit.ui.main.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.projectdelta.habbit.R
import com.projectdelta.habbit.data.NotFound
import com.projectdelta.habbit.databinding.TodoFragmentBinding
import com.projectdelta.habbit.ui.base.BaseViewBindingFragment
import com.projectdelta.habbit.ui.main.MainActivity
import com.projectdelta.habbit.ui.main.adapter.RecyclerViewTodoAdapter
import com.projectdelta.habbit.ui.main.viewModel.HomeSharedViewModel
import com.projectdelta.habbit.util.*
import com.projectdelta.habbit.util.constant.ICON_SIZE_DP
import com.projectdelta.habbit.util.system.lang.*
import com.projectdelta.habbit.widget.adapter.CustomItemTouchHelperCallback
import com.projectdelta.habbit.widget.adapter.RecyclerItemClickListenr
import com.projectdelta.habbit.widget.adapter.StatesRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TodoFragment : BaseViewBindingFragment<TodoFragmentBinding>() {

	companion object {
		fun newInstance() = TodoFragment()
		private const val TAG = "TodoFragment"
	}

	private val viewModel: HomeSharedViewModel by activityViewModels()
	lateinit var adapter : RecyclerViewTodoAdapter

	private lateinit var activity: MainActivity

	override fun onAttach(context: Context) {
		super.onAttach(context)
		this.activity = context as MainActivity
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = TodoFragmentBinding.inflate(inflater , container , false)

		Log.d(TAG, "onCreateView: $viewModel")

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setRv()

	}

	private fun setRv() {
		binding.todoRv.layoutManager = LinearLayoutManager(activity)
		adapter = RecyclerViewTodoAdapter()

		val emptyView: View =
			layoutInflater.inflate(R.layout.layout_empty_view, binding.todoRv, false)
		emptyView.findViewById<MaterialTextView>(R.id.empty_view_tw_string).text = NotFound.get()

		val statesAdapter =
			StatesRecyclerViewAdapter(
				adapter,
				emptyView,
				emptyView,
				emptyView
			)
		binding.todoRv.adapter = statesAdapter
		statesAdapter.state = StatesRecyclerViewAdapter.STATE_EMPTY

		val divider = DividerItemDecoration(binding.todoRv.context, DividerItemDecoration.VERTICAL)

		// https://stackoverflow.com/a/38909958
		binding.todoRv.viewTreeObserver.addOnPreDrawListener(
			object : ViewTreeObserver.OnPreDrawListener {
				override fun onPreDraw(): Boolean {
					binding.todoRv.viewTreeObserver.removeOnPreDrawListener(this)
					for (i in 0 until binding.todoRv.childCount) {
						binding.todoRv.getChildAt(i).apply {
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

		binding.todoRv.addOnItemTouchListener(RecyclerItemClickListenr(context?.applicationContext!!,
			binding.todoRv,
			object : RecyclerItemClickListenr.OnItemClickListener {
				override fun onItemClick(view: View, position: Int) {
					activity.launchEditActivity(adapter.getItemAt(position))
				}

				override fun onItemLongClick(view: View?, position: Int) {
					view?.findViewById<LinearLayout>(R.id.tasks_ll)?.visibility =
						when (view?.findViewById<LinearLayout>(R.id.tasks_ll)?.visibility) {
							View.VISIBLE -> View.GONE
							else -> View.VISIBLE
						}
				}
			}
		))

		val itemTouchHelper = ItemTouchHelper(CustomItemTouchHelperCallback.Builder().apply {
			flagStartHelper(ItemTouchHelper.START)
			flagEndHelper(ItemTouchHelper.END)
			iconSize(ICON_SIZE_DP.dpToPx * 1.3f)
			leftBackgroundColor(Color.parseColor("#59b2ff"))
			leftIcon(
				convertDrawableToBitmap(
					ContextCompat.getDrawable(
						context?.applicationContext!!,
						R.drawable.ic_skip_next_black_24dp
					)!!
				)
			)
			rightBackgroundColor(Color.parseColor("#b2ff59"))
			rightIcon(
				convertDrawableToBitmap(
					ContextCompat.getDrawable(
						context?.applicationContext!!,
						R.drawable.ic_done_black_24dp
					)!!
				)
			)
			onSwipeListener(object : CustomItemTouchHelperCallback.OnSwipeListener {
				override fun onSwipeLeftToRight(vh: RecyclerView.ViewHolder?) {
					object : RecyclerViewTodoAdapter.OnSwipeRight {
						override fun doWork(viewHolder: RecyclerView.ViewHolder) {
							viewModel.notifyTaskSkipped(adapter.getItemAt(viewHolder.bindingAdapterPosition))
						}
					}.doWork(vh!!)
				}

				override fun onSwipeRightToLeft(vh: RecyclerView.ViewHolder?) {
					object :
						RecyclerViewTodoAdapter.OnSwipeLeft {
						override fun doWork(viewHolder: RecyclerView.ViewHolder) {
							viewModel.notifyTaskDone(adapter.getItemAt(viewHolder.bindingAdapterPosition))
						}
					}.doWork(vh!!)
				}
			})
		}.build())

		lifecycleScope.launch {
			viewModel.getAllTasksSorted().map { data ->
				data
					.unfinishedTill(viewModel.getTodayFromEpoch())
					.tasksBeforeSkipTime(viewModel.getMSFromMidnight())
			}.collect { data ->
				if( data.isNullOrEmpty() ){
					// remove decorations
					statesAdapter.state = StatesRecyclerViewAdapter.STATE_EMPTY
					binding.todoRv.removeItemDecorations()
					itemTouchHelper.attachToRecyclerView(null)
				}
				else{
					// add decoration
					statesAdapter.state = StatesRecyclerViewAdapter.STATE_NORMAL
					binding.todoRv.addItemDecoration(divider)
					itemTouchHelper.attachToRecyclerView(binding.todoRv)
					// update adapter
					adapter.set(viewModel.getTodayFromEpoch())
					adapter.submitList(data)
				}
			}
		}
	}

	override fun onDestroyView() {
		binding.todoRv.adapter = null
		super.onDestroyView()
	}
}