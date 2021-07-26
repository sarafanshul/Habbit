package com.projectdelta.habbit.ui.activity.editTask.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.projectdelta.habbit.ui.activity.editTask.state.EditTaskInteractionState.*
import com.projectdelta.habbit.ui.activity.editTask.state.CollapsingToolbarState.*


class EditTaskInteractionManager {

    private val _titleState : MutableLiveData< EditTaskInteractionState >
        = MutableLiveData( DefaultState() )

    private val _collapsingToolbarState : MutableLiveData< CollapsingToolbarState >
        = MutableLiveData( Expanded() )

    private val _bodyState : MutableLiveData< EditTaskInteractionState >
        = MutableLiveData( DefaultState() )

    val titleState : LiveData< EditTaskInteractionState >
        get() = _titleState

    val collapsingToolbarState : LiveData< CollapsingToolbarState >
        get() = _collapsingToolbarState

    val bodyState : LiveData< EditTaskInteractionState >
        get() = _bodyState


    fun setCollapsingToolbarState( state: CollapsingToolbarState ){
        if(state.toString() != _collapsingToolbarState.value.toString())
            _collapsingToolbarState.value = state
    }

    /**
     * Sets state of title and updates body state because
     * Both can not be in 'EditState' at the same time.
     */
    fun setNewTitleState( state: EditTaskInteractionState ){
        if(_titleState.toString() != state.toString()){
            _titleState.value = state
            when( state ){
                is EditState -> {
                    _bodyState.value = DefaultState()
                }
            }
        }
    }

    /**
     * Sets state of body and updates title state because
     * Both can not be in 'EditState' at the same time.
     */
    fun setNewBodyState( state: EditTaskInteractionState ){
        if( _bodyState.toString() != state.toString() ){
            _bodyState.value = state

            when( state ){
                is EditState -> {
                    _titleState.value = DefaultState()
                }
            }
        }
    }

    fun isEditingTitle() =  titleState.value.toString() == EditState().toString()

    fun isEditingBody() = bodyState.value.toString() == EditState().toString()

    fun exitEditState() {
        _titleState.value = DefaultState()
        _bodyState.value = DefaultState()
    }

    /**
     * Returns true if either title or body are in edit state
     */
    fun checkEditState() = isEditingTitle() || isEditingBody()

}