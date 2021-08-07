package com.projectdelta.habbit.ui.base

import androidx.viewbinding.ViewBinding

abstract class BaseViewBindingActivity <VB : ViewBinding> : BaseActivity() {

    var _binding : VB?= null
    val binding : VB
        get() = _binding!!

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}