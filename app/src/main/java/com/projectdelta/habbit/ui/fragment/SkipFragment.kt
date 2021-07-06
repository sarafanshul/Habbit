package com.projectdelta.habbit.ui.fragment

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.projectdelta.habbit.ui.MainActivity
import com.projectdelta.habbit.R
import com.projectdelta.habbit.adapter.RecyclerViewSkipAdapter
import com.projectdelta.habbit.constant.ICON_SIZE_DP
import com.projectdelta.habbit.databinding.SkipFragmentBinding
import com.projectdelta.habbit.util.NotFound
import com.projectdelta.habbit.util.lang.convertDrawableToBitmap
import com.projectdelta.habbit.util.lang.dpToPx
import com.projectdelta.habbit.util.view.RecyclerItemClickListenr
import com.projectdelta.habbit.util.view.StatesRecyclerViewAdapter
import com.projectdelta.habbit.util.lang.skippedTill
import com.projectdelta.habbit.util.view.CustomItemTouchHelperCallback
import com.projectdelta.habbit.viewModel.HomeSharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SkipFragment : Fragment() {

	companion object {
		fun newInstance() = SkipFragment()
	}

	private var _binding : SkipFragmentBinding ?= null
	private val binding
		get() = _binding!!

	private val viewModel: HomeSharedViewModel by activityViewModels()
	lateinit var adapter : RecyclerViewSkipAdapter
	private lateinit var activity: MainActivity

	override fun onAttach(activity : Activity) {
		super.onAttach(activity)
		this.activity = activity as MainActivity
	}

	private val TAG = "SkipFragment"

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		_binding = SkipFragmentBinding.inflate(inflater , container , false)

		Log.d(TAG, "onCreateView: $viewModel")

		return binding.root
	}


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setRv()
	}

	private fun setRv() {
		binding.skipRv.layoutManager = LinearLayoutManager(activity)

		adapter = RecyclerViewSkipAdapter()

		val emptyView : View = layoutInflater.inflate( R.layout.layout_empty_view , binding.skipRv , false )
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

		binding.skipRv.addItemDecoration(
			DividerItemDecoration( binding.skipRv.context , DividerItemDecoration.VERTICAL)
		)

		// https://stackoverflow.com/a/38909958
		binding.skipRv.viewTreeObserver.addOnPreDrawListener (
			object : ViewTreeObserver.OnPreDrawListener {
				override fun onPreDraw(): Boolean {
					binding.skipRv.viewTreeObserver.removeOnPreDrawListener(this)
					for( i in 0 until binding.skipRv.childCount){
						binding.skipRv.getChildAt( i ).apply {
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

		binding.skipRv.addOnItemTouchListener(RecyclerItemClickListenr( context?.applicationContext!! ,
			binding.skipRv ,
			object : RecyclerItemClickListenr.OnItemClickListener{
				override fun onItemClick(view: View, position: Int) {
					activity.launchEditActivity( adapter.data[position] )
				}

				override fun onItemLongClick(view: View?, position: Int) {

				}
			}
		))

		val itemTouchHelper = ItemTouchHelper(CustomItemTouchHelperCallback.Builder().apply {
			flagEndHelper(ItemTouchHelper.END)
			iconSize(ICON_SIZE_DP.dpToPx * 1.3f)
			leftBackgroundColor( Color.parseColor("#ff6961") )
			leftIcon( convertDrawableToBitmap( ContextCompat.getDrawable( context?.applicationContext!!, R.drawable.ic_delete_black_24dp)!! ) )
			rightBackgroundColor(Color.parseColor( "#B33F40"))
			rightIcon( convertDrawableToBitmap( ContextCompat.getDrawable( context?.applicationContext!!, R.drawable.ic_delete_black_24dp)!! ) )
			onSwipeListener( object : CustomItemTouchHelperCallback.OnSwipeListener{
				override fun onSwipeLeftToRight(vh: RecyclerView.ViewHolder?) { object : RecyclerViewSkipAdapter.OnSwipeRight{
					override fun doWork(viewHolder: RecyclerView.ViewHolder) {
						val position = viewHolder.adapterPosition
						val T = adapter.data.removeAt( position )
						adapter.notifyItemRemoved( position )
						viewModel.delete( T )
					}
				}.doWork(vh!!) }

				override fun onSwipeRightToLeft(vh: RecyclerView.ViewHolder?) { }
			})
		}.build())
		itemTouchHelper.attachToRecyclerView( binding.skipRv )

		viewModel.data.observe(viewLifecycleOwner , {data ->
			if( data.isNullOrEmpty() ) {
				statesAdapter.state = StatesRecyclerViewAdapter.STATE_EMPTY
				return@observe
			}

			val undoneData = data.skippedTill( viewModel.getToday() , viewModel.getMSFromMidnight() )

			Log.d("DATA" , undoneData.toMutableList().toString())

			if( undoneData.isNullOrEmpty() ) {
				statesAdapter.state = StatesRecyclerViewAdapter.STATE_EMPTY
			}else {
				statesAdapter.state = StatesRecyclerViewAdapter.STATE_NORMAL

				adapter.set(
					undoneData.toMutableList(),
					viewModel.getToday()
				)
			}
		})
	}

}