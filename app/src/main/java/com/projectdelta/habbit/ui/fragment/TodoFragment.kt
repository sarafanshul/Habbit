package com.projectdelta.habbit.ui.fragment

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.projectdelta.habbit.R
import com.projectdelta.habbit.adapter.RecyclerViewTodoAdapter
import com.projectdelta.habbit.constant.ICON_SIZE_DP
import com.projectdelta.habbit.databinding.TodoFragmentBinding
import com.projectdelta.habbit.ui.MainActivity
import com.projectdelta.habbit.util.*
import com.projectdelta.habbit.util.lang.convertDrawableToBitmap
import com.projectdelta.habbit.util.lang.dpToPx
import com.projectdelta.habbit.util.lang.tasksBeforeSkipTime
import com.projectdelta.habbit.util.lang.unfinishedTill
import com.projectdelta.habbit.util.view.CustomItemTouchHelperCallback
import com.projectdelta.habbit.util.view.RecyclerItemClickListenr
import com.projectdelta.habbit.util.view.StatesRecyclerViewAdapter
import com.projectdelta.habbit.viewModel.HomeSharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoFragment : Fragment() {

	companion object {
		fun newInstance() = TodoFragment()
	}

	private var _binding : TodoFragmentBinding ?= null
	private val binding
		get() = _binding!!

	private val viewModel: HomeSharedViewModel by activityViewModels()
	lateinit var adapter : RecyclerViewTodoAdapter

	private lateinit var activity: MainActivity

	override fun onAttach(activity : Activity) {
		super.onAttach(activity)
		this.activity = activity as MainActivity
	}

	private val TAG = "TodoFragment"

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		_binding = TodoFragmentBinding.inflate(inflater , container , false)

		Log.d(TAG, "onCreateView: $viewModel")

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setRv()

	}
	override fun onDestroy() {
		_binding = null
		super.onDestroy()
	}


	private fun setRv() {
		binding.todoRv.layoutManager = LinearLayoutManager(activity)
		adapter = RecyclerViewTodoAdapter()

		val emptyView : View = layoutInflater.inflate( R.layout.layout_empty_view , binding.todoRv , false )
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

		val divider = DividerItemDecoration( binding.todoRv.context , DividerItemDecoration.VERTICAL)
		binding.todoRv.addItemDecoration( divider )


		// https://stackoverflow.com/a/38909958
		binding.todoRv.viewTreeObserver.addOnPreDrawListener (
			object : ViewTreeObserver.OnPreDrawListener {
				override fun onPreDraw(): Boolean {
					binding.todoRv.viewTreeObserver.removeOnPreDrawListener(this)
					for( i in 0 until binding.todoRv.childCount){
						binding.todoRv.getChildAt( i ).apply {
							alpha = 0.0f
							animate().apply {
								alpha(1.0f)
								setDuration(300)
								setStartDelay(i*50L)
								start()
							}
						}
					}
					return true
				}
			}
		)

		binding.todoRv.addOnItemTouchListener(RecyclerItemClickListenr( context?.applicationContext!! ,
			binding.todoRv ,
			object : RecyclerItemClickListenr.OnItemClickListener{
				override fun onItemClick(view: View, position: Int) {
					if( adapter.dataIsInitialized() )
						activity.launchEditActivity( adapter.data[position] )
				}

				override fun onItemLongClick(view: View?, position: Int) {

				}
			}
		))

		val itemTouchHelper = ItemTouchHelper(CustomItemTouchHelperCallback.Builder().apply {
			flagStartHelper(ItemTouchHelper.START)
			flagEndHelper(ItemTouchHelper.END)
			iconSize(ICON_SIZE_DP.dpToPx * 1.3f)
			leftBackgroundColor( Color.parseColor("#59b2ff") )
			leftIcon( convertDrawableToBitmap( ContextCompat.getDrawable( context?.applicationContext!!, R.drawable.ic_skip_next_black_24dp)!! ) )
			rightBackgroundColor(Color.parseColor( "#b2ff59"))
			rightIcon( convertDrawableToBitmap( ContextCompat.getDrawable( context?.applicationContext!!, R.drawable.ic_done_black_24dp)!! ) )
			onSwipeListener( object : CustomItemTouchHelperCallback.OnSwipeListener{
				override fun onSwipeLeftToRight(vh: RecyclerView.ViewHolder?) {object : RecyclerViewTodoAdapter.OnSwipeRight{
					override fun doWork(viewHolder: RecyclerView.ViewHolder) {
						val position = viewHolder.adapterPosition
						val T = adapter.data.removeAt(position)
						adapter.notifyItemRemoved(position)
						viewModel.notifyTaskSkipped(T)
					}
				}.doWork(vh!!) }

				override fun onSwipeRightToLeft(vh: RecyclerView.ViewHolder?) {object :RecyclerViewTodoAdapter.OnSwipeLeft{
					override fun doWork(viewHolder: RecyclerView.ViewHolder) {
						val position = viewHolder.adapterPosition
						val T = adapter.data.removeAt(position)
						adapter.notifyItemRemoved(position)
						viewModel.notifyTaskDone(T)
					}
				}.doWork(vh!!) }
			})
		}.build())

		viewModel.data.observe(viewLifecycleOwner , {data ->
			if( data.isNullOrEmpty() ) {
				statesAdapter.state = StatesRecyclerViewAdapter.STATE_EMPTY
				binding.todoRv.removeItemDecoration( divider )
				return@observe
			}
			Log.d("DATA" , "${viewModel.getToday()}")

			val undoneData = data.tasksBeforeSkipTime(viewModel.getMSFromMidnight()).unfinishedTill( viewModel.getToday() )

			Log.d("DATA" , undoneData.toMutableList().toString())

			if( undoneData.isNullOrEmpty() ) {
				statesAdapter.state = StatesRecyclerViewAdapter.STATE_EMPTY
				binding.todoRv.removeItemDecoration( divider )
				itemTouchHelper.attachToRecyclerView(null)
			}else {
				statesAdapter.state = StatesRecyclerViewAdapter.STATE_NORMAL
				binding.todoRv.addItemDecoration( divider )
				itemTouchHelper.attachToRecyclerView( binding.todoRv )
				adapter.set(
					undoneData.toMutableList(),
					viewModel.getToday()
				)
			}
		})
	}

}