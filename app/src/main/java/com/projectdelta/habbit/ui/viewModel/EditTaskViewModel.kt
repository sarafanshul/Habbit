package com.projectdelta.habbit.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.projectdelta.habbit.data.model.entities.Task
import com.projectdelta.habbit.data.repository.TasksRepositoryImpl
import com.projectdelta.habbit.ui.activity.editTask.state.CollapsingToolbarState
import com.projectdelta.habbit.ui.activity.editTask.state.EditTaskInteractionManager
import com.projectdelta.habbit.ui.activity.editTask.state.EditTaskInteractionState
import com.projectdelta.habbit.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
	private val repository: TasksRepositoryImpl
) : BaseViewModel(){

	private val editTaskInteractionManager : EditTaskInteractionManager = EditTaskInteractionManager()

	val titleInteractionState : LiveData< EditTaskInteractionState >
		get() = editTaskInteractionManager.titleState

	val bodyInteractionState : LiveData< EditTaskInteractionState >
		get() = editTaskInteractionManager.bodyState

	val collapsingToolbarState : LiveData< CollapsingToolbarState >
		get() = editTaskInteractionManager.collapsingToolbarState


	fun getTaskById( id : Long ) : Task = repository.getTaskById( id )

	fun getTaskByIdLive( id : Long ) = repository.getTaskByIdLive( id )

	fun updateTask( task: Task ) = viewModelScope.launch(Dispatchers.IO) {
		repository.insertTask( task )
	}

	fun delete( task: Task ) = viewModelScope.launch (Dispatchers.IO){
		repository.deleteTask( task )
	}

	fun setCollapsingToolbarState( state: CollapsingToolbarState ){
		editTaskInteractionManager.setCollapsingToolbarState( state )
	}

	fun setInteractionTitleState( state : EditTaskInteractionState ){
		editTaskInteractionManager.setNewTitleState( state )
	}

	fun setInteractionBodyState( state: EditTaskInteractionState ){
		editTaskInteractionManager.setNewBodyState( state )
	}

	fun isToolbarCollapsed() = collapsingToolbarState.toString() == CollapsingToolbarState.STATE_COLLAPSED

	fun isToolbarExpanded() = collapsingToolbarState.toString() == CollapsingToolbarState.STATE_EXPANDED

	fun checkEditState() = editTaskInteractionManager.checkEditState()

	fun exitEditState() = editTaskInteractionManager.exitEditState()

	fun isEditingTitle() = editTaskInteractionManager.isEditingTitle()

	fun isEditingBody() = editTaskInteractionManager.isEditingBody()


}