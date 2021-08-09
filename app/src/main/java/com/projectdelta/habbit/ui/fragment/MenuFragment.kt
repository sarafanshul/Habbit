package com.projectdelta.habbit.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.projectdelta.habbit.R
import com.projectdelta.habbit.databinding.FragmentMenuBinding
import com.projectdelta.habbit.ui.base.BaseViewBindingFragment
import com.projectdelta.habbit.ui.viewModel.HomeSharedViewModel

class MenuFragment : BaseViewBindingFragment<FragmentMenuBinding>() {

	companion object{
		private const val TAG = "MenuFragment"
	}

	private val viewModel: HomeSharedViewModel by activityViewModels()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		_binding = FragmentMenuBinding.inflate(inflater , container , false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setLayout()
	}

	private fun setLayout() {

	}
}