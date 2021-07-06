package com.projectdelta.habbit.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textview.MaterialTextView
import com.projectdelta.habbit.ui.MainActivity
import com.projectdelta.habbit.R
import com.projectdelta.habbit.adapter.RecyclerViewDoneAdapter
import com.projectdelta.habbit.databinding.DoneFragmentBinding
import com.projectdelta.habbit.util.NotFound
import com.projectdelta.habbit.util.view.RecyclerItemClickListenr
import com.projectdelta.habbit.util.view.StatesRecyclerViewAdapter
import com.projectdelta.habbit.util.lang.completedTill
import com.projectdelta.habbit.viewModel.HomeSharedViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DoneFragment : Fragment() {

	companion object {
		fun newInstance() = DoneFragment()
		private const val TAG = "DoneFragment"
	}

	private val viewModel: HomeSharedViewModel by activityViewModels()
	private var _binding : DoneFragmentBinding ?= null
	private val binding
		get() = _binding!!

	lateinit var adapter : RecyclerViewDoneAdapter

	private lateinit var activity: MainActivity

	override fun onAttach(activity : Activity) {
		super.onAttach(activity)
		this.activity = activity as MainActivity
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		_binding = DoneFragmentBinding.inflate(inflater , container , false)

		Log.d(TAG, "onCreateView: $viewModel")

		return binding.root
	}


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setRv()
	}

	private fun setRv() {
		binding.doneRv.layoutManager = LinearLayoutManager( activity )
		adapter = RecyclerViewDoneAdapter()

		val emptyView : View = layoutInflater.inflate( R.layout.layout_empty_view , binding.doneRv , false )
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

		binding.doneRv.addItemDecoration(
			DividerItemDecoration( binding.doneRv.context , DividerItemDecoration.VERTICAL)
		)

		// https://stackoverflow.com/a/38909958
		binding.doneRv.viewTreeObserver.addOnPreDrawListener (
			object :ViewTreeObserver.OnPreDrawListener {
				override fun onPreDraw(): Boolean {
					binding.doneRv.viewTreeObserver.removeOnPreDrawListener(this)
					for( i in 0 until binding.doneRv.childCount){
						binding.doneRv.getChildAt( i ).apply {
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

		binding.doneRv.addOnItemTouchListener(RecyclerItemClickListenr( context?.applicationContext!! ,
			binding.doneRv ,
			object : RecyclerItemClickListenr.OnItemClickListener{
				override fun onItemClick(view: View, position: Int) {
					activity.launchEditActivity( adapter.data[position] )
				}

				override fun onItemLongClick(view: View?, position: Int) {

				}
			}
		))

		viewModel.data.observe(viewLifecycleOwner , {data ->
			if( data.isNullOrEmpty() ) {
				statesAdapter.state = StatesRecyclerViewAdapter.STATE_EMPTY
				return@observe
			}

			val doneData = data.completedTill( viewModel.getToday() )

			Log.d("DATA" , doneData.toMutableList().toString())

			if( doneData.isNullOrEmpty() ){
				statesAdapter.state = StatesRecyclerViewAdapter.STATE_EMPTY
			}else {
				statesAdapter.state = StatesRecyclerViewAdapter.STATE_NORMAL
				adapter.set(
					doneData,
					viewModel.getToday()
				)
			}
		})
	}

}