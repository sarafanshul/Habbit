package com.projectdelta.habbit.ui.edit.activity.state

sealed class CollapsingToolbarState {

	companion object {
		const val STATE_COLLAPSED = "Collapsed"
		const val STATE_EXPANDED = "Expanded"
	}

	class Collapsed : CollapsingToolbarState() {

		override fun toString(): String {
			return STATE_COLLAPSED
		}

	}

	class Expanded : CollapsingToolbarState() {

		override fun toString(): String {
			return STATE_EXPANDED
		}

	}

}
