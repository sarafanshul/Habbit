package com.projectdelta.habbit.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.projectdelta.habbit.R
import com.projectdelta.habbit.databinding.FragmentInsightsCalendarBinding
import com.projectdelta.habbit.viewModel.InsightsSharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InsightsCalendarFragment : Fragment() {

	companion object{
		fun newInstance() = InsightsCalendarFragment()
		private const val TAG = "InsightsCalendarFragment"
	}

	private var _binding : FragmentInsightsCalendarBinding ?= null
	private val binding
		get() = _binding!!

	private val viewModel : InsightsSharedViewModel by activityViewModels()

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {

		_binding = FragmentInsightsCalendarBinding.inflate(inflater , container , false )

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setLayout()
	}

	private fun setLayout() {

	}

}