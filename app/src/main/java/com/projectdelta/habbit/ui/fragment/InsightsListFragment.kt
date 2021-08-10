package com.projectdelta.habbit.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.projectdelta.habbit.R
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class InsightsListFragment : BaseViewBindingFragment<FragmentInsightsListBinding>() {
	companion object{
		fun newInstance() = InsightsListFragment()
		private const val TAG = "InsightsListFragment"
	}

	private lateinit var activity : InsightsActivity
	private lateinit var adapter : RecyclerViewListAdapter

	private var today = TimeUtil.getTodayFromEpoch()

	private var job : Job?= null

	private val viewModel : InsightsSharedViewModel by activityViewModels()

	private lateinit var statesAdapter : StatesRecyclerViewAdapter

	override fun onAttach(activity: Activity) {
		super.onAttach(activity)
		this.activity = activity as InsightsActivity
	}

	override fun onStart() {
		super.onStart()
		// bug (empty layout because today is not changed and view is refreshed)
		today = TimeUtil.getTodayFromEpoch()
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {

		_binding = FragmentInsightsListBinding.inflate(inflater , container , false )

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setLayout()
	}

	private fun setLayout() {
		val linearLayoutManager = LinearLayoutManager(activity)
		binding.insightsListRv.layoutManager = linearLayoutManager
		adapter = RecyclerViewListAdapter()

		val emptyView : View = layoutInflater.inflate( R.layout.layout_empty_view , binding.insightsListRv , false )
		emptyView.findViewById<MaterialTextView>(R.id.empty_view_tw_string).text = NotFound.get()

		statesAdapter =
            StatesRecyclerViewAdapter(
                adapter,
                emptyView,
                emptyView,
                emptyView
            )
		binding.insightsListRv.adapter = statesAdapter
		statesAdapter.state = StatesRecyclerViewAdapter.STATE_EMPTY

		binding.insightsListRv.addItemDecoration(
			DividerItemDecoration( binding.insightsListRv.context , DividerItemDecoration.VERTICAL)
		)

		// https://stackoverflow.com/a/38909958
		binding.insightsListRv.viewTreeObserver.addOnPreDrawListener (
			object : ViewTreeObserver.OnPreDrawListener {
				override fun onPreDraw(): Boolean {
					binding.insightsListRv.viewTreeObserver.removeOnPreDrawListener(this)
					for( i in 0 until binding.insightsListRv.childCount){
						binding.insightsListRv.getChildAt( i ).apply {
							alpha = 0.0f
							animate().apply {
								alpha(1.0f)
								duration = 300
								startDelay = i*50L
								start()
							}
						}
					}
					return true
				}
			}
		)

		binding.insightsListRv.addOnScrollListener( object : EndlessRecyclerViewScrollListener(linearLayoutManager){
			override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
				Log.d(TAG, "onLoadMore: $totalItemsCount, now calling addData")
				addData()
			}
		})

		binding.insightsListRv.addOnItemTouchListener( RecyclerItemClickListenr(
			context?.applicationContext!!,
			binding.insightsListRv,
			object: RecyclerItemClickListenr.OnItemClickListener{
				override fun onItemClick(view: View, position: Int) {
					if( adapter.dataIsInitialized() ) {
						val title = when( adapter.data[position].tasksTitle.size ){
							1 -> "${adapter.data[position].tasksTitle.size} task completed on ${TimeUtil.getPastDateFromOffset((TimeUtil.getTodayFromEpoch() - adapter.data[position].id).toInt())}"
							else -> "${adapter.data[position].tasksTitle.size} tasks completed on ${TimeUtil.getPastDateFromOffset((TimeUtil.getTodayFromEpoch() - adapter.data[position].id).toInt())}"
						}
						val message = adapter.data[position].titlesToBulletList()
						MaterialAlertDialogBuilder(activity).apply {
							setTitle(title)
							setMessage(message)
							create()
						}.show()
					}
				}

				override fun onItemLongClick(view: View?, position: Int) {}
			} ))

	}

	fun addData(){
		job = lifecycleScope.launch {
			val addData = viewModel.getDayRange(today - 10 , today)
			Log.d(TAG, "addData: $today , ${addData.toString()}")
			today -= 11
			if( ! addData.isNullOrEmpty() ) {
				if( statesAdapter.state != StatesRecyclerViewAdapter.STATE_NORMAL )
					statesAdapter.state = StatesRecyclerViewAdapter.STATE_NORMAL
				adapter.addAll(addData.sortedBy { -it.id }.toMutableList())
			}
		}
	}

	override fun onDestroy() {
		job?.cancel()
		super.onDestroy()
	}
}